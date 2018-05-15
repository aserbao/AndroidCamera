package com.aserbao.androidcustomcamera.whole.record.encoder;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGLContext;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.aserbao.androidcustomcamera.base.MyApplication;
import com.aserbao.androidcustomcamera.whole.record.encoder.gles.EglCore;
import com.aserbao.androidcustomcamera.whole.record.filters.BaseFilter;
import com.aserbao.androidcustomcamera.whole.record.filters.NoneFilter;
import com.aserbao.androidcustomcamera.whole.record.filters.gpuFilters.baseFilter.GPUImageFilter;
import com.aserbao.androidcustomcamera.whole.record.filters.gpuFilters.baseFilter.MagicCameraInputFilter;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.FloatBuffer;

/**
 * description:
 * Created by aserbao on 2018/5/15.
 */

public class TextureMovieEncoder implements Runnable {
        private static final String TAG = "";
        private static final boolean VERBOSE = false;

        private static final int MSG_START_RECORDING = 0;
        private static final int MSG_STOP_RECORDING = 1;
        private static final int MSG_FRAME_AVAILABLE = 2;
        private static final int MSG_SET_TEXTURE_ID = 3;
        private static final int MSG_UPDATE_SHARED_CONTEXT = 4;
        private static final int MSG_QUIT = 5;
        private static final int MSG_PAUSE=6;
        private static final int MSG_RESUME=7;

        // ----- accessed exclusively by encoder thread -----
        private WindowSurface mInputWindowSurface;
        private EglCore mEglCore;
        private MagicCameraInputFilter mInput;
        private int mTextureId;

        private VideoEncoderCore mVideoEncoder;

        // ----- accessed by multiple threads -----
        private volatile EncoderHandler mHandler;

        private Object mReadyFence = new Object();      // guards ready/running
        private boolean mReady;
        private boolean mRunning;
        private GPUImageFilter filter;
        private FloatBuffer gLCubeBuffer;
        private FloatBuffer gLTextureBuffer;
        private long baseTimeStamp=-1;//第一帧的时间戳

        public TextureMovieEncoder() {

        }

        /**
         * Encoder configuration.
         * <p>
         * Object is immutable, which means we can safely pass it between threads without
         * explicit synchronization (and don't need to worry about it getting tweaked out from
         * under us).
         * <p>
         * TODO: make frame rate and iframe interval configurable?  Maybe use builder pattern
         *       with reasonable defaults for those and bit rate.
         */
        public static class EncoderConfig {
            final String path;
            final int mWidth;
            final int mHeight;
            final int mBitRate;
            final EGLContext mEglContext;

            public EncoderConfig(String path, int width, int height, int bitRate,
                                 EGLContext sharedEglContext, Camera.CameraInfo info) {
                this.path = path;
                mWidth = width;
                mHeight = height;
                mBitRate = bitRate;
                mEglContext = sharedEglContext;
            }

            @Override
            public String toString() {
                return "EncoderConfig: " + mWidth + "x" + mHeight + " @" + mBitRate +
                        " to '" + path + "' ctxt=" + mEglContext;
            }
        }

        /**
         * Tells the video recorder to start recording.  (Call from non-encoder thread.)
         * <p>
         * Creates a new thread, which will create an encoder using the provided configuration.
         * <p>
         * Returns after the recorder thread has started and is ready to accept Messages.  The
         * encoder may not yet be fully configured.
         */
        public void startRecording(EncoderConfig config) {
            Log.d(TAG, "Encoder: startRecording()");
            synchronized (mReadyFence) {
                if (mRunning) {
                    Log.w(TAG, "Encoder thread already running");
                    return;
                }
                mRunning = true;
                new Thread(this, "TextureMovieEncoder").start();
                while (!mReady) {
                    try {
                        mReadyFence.wait();
                    } catch (InterruptedException ie) {
                        // ignore
                    }
                }
            }

            mHandler.sendMessage(mHandler.obtainMessage(MSG_START_RECORDING, config));
        }

