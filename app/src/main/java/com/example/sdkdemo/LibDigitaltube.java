/***********************************************
*
*		DEMO INTERFACE FOR libPsam_jni.so
*
************************************************/

package com.example.sdkdemo;

import android.util.Log;

public class LibDigitaltube {
    private static final String TAG = "LibDigitaltube";

    public native boolean digitaltube_open();
    public native boolean digitaltube_close();
    public native int digitaltube_write_data(int data);


    static {
        System.loadLibrary("digitaltube_jni");
    };
}
