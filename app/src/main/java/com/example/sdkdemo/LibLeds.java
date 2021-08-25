/***********************************************
*
*		DEMO INTERFACE FOR libLeds_jni.so
*
************************************************/

package com.example.sdkdemo;

import android.util.Log;

public class LibLeds {
    private static final String TAG = "LibLeds";

    public native boolean set_lcd_backlight(int level);
    public native boolean set_aux_led(int level);
    public native boolean set_infra_led(int level);
	
    static {
        System.loadLibrary("leds_jni");
    };
}
