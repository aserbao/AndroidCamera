package com.aserbao.androidcustomcamera.whole.record.filters;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;
import android.util.SparseArray;

import com.aserbao.androidcustomcamera.whole.record.utils.MatrixUtils;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * Description:
 */
public abstract class AFilter {

    private static final String TAG="Filter";


    public static boolean DEBUG=true;
    /**
     * 单位矩阵
     */
    public static final float[] OM= MatrixUtils.getOriginalMatrix();
    /**
     * 程序句柄
     */
    protected int mProgram;
    /**
     * 顶点坐标句柄
     */
    protected int mHPosition;
    /**
     * 纹理坐标句柄
     */
    protected int mHCoord;
    /**
     * 总变换矩阵句柄
     */
    protected int mHMatrix;
    /**
     * 默认纹理贴图句柄
     */
    protected int mHTexture;

    protected Resources mRes;


    /**
     * 顶点坐标Buffer
     */
    protected FloatBuffer mVerBuffer;

    /**
     * 纹理坐标Buffer
     */
    protected FloatBuffer mTexBuffer;

    /**
     * 索引坐标Buffer
     */

    protected int mFlag=0;

    private float[] matrix= Arrays.copyOf(OM,16);

    private int textureType=0;      //默认使用Texture2D0
    private int textureId=0;
    //顶点坐标
    private float pos[] = {
        -1.0f,  1.0f,
        -1.0f, -1.0f,
        1.0f, 1.0f,
        1.0f,  -1.0f,
    };

    //纹理坐标
    private float[] coord={
        0.0f, 0.0f,
        0.0f,  1.0f,
        1.0f,  0.0f,
        1.0f, 1.0f,
    };

    private SparseArray<boolean[]> mBools;
    private SparseArray<int[]> mInts;
    private SparseArray<float[]> mFloats;


    public AFilter(Resources mRes){
        this.mRes=mRes;
        initBuffer();
    }

    public final void create(){
        onCreate();
    }

    public final void setSize(int width,int height){
        onSizeChanged(width,height);
    }

    public void draw(){
        onClear();
        onUseProgram();
        onSetExpandData();
        onBindTexture();
        onDraw();
    }
    public void draw(long time){
        onClear();
        onUseProgram();
        onSetExpandData();
        onBindTexture();
        onDraw();
    }

    public final void setMatrix(float[] matrix){
        this.matrix=matrix;
    }

    public float[] getMatrix(){
        return matrix;
    }

    public final void setTextureType(int type){
        this.textureType=type;
    }

    public final int getTextureType(){
        return textureType;
    }

    public final int getTextureId(){
        return textureId;
    }

    public final void setTextureId(int textureId){
        this.textureId=textureId;
    }

    public void setFlag(int flag){
        this.mFlag=flag;
    }

    public int getFlag(){
        return mFlag;
    }

    public void setFloat(int type,float ... params){
        if(mFloats==null){
            mFloats=new SparseArray<>();
        }
        mFloats.put(type,params);
    }
    public void setInt(int type,int ... params){
        if(mInts==null){
            mInts=new SparseArray<>();
        }
        mInts.put(type,params);
    }
    public void setBool(int type,boolean ... params){
        if(mBools==null){
            mBools=new SparseArray<>();
        }
        mBools.put(type,params);
    }

    public boolean getBool(int type,int index) {
        if (mBools == null) return false;
        boolean[] b = mBools.get(type);
        return !(b == null || b.length <= index) && b[index];
    }

    public int getInt(int type,int index){
        if (mInts == null) return 0;
        int[] b = mInts.get(type);
        if(b == null || b.length <= index){
            return 0;
        }
        return b[index];
    }

    public float getFloat(int type,int index){
        if (mFloats == null) return 0;
        float[] b = mFloats.get(type);
        if(b == null || b.length <= index){
            return 0;
        }
        return b[index];
    }

    public int getOutputTexture(){
        return -1;
    }

    /**
     * 实现此方法，完成程序的创建，可直接调用createProgram来实现
     */
    protected abstract void onCreate();
    protected abstract void onSizeChanged(int width,int height);

