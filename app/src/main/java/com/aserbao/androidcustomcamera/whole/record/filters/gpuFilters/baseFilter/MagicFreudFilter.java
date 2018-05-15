package com.aserbao.androidcustomcamera.whole.record.filters.gpuFilters.baseFilter;

import android.opengl.GLES20;

import com.aserbao.androidcustomcamera.base.MyApplication;
import com.aserbao.androidcustomcamera.R;

import com.aserbao.androidcustomcamera.whole.record.filters.gpuFilters.utils.OpenGlUtils;


public class MagicFreudFilter extends GPUImageFilter {
	private int mTexelHeightUniformLocation;
    private int mTexelWidthUniformLocation;
	private int[] inputTextureHandles = {-1};
	private int[] inputTextureUniformLocations = {-1};
	private int mGLStrengthLocation;

	public MagicFreudFilter(){
		super(NO_FILTER_VERTEX_SHADER, OpenGlUtils.readShaderFromRawResource(R.raw.freud));
	}
	
	protected void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteTextures(1, inputTextureHandles, 0);
        for(int i = 0; i < inputTextureHandles.length; i++)
        	inputTextureHandles[i] = -1;
    }
	
	protected void onDrawArraysAfter(){
		for(int i = 0; i < inputTextureHandles.length
				&& inputTextureHandles[i] != OpenGlUtils.NO_TEXTURE; i++){
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3));
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		}
	}
	  
	protected void onDrawArraysPre(){
		for(int i = 0; i < inputTextureHandles.length 
				&& inputTextureHandles[i] != OpenGlUtils.NO_TEXTURE; i++){
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3) );
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureHandles[i]);
			GLES20.glUniform1i(inputTextureUniformLocations[i], (i+3));
		}
	}
	
	protected void onInit(){
		super.onInit();
		inputTextureUniformLocations[0] = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture2");
		
		mTexelWidthUniformLocation = GLES20.glGetUniformLocation(getProgram(), "inputImageTextureWidth");
        mTexelHeightUniformLocation = GLES20.glGetUniformLocation(getProgram(), "inputImageTextureHeight");

		mGLStrengthLocation = GLES20.glGetUniformLocation(mGLProgId,
				"strength");
	}
	
	protected void onInitialized(){
		super.onInitialized();
		setFloat(mGLStrengthLocation, 1.0f);
	    runOnDraw(new Runnable(){
		    public void run(){
		    	inputTextureHandles[0] = OpenGlUtils.loadTexture(MyApplication.getContext(), "filter/freud_rand.png");
		    }
	    });
	}
	
	public void onInputSizeChanged(final int width, final int height) {
        super.onInputSizeChanged(width, height);
		runOnDraw(new Runnable() {
			@Override
			public void run() {
				GLES20.glUniform1f(mTexelWidthUniformLocation, (float)width);
				GLES20.glUniform1f(mTexelHeightUniformLocation, (float)height);
			}
		});
    }
}
