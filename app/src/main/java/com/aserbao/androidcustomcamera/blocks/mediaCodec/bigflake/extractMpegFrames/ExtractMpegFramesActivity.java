package com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake.extractMpegFrames;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.test.AndroidTestCase;
import android.util.Log;
import android.view.Surface;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake.BigFlakeBaseActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import static junit.framework.Assert.fail;

/**
 * 功能:从.mp4文件中提取前n(10)帧视频，并将其保存到单个PNG文件中/sdcard/。
 * author aserbao
 * date : On 2018/11/23
 * email: 1142803753@qq.com
 */
public class ExtractMpegFramesActivity extends BigFlakeBaseActivity {

    @Override
    public void excute() throws Throwable {
        extractMpegFrames();
    }
    /*
     * Copyright 2013 The Android Open Source Project
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *      http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */

//20131122: minor tweaks to saveFrame() I/O
//20131205: add alpha to EGLConfig (huge glReadPixels speedup); pre-allocate pixel buffers;
//          log time to run saveFrame()
//20131210: switch from EGL14 to EGL10 for API 16 compatibility
//20140123: correct error checks on glGet*Location() and program creation (they don't set error)
//20140212: eliminate byte swap
    /**
     * Extract frames from an MP4 using MediaExtractor, MediaCodec, and GLES.  Put a .mp4 file
     * in "/sdcard/source.mp4" and look for output files named "/sdcard/frame-XX.png".
     * <p>
     * This uses various features first available in Android "Jellybean" 4.1 (API 16).
     * <p>
     * (This was derived from bits and pieces of CTS tests, and is packaged as such, but is not
     * currently part of CTS.)
     */
        private static final String TAG = "ExtractMpegFramesTest";
        private static final boolean VERBOSE = false;           // lots of logging

        // where to find files (note: requires WRITE_EXTERNAL_STORAGE permission)
        private static final File FILES_DIR = Environment.getExternalStorageDirectory();
        private static final int MAX_FRAMES = 10;       // stop extracting after this many

        /** test entry point */
        public void testExtractMpegFrames() throws Throwable {
            ExtractMpegFramesWrapper.runTest(this);
        }



    /**
         * Wraps extractMpegFrames().  This is necessary because SurfaceTexture will try to use
         * the looper in the current thread if one exists, and the CTS tests create one on the
         * test thread.
         *
         * The wrapper propagates exceptions thrown by the worker thread back to the caller.
         */
        private static class ExtractMpegFramesWrapper implements Runnable {
            private Throwable mThrowable;
            private ExtractMpegFramesActivity mTest;

            private ExtractMpegFramesWrapper(ExtractMpegFramesActivity test) {
                mTest = test;
            }

            @Override
            public void run() {
                try {
                    mTest.extractMpegFrames();
                } catch (Throwable th) {
                    mThrowable = th;
                }
            }

            /** Entry point. */
            public static void runTest(ExtractMpegFramesActivity obj) throws Throwable {
                ExtractMpegFramesWrapper wrapper = new ExtractMpegFramesWrapper(obj);
                Thread th = new Thread(wrapper, "codec test");
                th.start();
                th.join();
                if (wrapper.mThrowable != null) {
                    throw wrapper.mThrowable;
                }
            }
        }

