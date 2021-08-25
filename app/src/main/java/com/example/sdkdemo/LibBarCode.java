/***********************************************
*
*		DEMO INTERFACE FOR libbarcode_jni.so
*
************************************************/

package com.example.sdkdemo;

import android.util.Log;

public class LibBarCode {
    private static final String TAG = "LibBarCode";
    private OnBarCodeProgressListener callbackListener;

    //回调到各个线程
    public interface OnBarCodeProgressListener {
//        public int onProgressChange(String barcode, int len);
        public int onProgressChange(byte[] barcode, int len);
    };

    public boolean barCodeRead(OnBarCodeProgressListener l) {
        callbackListener = l;
        return read_barcode();
    }

    public native boolean init_barcode();
    public native boolean close_barcode();
    private native boolean read_barcode();
    public native boolean barcode_write(byte[] pkg);

    private int onProgressCallBack(byte[] barcode, int len) {
        //自行执行回调后的操作
        callbackListener.onProgressChange(barcode, len);
        return 1;
    }

	
    static {
        System.loadLibrary("barcode_jni");
    };
    public class aaaLibBarCode {

    }

}