    protected final void createProgram(String vertex, String fragment){
        mProgram= uCreateGlProgram(vertex,fragment);
        mHPosition= GLES20.glGetAttribLocation(mProgram, "vPosition");
        mHCoord= GLES20.glGetAttribLocation(mProgram,"vCoord");
        mHMatrix= GLES20.glGetUniformLocation(mProgram,"vMatrix");
        mHTexture= GLES20.glGetUniformLocation(mProgram,"vTexture");
    }

    protected final void createProgramByAssetsFile(String vertex, String fragment){
        createProgram(uRes(mRes,vertex),uRes(mRes,fragment));
    }

    /**
     * Buffer初始化
     */
    protected void initBuffer(){
        ByteBuffer a= ByteBuffer.allocateDirect(32);
        a.order(ByteOrder.nativeOrder());
        mVerBuffer=a.asFloatBuffer();
        mVerBuffer.put(pos);
        mVerBuffer.position(0);
        ByteBuffer b= ByteBuffer.allocateDirect(32);
        b.order(ByteOrder.nativeOrder());
        mTexBuffer=b.asFloatBuffer();
        mTexBuffer.put(coord);
        mTexBuffer.position(0);
    }

    protected void onUseProgram(){
        GLES20.glUseProgram(mProgram);
    }

    /**
     * 启用顶点坐标和纹理坐标进行绘制
     */
    protected void onDraw(){
        GLES20.glEnableVertexAttribArray(mHPosition);
        GLES20.glVertexAttribPointer(mHPosition,2, GLES20.GL_FLOAT, false, 0,mVerBuffer);
        GLES20.glEnableVertexAttribArray(mHCoord);
        GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHCoord);
    }

    /**
     * 清除画布
     */
    protected void onClear(){
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * 设置其他扩展数据
     */
    protected void onSetExpandData(){
        GLES20.glUniformMatrix4fv(mHMatrix,1,false,matrix,0);
    }

    /**
     * 绑定默认纹理
     */
    protected void onBindTexture(){
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+textureType);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,getTextureId());
        GLES20.glUniform1i(mHTexture,textureType);
    }

    public static void glError(int code, Object index){
        if(DEBUG&&code!=0){
            Log.e(TAG,"glError:"+code+"---"+index);
        }
    }

    //通过路径加载Assets中的文本内容
    public static String uRes(Resources mRes, String path){
        StringBuilder result=new StringBuilder();
        try{
            InputStream is=mRes.getAssets().open(path);
            int ch;
            byte[] buffer=new byte[1024];
            while (-1!=(ch=is.read(buffer))){
                result.append(new String(buffer,0,ch));
            }
        }catch (Exception e){
            return null;
        }
        return result.toString().replaceAll("\\r\\n","\n");
    }

    //创建GL程序
    public static int uCreateGlProgram(String vertexSource, String fragmentSource){
        int vertex=uLoadShader(GLES20.GL_VERTEX_SHADER,vertexSource);
        if(vertex==0)return 0;
        int fragment=uLoadShader(GLES20.GL_FRAGMENT_SHADER,fragmentSource);
        if(fragment==0)return 0;
        int program= GLES20.glCreateProgram();
        if(program!=0){
            GLES20.glAttachShader(program,vertex);
            GLES20.glAttachShader(program,fragment);
            GLES20.glLinkProgram(program);
            int[] linkStatus=new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS,linkStatus,0);
            if(linkStatus[0]!= GLES20.GL_TRUE){
                glError(1,"Could not link program:"+ GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program=0;
            }
        }
        return program;
    }

    /**加载shader*/
    public static int uLoadShader(int shaderType, String source){
        int shader= GLES20.glCreateShader(shaderType);
        if(0!=shader){
            GLES20.glShaderSource(shader,source);
            GLES20.glCompileShader(shader);
            int[] compiled=new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS,compiled,0);
            if(compiled[0]==0){
                glError(1,"Could not compile shader:"+shaderType);
                glError(1,"GLES20 Error:"+ GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader=0;
            }
        }
        return shader;
    }


}