        /**
         * Tests extraction from an MP4 to a series of PNG files.
         * <p>
         * We scale the video to 640x480 for the PNG just to demonstrate that we can scale the
         * video with the GPU.  If the input video has a different aspect ratio, we could preserve
         * it by adjusting the GL viewport to get letterboxing or pillarboxing, but generally if
         * you're extracting frames you don't want black bars.
         */
        private void extractMpegFrames() throws IOException {
            MediaCodec decoder = null;
            CodecOutputSurface outputSurface = null;
            MediaExtractor extractor = null;
            int saveWidth = 640;
            int saveHeight = 480;

            try {
                extractor = new MediaExtractor();

                AssetFileDescriptor srcFd = getResources().openRawResourceFd(R.raw.video_480x360_mp4_h264_500kbps_30fps_aac_stereo_128kbps_44100hz);
                extractor = new MediaExtractor();
                extractor.setDataSource(srcFd.getFileDescriptor(), srcFd.getStartOffset(),
                        srcFd.getLength());
                int trackIndex = selectTrack(extractor);
                extractor.selectTrack(trackIndex);

                MediaFormat format = extractor.getTrackFormat(trackIndex);
                if (VERBOSE) {
                    Log.d(TAG, "Video size is " + format.getInteger(MediaFormat.KEY_WIDTH) + "x" +
                            format.getInteger(MediaFormat.KEY_HEIGHT));
                }

                // Could use width/height from the MediaFormat to get full-size frames.
                outputSurface = new CodecOutputSurface(saveWidth, saveHeight);

                // Create a MediaCodec decoder, and configure it with the MediaFormat from the
                // extractor.  It's very important to use the format from the extractor because
                // it contains a copy of the CSD-0/CSD-1 codec-specific data chunks.
                String mime = format.getString(MediaFormat.KEY_MIME);
                decoder = MediaCodec.createDecoderByType(mime);
                decoder.configure(format, outputSurface.getSurface(), null, 0);
                decoder.start();

                doExtract(extractor, trackIndex, decoder, outputSurface);
            } finally {
                // release everything we grabbed
                if (outputSurface != null) {
                    outputSurface.release();
                    outputSurface = null;
                }
                if (decoder != null) {
                    decoder.stop();
                    decoder.release();
                    decoder = null;
                }
                if (extractor != null) {
                    extractor.release();
                    extractor = null;
                }
            }
        }

        /**
         * Selects the video track, if any.
         *
         * @return the track index, or -1 if no video track is found.
         */
        private int selectTrack(MediaExtractor extractor) {
            // Select the first video track we find, ignore the rest.
            int numTracks = extractor.getTrackCount();
            for (int i = 0; i < numTracks; i++) {
                MediaFormat format = extractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("video/")) {
                    if (VERBOSE) {
                        Log.d(TAG, "Extractor selected track " + i + " (" + mime + "): " + format);
                    }
                    return i;
                }
            }

