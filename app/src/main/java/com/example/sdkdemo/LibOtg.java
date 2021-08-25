/***********************************************
*
*		DEMO INTERFACE FOR libgpio_jni.so
*
************************************************/

package com.example.sdkdemo;

import android.util.Log;

public class LibOtg {
    private static final String TAG = "LibOtg";

    public static final int OTG_ENABLE_MODE = 1;
    public static final int OTG_DISABLE_MODE = 0;


    public native boolean enable_otg(int enable);
	
    static {
        System.loadLibrary("otg_jni");
    };
}
