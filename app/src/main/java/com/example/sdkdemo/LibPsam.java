/***********************************************
*
*		DEMO INTERFACE FOR libPsam_jni.so
*
************************************************/

package com.example.sdkdemo;

import android.util.Log;

public class LibPsam {
    private static final String TAG = "LibPsam";

    public native boolean psam_open();
    public native boolean psam_close();
    public native int psam_get_version();
    public native int psam_set_bandrate(int bandrate);
    public native int psam_set_clock(int clk);
    public native byte[] psam_send_package(byte[] pkg, int len, int sam, int type);
    public native boolean psam_is_valid();

    static {
        System.loadLibrary("psam_jni");
    };
}