            return -1;
        }

        /**
         * Work loop.
         */
        static void doExtract(MediaExtractor extractor, int trackIndex, MediaCodec decoder,
                              CodecOutputSurface outputSurface) throws IOException {
            final int TIMEOUT_USEC = 10000;
            ByteBuffer[] decoderInputBuffers = decoder.getInputBuffers();
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            int inputChunk = 0;
            int decodeCount = 0;
            long frameSaveTime = 0;

            boolean outputDone = false;
            boolean inputDone = false;
            while (!outputDone) {
                if (VERBOSE) Log.d(TAG, "loop");

                // Feed more data to the decoder.
                if (!inputDone) {
                    int inputBufIndex = decoder.dequeueInputBuffer(TIMEOUT_USEC);
                    if (inputBufIndex >= 0) {
                        ByteBuffer inputBuf = decoderInputBuffers[inputBufIndex];
                        // Read the sample data into the ByteBuffer.  This neither respects nor
                        // updates inputBuf's position, limit, etc.
                        int chunkSize = extractor.readSampleData(inputBuf, 0);
                        if (chunkSize < 0) {
                            // End of stream -- send empty frame with EOS flag set.
                            decoder.queueInputBuffer(inputBufIndex, 0, 0, 0L,
                                    MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            inputDone = true;
                            if (VERBOSE) Log.d(TAG, "sent input EOS");
                        } else {
                            if (extractor.getSampleTrackIndex() != trackIndex) {
                                Log.w(TAG, "WEIRD: got sample from track " +
                                        extractor.getSampleTrackIndex() + ", expected " + trackIndex);
                            }
                            long presentationTimeUs = extractor.getSampleTime();
                            decoder.queueInputBuffer(inputBufIndex, 0, chunkSize,
                                    presentationTimeUs, 0 /*flags*/);
                            if (VERBOSE) {
                                Log.d(TAG, "submitted frame " + inputChunk + " to dec, size=" +
                                        chunkSize);
                            }
                            inputChunk++;
                            extractor.advance();
                        }
                    } else {
                        if (VERBOSE) Log.d(TAG, "input buffer not available");
                    }
                }

                if (!outputDone) {
                    int decoderStatus = decoder.dequeueOutputBuffer(info, TIMEOUT_USEC);
                    if (decoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        // no output available yet
                        if (VERBOSE) Log.d(TAG, "no output from decoder available");
                    } else if (decoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        // not important for us, since we're using Surface
                        if (VERBOSE) Log.d(TAG, "decoder output buffers changed");
                    } else if (decoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        MediaFormat newFormat = decoder.getOutputFormat();
                        if (VERBOSE) Log.d(TAG, "decoder output format changed: " + newFormat);
                    } else if (decoderStatus < 0) {
                        fail("unexpected result from decoder.dequeueOutputBuffer: " + decoderStatus);
                    } else { // decoderStatus >= 0
                        if (VERBOSE) Log.d(TAG, "surface decoder given buffer " + decoderStatus +
                                " (size=" + info.size + ")");
                        if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            if (VERBOSE) Log.d(TAG, "output EOS");
                            outputDone = true;
                        }

                        boolean doRender = (info.size != 0);

                        // As soon as we call releaseOutputBuffer, the buffer will be forwarded
                        // to SurfaceTexture to convert to a texture.  The API doesn't guarantee
                        // that the texture will be available before the call returns, so we
                        // need to wait for the onFrameAvailable callback to fire.
                        decoder.releaseOutputBuffer(decoderStatus, doRender);
                        if (doRender) {
                            if (VERBOSE) Log.d(TAG, "awaiting decode of frame " + decodeCount);
                            outputSurface.awaitNewImage();
                            outputSurface.drawImage(true);

                            if (decodeCount < MAX_FRAMES) {
                                File outputFile = new File(FILES_DIR,
                                        String.format("frame-%02d.png", decodeCount));
                                long startWhen = System.nanoTime();
                                outputSurface.saveFrame(outputFile.toString());
                                frameSaveTime += System.nanoTime() - startWhen;
                            }
                            if (decodeCount == MAX_FRAMES){

                            }
                            decodeCount++;
                        }
                    }
                }
            }

            int numSaved = (MAX_FRAMES < decodeCount) ? MAX_FRAMES : decodeCount;
            Log.d(TAG, "Saving " + numSaved + " frames took " +
                    (frameSaveTime / numSaved / 1000) + " us per frame");
        }


        /**
         * Holds state associated with a Surface used for MediaCodec decoder output.
         * <p>
         * The constructor for this class will prepare GL, create a SurfaceTexture,
         * and then create a Surface for that SurfaceTexture.  The Surface can be passed to
         * MediaCodec.configure() to receive decoder output.  When a frame arrives, we latch the
         * texture with updateTexImage(), then render the texture with GL to a pbuffer.
         * <p>
         * By default, the Surface will be using a BufferQueue in asynchronous mode, so we
         * can potentially drop frames.
         */
        private static class CodecOutputSurface
                implements SurfaceTexture.OnFrameAvailableListener {
            private STextureRender mTextureRender;
            private SurfaceTexture mSurfaceTexture;
            private Surface mSurface;
            private EGL10 mEgl;

            private EGLDisplay mEGLDisplay = EGL10.EGL_NO_DISPLAY;
            private EGLContext mEGLContext = EGL10.EGL_NO_CONTEXT;
            private EGLSurface mEGLSurface = EGL10.EGL_NO_SURFACE;
            int mWidth;
            int mHeight;

            private Object mFrameSyncObject = new Object();     // guards mFrameAvailable
            private boolean mFrameAvailable;

            private ByteBuffer mPixelBuf;                       // used by saveFrame()

            /**
             * Creates a CodecOutputSurface backed by a pbuffer with the specified dimensions.  The
             * new EGL context and surface will be made current.  Creates a Surface that can be passed
             * to MediaCodec.configure().
             */
            public CodecOutputSurface(int width, int height) {
                if (width <= 0 || height <= 0) {
                    throw new IllegalArgumentException();
                }
                mEgl = (EGL10) EGLContext.getEGL();
                mWidth = width;
                mHeight = height;

                eglSetup();
                makeCurrent();
                setup();
            }

            /**
             * Creates interconnected instances of TextureRender, SurfaceTexture, and Surface.
             */
            private void setup() {
                mTextureRender = new STextureRender();
                mTextureRender.surfaceCreated();

                if (VERBOSE) Log.d(TAG, "textureID=" + mTextureRender.getTextureId());
                mSurfaceTexture = new SurfaceTexture(mTextureRender.getTextureId());

                // This doesn't work if this object is created on the thread that CTS started for
                // these test cases.
                //
                // The CTS-created thread has a Looper, and the SurfaceTexture constructor will
                // create a Handler that uses it.  The "frame available" message is delivered
                // there, but since we're not a Looper-based thread we'll never see it.  For
                // this to do anything useful, CodecOutputSurface must be created on a thread without
                // a Looper, so that SurfaceTexture uses the main application Looper instead.
                //
                // Java language note: passing "this" out of a constructor is generally unwise,
                // but we should be able to get away with it here.
                mSurfaceTexture.setOnFrameAvailableListener(this);

                mSurface = new Surface(mSurfaceTexture);

                mPixelBuf = ByteBuffer.allocateDirect(mWidth * mHeight * 4);
                mPixelBuf.order(ByteOrder.LITTLE_ENDIAN);
            }

            /**
             * Prepares EGL.  We want a GLES 2.0 context and a surface that supports pbuffer.
             */
            private void eglSetup() {
                final int EGL_OPENGL_ES2_BIT = 0x0004;
                final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

                mEGLDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
                if (mEGLDisplay == EGL10.EGL_NO_DISPLAY) {
                    throw new RuntimeException("unable to get EGL14 display");
                }
                int[] version = new int[2];
                if (!mEgl.eglInitialize(mEGLDisplay, version)) {
                    mEGLDisplay = null;
                    throw new RuntimeException("unable to initialize EGL14");
                }

                // Configure EGL for pbuffer and OpenGL ES 2.0, 24-bit RGB.
                int[] attribList = {
                        EGL10.EGL_RED_SIZE, 8,
                        EGL10.EGL_GREEN_SIZE, 8,
                        EGL10.EGL_BLUE_SIZE, 8,
                        EGL10.EGL_ALPHA_SIZE, 8,
                        EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                        EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
                        EGL10.EGL_NONE
                };
                EGLConfig[] configs = new EGLConfig[1];
                int[] numConfigs = new int[1];
                if (!mEgl.eglChooseConfig(mEGLDisplay, attribList, configs, configs.length,
                        numConfigs)) {
                    throw new RuntimeException("unable to find RGB888+recordable ES2 EGL config");
                }

                // Configure context for OpenGL ES 2.0.
                int[] attrib_list = {
                        EGL_CONTEXT_CLIENT_VERSION, 2,
                        EGL10.EGL_NONE
                };
                mEGLContext = mEgl.eglCreateContext(mEGLDisplay, configs[0], EGL10.EGL_NO_CONTEXT,
                        attrib_list);
                checkEglError("eglCreateContext");
                if (mEGLContext == null) {
                    throw new RuntimeException("null context");
                }

                // Create a pbuffer surface.
                int[] surfaceAttribs = {
                        EGL10.EGL_WIDTH, mWidth,
                        EGL10.EGL_HEIGHT, mHeight,
                        EGL10.EGL_NONE
                };
                mEGLSurface = mEgl.eglCreatePbufferSurface(mEGLDisplay, configs[0], surfaceAttribs);
                checkEglError("eglCreatePbufferSurface");
                if (mEGLSurface == null) {
                    throw new RuntimeException("surface was null");
                }
            }

            /**
             * Discard all resources held by this class, notably the EGL context.
             */
            public void release() {
                if (mEGLDisplay != EGL10.EGL_NO_DISPLAY) {
                    mEgl.eglDestroySurface(mEGLDisplay, mEGLSurface);
                    mEgl.eglDestroyContext(mEGLDisplay, mEGLContext);
                    //mEgl.eglReleaseThread();
                    mEgl.eglMakeCurrent(mEGLDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
                            EGL10.EGL_NO_CONTEXT);
                    mEgl.eglTerminate(mEGLDisplay);
                }
                mEGLDisplay = EGL10.EGL_NO_DISPLAY;
                mEGLContext = EGL10.EGL_NO_CONTEXT;
                mEGLSurface = EGL10.EGL_NO_SURFACE;

                mSurface.release();

                // this causes a bunch of warnings that appear harmless but might confuse someone:
                //  W BufferQueue: [unnamed-3997-2] cancelBuffer: BufferQueue has been abandoned!
                //mSurfaceTexture.release();

                mTextureRender = null;
                mSurface = null;
                mSurfaceTexture = null;
            }

            /**
             * Makes our EGL context and surface current.
             */
            public void makeCurrent() {
                if (!mEgl.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext)) {
                    throw new RuntimeException("eglMakeCurrent failed");
                }
            }

            /**
             * Returns the Surface.
             */
            public Surface getSurface() {
                return mSurface;
            }

            /**
             * Latches the next buffer into the texture.  Must be called from the thread that created
             * the CodecOutputSurface object.  (More specifically, it must be called on the thread
             * with the EGLContext that contains the GL texture object used by SurfaceTexture.)
             */
            public void awaitNewImage() {
                final int TIMEOUT_MS = 2500;

                synchronized (mFrameSyncObject) {
                    while (!mFrameAvailable) {
                        try {
                            // Wait for onFrameAvailable() to signal us.  Use a timeout to avoid
                            // stalling the test if it doesn't arrive.
                            mFrameSyncObject.wait(TIMEOUT_MS);
                            if (!mFrameAvailable) {
                                // TODO: if "spurious wakeup", continue while loop
                                throw new RuntimeException("frame wait timed out");
                            }
                        } catch (InterruptedException ie) {
                            // shouldn't happen
                            throw new RuntimeException(ie);
                        }
                    }
                    mFrameAvailable = false;
                }

                // Latch the data.
                mTextureRender.checkGlError("before updateTexImage");
                mSurfaceTexture.updateTexImage();
            }

            /**
             * Draws the data from SurfaceTexture onto the current EGL surface.
             *
             * @param invert if set, render the image with Y inverted (0,0 in top left)
             */
            public void drawImage(boolean invert) {
                mTextureRender.drawFrame(mSurfaceTexture, invert);
            }

            // SurfaceTexture callback
            @Override
            public void onFrameAvailable(SurfaceTexture st) {
                if (VERBOSE) Log.d(TAG, "new frame available");
                synchronized (mFrameSyncObject) {
                    if (mFrameAvailable) {
                        throw new RuntimeException("mFrameAvailable already set, frame could be dropped");
                    }
                    mFrameAvailable = true;
                    mFrameSyncObject.notifyAll();
                }
            }

            /**
             * Saves the current frame to disk as a PNG image.
             */
            public void saveFrame(String filename) throws IOException {
                // glReadPixels gives us a ByteBuffer filled with what is essentially big-endian RGBA
                // data (i.e. a byte of red, followed by a byte of green...).  To use the Bitmap
                // constructor that takes an int[] array with pixel data, we need an int[] filled
                // with little-endian ARGB data.
                //
                // If we implement this as a series of buf.get() calls, we can spend 2.5 seconds just
                // copying data around for a 720p frame.  It's better to do a bulk get() and then
                // rearrange the data in memory.  (For comparison, the PNG compress takes about 500ms
                // for a trivial frame.)
                //
                // So... we set the ByteBuffer to little-endian, which should turn the bulk IntBuffer
                // get() into a straight memcpy on most Android devices.  Our ints will hold ABGR data.
                // Swapping B and R gives us ARGB.  We need about 30ms for the bulk get(), and another
                // 270ms for the color swap.
                //
                // We can avoid the costly B/R swap here if we do it in the fragment shader (see
                // http://stackoverflow.com/questions/21634450/ ).
                //
                // Having said all that... it turns out that the Bitmap#copyPixelsFromBuffer()
                // method wants RGBA pixels, not ARGB, so if we create an empty bitmap and then
                // copy pixel data in we can avoid the swap issue entirely, and just copy straight
                // into the Bitmap from the ByteBuffer.
                //
                // Making this even more interesting is the upside-down nature of GL, which means
                // our output will look upside-down relative to what appears on screen if the
                // typical GL conventions are used.  (For ExtractMpegFrameTest, we avoid the issue
                // by inverting the frame when we render it.)
                //
                // Allocating large buffers is expensive, so we really want mPixelBuf to be
                // allocated ahead of time if possible.  We still get some allocations from the
                // Bitmap / PNG creation.

                mPixelBuf.rewind();
                GLES20.glReadPixels(0, 0, mWidth, mHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
                        mPixelBuf);

                BufferedOutputStream bos = null;
                try {
                    bos = new BufferedOutputStream(new FileOutputStream(filename));
                    Bitmap bmp = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
                    mPixelBuf.rewind();
                    bmp.copyPixelsFromBuffer(mPixelBuf);
                    bmp.compress(Bitmap.CompressFormat.PNG, 90, bos);
                    bmp.recycle();
                } finally {
                    if (bos != null) bos.close();
                }
                if (VERBOSE) {
                    Log.d(TAG, "Saved " + mWidth + "x" + mHeight + " frame as '" + filename + "'");
                }
            }

            /**
             * Checks for EGL errors.
             */
            private void checkEglError(String msg) {
                int error;
                if ((error = mEgl.eglGetError()) != EGL10.EGL_SUCCESS) {
                    throw new RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error));
                }
            }
        }


        /**
         * Code for rendering a texture onto a surface using OpenGL ES 2.0.
         */
        private static class STextureRender {
            private static final int FLOAT_SIZE_BYTES = 4;
            private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
            private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
            private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
            private final float[] mTriangleVerticesData = {
                    // X, Y, Z, U, V
                    -1.0f, -1.0f, 0, 0.f, 0.f,
                    1.0f, -1.0f, 0, 1.f, 0.f,
                    -1.0f,  1.0f, 0, 0.f, 1.f,
                    1.0f,  1.0f, 0, 1.f, 1.f,
            };

            private FloatBuffer mTriangleVertices;

            private static final String VERTEX_SHADER =
                    "uniform mat4 uMVPMatrix;\n" +
                            "uniform mat4 uSTMatrix;\n" +
                            "attribute vec4 aPosition;\n" +
                            "attribute vec4 aTextureCoord;\n" +
                            "varying vec2 vTextureCoord;\n" +
                            "void main() {\n" +
                            "    gl_Position = uMVPMatrix * aPosition;\n" +
                            "    vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                            "}\n";

            private static final String FRAGMENT_SHADER =
                    "#extension GL_OES_EGL_image_external : require\n" +
                            "precision mediump float;\n" +      // highp here doesn't seem to matter
                            "varying vec2 vTextureCoord;\n" +
                            "uniform samplerExternalOES sTexture;\n" +
                            "void main() {\n" +
                            "    gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                            "}\n";

            private float[] mMVPMatrix = new float[16];
            private float[] mSTMatrix = new float[16];

            private int mProgram;
            private int mTextureID = -12345;
            private int muMVPMatrixHandle;
            private int muSTMatrixHandle;
            private int maPositionHandle;
            private int maTextureHandle;

            public STextureRender() {
                mTriangleVertices = ByteBuffer.allocateDirect(
                        mTriangleVerticesData.length * FLOAT_SIZE_BYTES)
                        .order(ByteOrder.nativeOrder()).asFloatBuffer();
                mTriangleVertices.put(mTriangleVerticesData).position(0);

                Matrix.setIdentityM(mSTMatrix, 0);
            }

            public int getTextureId() {
                return mTextureID;
            }

            /**
             * Draws the external texture in SurfaceTexture onto the current EGL surface.
             */
            public void drawFrame(SurfaceTexture st, boolean invert) {
                checkGlError("onDrawFrame start");
                st.getTransformMatrix(mSTMatrix);
                if (invert) {
                    mSTMatrix[5] = -mSTMatrix[5];
                    mSTMatrix[13] = 1.0f - mSTMatrix[13];
                }

                // (optional) clear to green so we can see if we're failing to set pixels
                GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

                GLES20.glUseProgram(mProgram);
                checkGlError("glUseProgram");

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);

                mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
                GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                        TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
                checkGlError("glVertexAttribPointer maPosition");
                GLES20.glEnableVertexAttribArray(maPositionHandle);
                checkGlError("glEnableVertexAttribArray maPositionHandle");

                mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
                GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
                        TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
                checkGlError("glVertexAttribPointer maTextureHandle");
                GLES20.glEnableVertexAttribArray(maTextureHandle);
                checkGlError("glEnableVertexAttribArray maTextureHandle");

                Matrix.setIdentityM(mMVPMatrix, 0);
                GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
                GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);

                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
                checkGlError("glDrawArrays");

                GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
            }

            /**
             * Initializes GL state.  Call this after the EGL surface has been created and made current.
             */
            public void surfaceCreated() {
                mProgram = createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
                if (mProgram == 0) {
                    throw new RuntimeException("failed creating program");
                }

                maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
                checkLocation(maPositionHandle, "aPosition");
                maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
                checkLocation(maTextureHandle, "aTextureCoord");

                muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
                checkLocation(muMVPMatrixHandle, "uMVPMatrix");
                muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
                checkLocation(muSTMatrixHandle, "uSTMatrix");

                int[] textures = new int[1];
                GLES20.glGenTextures(1, textures, 0);

                mTextureID = textures[0];
                GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);
                checkGlError("glBindTexture mTextureID");

                GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                        GLES20.GL_NEAREST);
                GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                        GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                        GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                        GLES20.GL_CLAMP_TO_EDGE);
                checkGlError("glTexParameter");
            }

            /**
             * Replaces the fragment shader.  Pass in null to reset to default.
             */
            public void changeFragmentShader(String fragmentShader) {
                if (fragmentShader == null) {
                    fragmentShader = FRAGMENT_SHADER;
                }
                GLES20.glDeleteProgram(mProgram);
                mProgram = createProgram(VERTEX_SHADER, fragmentShader);
                if (mProgram == 0) {
                    throw new RuntimeException("failed creating program");
                }
            }

            private int loadShader(int shaderType, String source) {
                int shader = GLES20.glCreateShader(shaderType);
                checkGlError("glCreateShader type=" + shaderType);
                GLES20.glShaderSource(shader, source);
                GLES20.glCompileShader(shader);
                int[] compiled = new int[1];
                GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
                if (compiled[0] == 0) {
                    Log.e(TAG, "Could not compile shader " + shaderType + ":");
                    Log.e(TAG, " " + GLES20.glGetShaderInfoLog(shader));
                    GLES20.glDeleteShader(shader);
                    shader = 0;
                }
                return shader;
            }

            private int createProgram(String vertexSource, String fragmentSource) {
                int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
                if (vertexShader == 0) {
                    return 0;
                }
                int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
                if (pixelShader == 0) {
                    return 0;
                }

                int program = GLES20.glCreateProgram();
                if (program == 0) {
                    Log.e(TAG, "Could not create program");
                }
                GLES20.glAttachShader(program, vertexShader);
                checkGlError("glAttachShader");
                GLES20.glAttachShader(program, pixelShader);
                checkGlError("glAttachShader");
                GLES20.glLinkProgram(program);
                int[] linkStatus = new int[1];
                GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
                if (linkStatus[0] != GLES20.GL_TRUE) {
                    Log.e(TAG, "Could not link program: ");
                    Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                    GLES20.glDeleteProgram(program);
                    program = 0;
                }
                return program;
            }

            public void checkGlError(String op) {
                int error;
                while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
                    Log.e(TAG, op + ": glError " + error);
                    throw new RuntimeException(op + ": glError " + error);
                }
            }

            public static void checkLocation(int location, String label) {
                if (location < 0) {
                    throw new RuntimeException("Unable to locate '" + label + "' in program");
                }
            }
        }
}
