package com.aserbao.androidcustomcamera.whole.pickvideo.callback;

public interface OnSelectStateListener<T> {
        void OnSelectStateChanged(boolean state, T file);
    }