        /**
         * Tells the video recorder to stop recording.  (Call from non-encoder thread.)
         * <p>
         * Returns immediately; the encoder/muxer may not yet be finished creating the movie.
         * <p>
         * TODO: have the encoder thread invoke a callback on the UI thread just before it shuts down
         * so we can provide reasonable status UI (and let the caller know that movie encoding
         * has completed).
         */
        public void stopRecording() {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_STOP_RECORDING));
            mHandler.sendMessage(mHandler.obtainMessage(MSG_QUIT));
            // We don't know when these will actually finish (or even start).  We don't want to
            // delay the UI thread though, so we return immediately.
        }
        public void pauseRecording(){
            mHandler.sendMessage(mHandler.obtainMessage(MSG_PAUSE));
        }
        public void resumeRecording(){
            mHandler.sendMessage(mHandler.obtainMessage(MSG_RESUME));
        }
        /**
         * Returns true if recording has been started.
         */
        public boolean isRecording() {
            synchronized (mReadyFence) {
                return mRunning;
            }
        }

        /**
         * Tells the video recorder to refresh its EGL surface.  (Call from non-encoder thread.)
         */
        public void updateSharedContext(EGLContext sharedContext) {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_SHARED_CONTEXT, sharedContext));
        }

        /**
         * Tells the video recorder that a new frame is available.  (Call from non-encoder thread.)
         * <p>
         * This function sends a message and returns immediately.  This isn't sufficient -- we
         * don't want the caller to latch a new frame until we're done with this one -- but we
         * can get away with it so long as the input frame rate is reasonable and the encoder
         * thread doesn't stall.
         * <p>
         * TODO: either block here until the texture has been rendered onto the encoder surface,
         * or have a separate "block if still busy" method that the caller can execute immediately
         * before it calls updateTexImage().  The latter is preferred because we don't want to
         * stall the caller while this thread does work.
         */
        public void frameAvailable(SurfaceTexture st) {
            synchronized (mReadyFence) {
                if (!mReady) {
                    return;
                }
            }

            float[] transform = new float[16];      // TODO - avoid alloc every frame
            st.getTransformMatrix(transform);
            long timestamp = st.getTimestamp();
            if (timestamp == 0) {
                // Seeing this after device is toggled off/on with power button.  The
                // first frame back has a zero timestamp.
                //
                // MPEG4Writer thinks this is cause to abort() in native code, so it's very
                // important that we just ignore the frame.
                Log.w(TAG, "HEY: got SurfaceTexture with timestamp of zero");
                return;
            }

            mHandler.sendMessage(mHandler.obtainMessage(MSG_FRAME_AVAILABLE,
                    (int) (timestamp >> 32), (int) timestamp, transform));
        }

        /**
         * Tells the video recorder what texture name to use.  This is the external texture that
         * we're receiving camera previews in.  (Call from non-encoder thread.)
         * <p>
         * TODO: do something less clumsy
         */
        public void setTextureId(int id) {
            synchronized (mReadyFence) {
                if (!mReady) {
                    return;
                }
            }
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TEXTURE_ID, id, 0, null));
        }

        /**
         * Encoder thread entry point.  Establishes Looper/Handler and waits for messages.
         * <p>
         * @see Thread#run()
         */
        @Override
        public void run() {
            // Establish a Looper for this thread, and define a Handler for it.
            Looper.prepare();
            synchronized (mReadyFence) {
                mHandler = new EncoderHandler(this);
                mReady = true;
                mReadyFence.notify();
            }
            Looper.loop();

            Log.d(TAG, "Encoder thread exiting");
            synchronized (mReadyFence) {
                mReady = mRunning = false;
                mHandler = null;
            }
        }


        /**
         * Handles encoder state change requests.  The handler is created on the encoder thread.
         */
        private static class EncoderHandler extends Handler {
            private WeakReference<TextureMovieEncoder> mWeakEncoder;

            public EncoderHandler(TextureMovieEncoder encoder) {
                mWeakEncoder = new WeakReference<TextureMovieEncoder>(encoder);
            }

            @Override  // runs on encoder thread
            public void handleMessage(Message inputMessage) {
                int what = inputMessage.what;
                Object obj = inputMessage.obj;

                TextureMovieEncoder encoder = mWeakEncoder.get();
                if (encoder == null) {
                    Log.w(TAG, "EncoderHandler.handleMessage: encoder is null");
                    return;
                }

                switch (what) {
                    case MSG_START_RECORDING:
                        encoder.handleStartRecording((EncoderConfig) obj);
                        break;
                    case MSG_STOP_RECORDING:
                        encoder.handleStopRecording();
                        break;
                    case MSG_FRAME_AVAILABLE:
                        long timestamp = (((long) inputMessage.arg1) << 32) |
                                (((long) inputMessage.arg2) & 0xffffffffL);
                        encoder.handleFrameAvailable((float[]) obj, timestamp);
                        break;
                    case MSG_SET_TEXTURE_ID:
                        encoder.handleSetTexture(inputMessage.arg1);
                        break;
                    case MSG_UPDATE_SHARED_CONTEXT:
                        encoder.handleUpdateSharedContext((EGLContext) inputMessage.obj);
                        break;
                    case MSG_QUIT:
                        Looper.myLooper().quit();
                        break;
                    case MSG_PAUSE:
                        encoder.handlePauseRecording();
                        break;
                    case MSG_RESUME:
                        encoder.handleResumeRecording();
                        break;
                    default:
                        throw new RuntimeException("Unhandled msg what=" + what);
                }
            }
        }

        /**
         * Starts recording.
         */
        private void handleStartRecording(EncoderConfig config) {
            Log.d(TAG, "handleStartRecording " + config);
            prepareEncoder(config.mEglContext, config.mWidth, config.mHeight, config.mBitRate,
                    config.path);
        }

        /**
         * Handles notification of an available frame.
         * <p>
         * The texture is rendered onto the encoder's input surface, along with a moving
         * box (just because we can).
         * <p>
         * @param transform The texture transform, from SurfaceTexture.
         * @param timestampNanos The frame's timestamp, from SurfaceTexture.
         */
        private void handleFrameAvailable(float[] transform, long timestampNanos) {
            if (VERBOSE) Log.d(TAG, "handleFrameAvailable tr=" + transform);
            mVideoEncoder.drainEncoder(false);
            Log.e("hero","---setTextureId=="+mTextureId);
            mShowFilter.setTextureId(mTextureId);
            mShowFilter.draw();
            if(baseTimeStamp==-1){
                baseTimeStamp=System.nanoTime();
                mVideoEncoder.startRecord();
            }
            long nano=System.nanoTime();
            long time=nano-baseTimeStamp-pauseDelayTime;
            System.out.println("TimeStampVideo="+time+";nanoTime="+nano+";baseTimeStamp="+baseTimeStamp+";pauseDelay="+pauseDelayTime);
            mInputWindowSurface.setPresentationTime(time);
            mInputWindowSurface.swapBuffers();
        }
        long pauseDelayTime;
        long onceDelayTime;
        private void handlePauseRecording(){
            onceDelayTime=System.nanoTime();
            mVideoEncoder.pauseRecording();
        }
        private void handleResumeRecording(){
            onceDelayTime=System.nanoTime()-onceDelayTime;
            pauseDelayTime+=onceDelayTime;
            mVideoEncoder.resumeRecording();
        }
        /**
         * Handles a request to stop encoding.
         */
        private void handleStopRecording() {
            Log.d(TAG, "handleStopRecording");
            mVideoEncoder.drainEncoder(true);
            mVideoEncoder.stopAudRecord();
            releaseEncoder();
        }

        /**
         * Sets the texture name that SurfaceTexture will use when frames are received.
         */
        private void handleSetTexture(int id) {
            //Log.d(TAG, "handleSetTexture " + id);
            mTextureId = id;
        }

        /**
         * Tears down the EGL surface and context we've been using to feed the MediaCodec input
         * surface, and replaces it with a new one that shares with the new context.
         * <p>
         * This is useful if the old context we were sharing with went away (maybe a GLSurfaceView
         * that got torn down) and we need to hook up with the new one.
         */
        private void handleUpdateSharedContext(EGLContext newSharedContext) {
            Log.d(TAG, "handleUpdatedSharedContext " + newSharedContext);

            // Release the EGLSurface and EGLContext.
            mInputWindowSurface.releaseEglSurface();
            mInput.destroy();
            mEglCore.release();

            // Create a new EGLContext and recreate the window surface.
            mEglCore = new EglCore(newSharedContext, EglCore.FLAG_RECORDABLE);
            mInputWindowSurface.recreate(mEglCore);
            mInputWindowSurface.makeCurrent();

            // Create new programs and such for the new context.
            mInput = new MagicCameraInputFilter();
            mInput.init();
            filter =null;
            if(filter != null){
                filter.init();
                filter.onInputSizeChanged(mPreviewWidth, mPreviewHeight);
                filter.onDisplaySizeChanged(mVideoWidth, mVideoHeight);
            }
        }

        private void prepareEncoder(EGLContext sharedContext, int width, int height, int bitRate,
                                    String path) {
            try {
                mVideoEncoder = new VideoEncoderCore(width, height, bitRate, path);
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
            mVideoWidth = width;
            mVideoHeight = height;
            mEglCore = new EglCore(sharedContext, EglCore.FLAG_RECORDABLE);
            mInputWindowSurface = new WindowSurface(mEglCore, mVideoEncoder.getInputSurface(), true);
            mInputWindowSurface.makeCurrent();

            mInput = new MagicCameraInputFilter();
            mInput.init();
            filter = null;
            if(filter != null){
                filter.init();
                filter.onInputSizeChanged(mPreviewWidth, mPreviewHeight);
                filter.onDisplaySizeChanged(mVideoWidth, mVideoHeight);
            }
            mShowFilter.create();
            baseTimeStamp=-1;
        }

        private void releaseEncoder() {
            mVideoEncoder.release();
            if (mInputWindowSurface != null) {
                mInputWindowSurface.release();
                mInputWindowSurface = null;
            }
            if (mInput != null) {
                mInput.destroy();
                mInput = null;
            }
            if (mEglCore != null) {
                mEglCore.release();
                mEglCore = null;
            }
            if(filter != null){
                filter.destroy();
                filter = null;
//            type = MagicFilterType.NONE;
            }
        }
        //    private MagicFilterType type = MagicFilterType.NONE;
        private BaseFilter mShowFilter=new NoneFilter(MyApplication.getContext().getResources());
//    public void setFilter(MagicFilterType type) {
//        this.type = type;
//    }

        private int mPreviewWidth = -1;
        private int mPreviewHeight = -1;
        private int mVideoWidth = -1;
        private int mVideoHeight = -1;

        public void setPreviewSize(int width, int height){
            mPreviewWidth = width;
            mPreviewHeight = height;
        }

        public void setTextureBuffer(FloatBuffer gLTextureBuffer) {
            this.gLTextureBuffer = gLTextureBuffer;
        }

        public void setCubeBuffer(FloatBuffer gLCubeBuffer) {
            this.gLCubeBuffer = gLCubeBuffer;
        }

}