package com.aserbao.androidcustomcamera.whole.editVideo.beans;

import java.io.Serializable;

/**
 * Created by Abner on 15/6/11.
 * QQ 230877476
 * Email nimengbo@gmail.com
 */
public class BubblePropertyModel implements Serializable {
    private static final long serialVersionUID = 6339777989485920188L;
    //气泡id
    private long bubbleId;
    //文本
    private String text;
    //x坐标
    private float xLocation;
    //y坐标
    private float yLocation;
    //角度
    private float degree;
    //缩放值
    private float scaling;
    //气泡顺序
    private int order;

    public long getBubbleId() {
        return bubbleId;
    }

    public void setBubbleId(long bubbleId) {
        this.bubbleId = bubbleId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getxLocation() {
        return xLocation;
    }

    public void setxLocation(float xLocation) {
        this.xLocation = xLocation;
    }

    public float getyLocation() {
        return yLocation;
    }

    public void setyLocation(float yLocation) {
        this.yLocation = yLocation;
    }

    public float getDegree() {
        return degree;
    }

    public void setDegree(float degree) {
        this.degree = degree;
    }

    public float getScaling() {
        return scaling;
    }

    public void setScaling(float scaling) {
        this.scaling = scaling;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
