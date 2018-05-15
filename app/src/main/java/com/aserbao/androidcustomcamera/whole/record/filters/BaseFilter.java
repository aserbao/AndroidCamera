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

import static com.aserbao.androidcustomcamera.base.MyApplication.DEBUG;

/**
 * description:
 * Created by aserbao on 2018/5/15.
 */


public abstract class BaseFilter {
    private static final String TAG = "BaseFilter";
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
    protected Resources mRes;

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
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };
    private SparseArray<boolean[]> mBools;
    private SparseArray<int[]> mInts;
    private SparseArray<float[]> mFloats;


    public BaseFilter(Resources mRes){
        this.mRes=mRes;
        initBuffer();
    }
    /**
     * Buffer初始化
     */
    protected void initBuffer() {
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
    /**
     * 清除画布
     */
    protected void onClear(){
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    protected final void createProgramByAssetsFile(String vertex, String fragment){
        createProgram(uRes(mRes,vertex),uRes(mRes,fragment));
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
    protected final void createProgram(String vertex, String fragment){
        mProgram= uCreateGlProgram(vertex,fragment);
        mHPosition= GLES20.glGetAttribLocation(mProgram, "vPosition");
        mHCoord= GLES20.glGetAttribLocation(mProgram,"vCoord");
        mHMatrix= GLES20.glGetUniformLocation(mProgram,"vMatrix");
        mHTexture= GLES20.glGetUniformLocation(mProgram,"vTexture");
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
    public static int uLoadShader(int shaderType,String source){
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

    public static void glError(int code,Object index){
        if(DEBUG&&code!=0){
            Log.e(TAG,"glError:"+code+"---"+index);
        }
    }

    //2
    /**
     * 绑定默认纹理
     */
    protected void onBindTexture(){
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+textureType);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,getTextureId());
        GLES20.glUniform1i(mHTexture,textureType);
    }

    public int getOutputTexture(){
        return -1;
    }

    public void draw(){
        onClear();
        onUseProgram();
        onSetExpandData();
        onBindTexture();
        onDraw();
    }

    protected void onUseProgram(){
        GLES20.glUseProgram(mProgram);
    }
    /**
     * 设置其他扩展数据
     */
    protected void onSetExpandData(){
        GLES20.glUniformMatrix4fv(mHMatrix,1,false,matrix,0);
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

    public final void setSize(int width,int height){
        onSizeChanged(width,height);
    }

    //========================================
    public final void create(){
        onCreate();
    }
    protected abstract void onCreate();
    protected abstract void onSizeChanged(int width,int height);


    //============================================Getting And Setting

    public static String getTAG() {
        return TAG;
    }

    public static float[] getOM() {
        return OM;
    }

    public int getProgram() {
        return mProgram;
    }

    public void setProgram(int program) {
        mProgram = program;
    }

    public int getHPosition() {
        return mHPosition;
    }

    public void setHPosition(int HPosition) {
        mHPosition = HPosition;
    }

    public int getHCoord() {
        return mHCoord;
    }

    public void setHCoord(int HCoord) {
        mHCoord = HCoord;
    }

    public int getHMatrix() {
        return mHMatrix;
    }

    public void setHMatrix(int HMatrix) {
        mHMatrix = HMatrix;
    }

    public int getHTexture() {
        return mHTexture;
    }

    public void setHTexture(int HTexture) {
        mHTexture = HTexture;
    }

    public FloatBuffer getVerBuffer() {
        return mVerBuffer;
    }

    public void setVerBuffer(FloatBuffer verBuffer) {
        mVerBuffer = verBuffer;
    }

    public FloatBuffer getTexBuffer() {
        return mTexBuffer;
    }

    public void setTexBuffer(FloatBuffer texBuffer) {
        mTexBuffer = texBuffer;
    }

    public int getFlag() {
        return mFlag;
    }

    public void setFlag(int flag) {
        mFlag = flag;
    }

    public Resources getRes() {
        return mRes;
    }

    public void setRes(Resources res) {
        mRes = res;
    }

    public float[] getMatrix() {
        return matrix;
    }

    public void setMatrix(float[] matrix) {
        this.matrix = matrix;
    }

    public int getTextureType() {
        return textureType;
    }

    public void setTextureType(int textureType) {
        this.textureType = textureType;
    }

    public int getTextureId() {
        return textureId;
    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    public float[] getPos() {
        return pos;
    }

    public void setPos(float[] pos) {
        this.pos = pos;
    }

    public float[] getCoord() {
        return coord;
    }

    public void setCoord(float[] coord) {
        this.coord = coord;
    }

    public SparseArray<boolean[]> getBools() {
        return mBools;
    }

    public void setBools(SparseArray<boolean[]> bools) {
        mBools = bools;
    }

    public SparseArray<int[]> getInts() {
        return mInts;
    }

    public void setInts(SparseArray<int[]> ints) {
        mInts = ints;
    }

    public SparseArray<float[]> getFloats() {
        return mFloats;
    }

    public void setFloats(SparseArray<float[]> floats) {
        mFloats = floats;
    }
}
