package com.aserbao.androidcustomcamera.base.beans;

import com.aserbao.androidcustomcamera.base.utils.StaticFinalValues;

/**
 * description:
 * Created by aserbao on 2018/1/25.
 */


public class BaseRecyclerBean {
    private String name;
    String extra_info;      //补充信息
    int tag = -1;           //标记
    private Class<?> clazz;
    int viewType = StaticFinalValues.VIEW_HOLDER_TEXT;

    public BaseRecyclerBean(String name, int tag) {
        this.name = name;
        this.tag = tag;
        this.viewType = StaticFinalValues.VIEW_HOLDER_TEXT;
    }

    public BaseRecyclerBean(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
        this.viewType = StaticFinalValues.VIEW_HOLDER_CLASS;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getExtra_info() {
        return extra_info;
    }

    public void setExtra_info(String extra_info) {
        this.extra_info = extra_info;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
