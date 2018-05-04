package com.aserbao.androidcustomcamera.base.beans;

/**
 * description:
 * Created by aserbao on 2018/1/25.
 */


public class ClassBean {
    private String name;
    private Class<?> clazz;

    public ClassBean(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
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
}
