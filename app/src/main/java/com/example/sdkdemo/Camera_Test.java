package com.example.sdkdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class Camera_Test extends Activity {

    public static final String ARG_ITEM_ID = "camera_item_id";
    private static final String TAG = "Camera_Test/Activity";
    private static final String TAG_BG = "Camera_Test/BG";
    private static final int LIGHT_INFRA_TYPE = 0x600000;
    private static final int LIGHT_AUX_TYPE = 0x600001;
    private int light_infra_value;
    private int light_aux_value;
    private Button mBtnPreview = null;
    private Button mBtnInfraLight = null;
    private Button mBtnAuxLight = null;
    private View.OnClickListener mBtnClickListener;

    private static SurfaceView surfaceView_main;               	//图像实时窗口
    private static SurfaceView surfaceView_sub;               	//图像实时窗口
    private SurfaceHolder surfaceHolder_main;                  	//定义访问surfaceView的接口
    private SurfaceHolder surfaceHolder_sub;                  	//定义访问surfaceView的接口
    private Camera mCamera_main = null;
    private Camera mCamera_sub = null;
    private boolean bIfPreview_main = false;
    private boolean bIfPreview_sub = false;
    private boolean bHaveTakeBack = false;
    private boolean bHaveTakeFront = false;
    private int camerID_Main = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int camerID_Sub = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private static LibLeds mLibLeds = new LibLeds();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("@M_" + TAG, "Enter onCreate  function of Main Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_demo);

        mBtnPreview = (Button) findViewById(R.id.button_Preview);
        mBtnInfraLight = (Button) findViewById(R.id.button_infrared_light);
        mBtnAuxLight = (Button) findViewById(R.id.button_aux_Light);

        surfaceView_main = findViewById(R.id.surfaceView_main_cam);
        surfaceHolder_main = surfaceView_main.getHolder();
        surfaceHolder_main.addCallback(new CameraSurfaceCallBack_Main());
        surfaceView_sub = findViewById(R.id.surfaceView_sub_cam);
        surfaceHolder_sub = surfaceView_sub.getHolder();
        surfaceHolder_sub.addCallback(new CameraSurfaceCallBack_Sub());

        light_infra_value = 0;
        light_aux_value = 0;

        mBtnClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                if (v == (View) mBtnPreview) {
                    Log.v("@M_" + TAG, "Preview button is pressed");

                } else if (v == (View) mBtnInfraLight) {
                    Log.v("@M_" + TAG, "Capture button is pressed");
                    setLightLevel(LIGHT_INFRA_TYPE);
                } else if (v == (View) mBtnAuxLight) {
                    Log.v("@M_" + TAG, "Light button is pressed");
                    setLightLevel(LIGHT_AUX_TYPE);
                } else {
                    return;
                }
            }
        };

        mBtnPreview.setOnClickListener(mBtnClickListener);
        mBtnInfraLight.setOnClickListener(mBtnClickListener);
        mBtnAuxLight.setOnClickListener(mBtnClickListener);
        mBtnAuxLight.setEnabled(true);
        mBtnInfraLight.setEnabled(true);
        mBtnPreview.setEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        startPreview(mCamera_main, camerID_Main);
        startPreview(mCamera_sub, camerID_Sub);
    }

    private void checkCameraInfo() {
        int camerID_Main = -1;
        int camerID_Sub = -1;
        int cameraCount = Camera.getNumberOfCameras();

        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int cameraIndex = 0; cameraIndex < cameraCount; cameraIndex++) {
            Camera.getCameraInfo(cameraIndex, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                camerID_Sub = cameraIndex;
            } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                camerID_Main = cameraIndex;
            }
        }
    }

    public static int getDisplayRotation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0: return 0;
            case Surface.ROTATION_90: return 90;
            case Surface.ROTATION_180: return 180;
            case Surface.ROTATION_270: return 270;
        }
        return 0;
    }

    @SuppressLint("NewApi")
    public static int getDisplayOrientation(int degrees, int cameraId) {
        // See android.hardware.Camera.setDisplayOrientation for documentation.
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    /**
     * surfaceView实例回调
     */
    public class CameraSurfaceCallBack_Main implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.e("TAG","------surfaceCreated------");
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.e("TAG","------surfaceChanged------");
            surfaceHolder_main = holder;
            if(mCamera_main == null)
                return;
            if(bIfPreview_main && holder.isCreating())
                setPreviewDisplay(holder, mCamera_main, camerID_Main);
            else
//                startPreview();
            startPreview(mCamera_main, camerID_Main);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "Surface Destroyed");
        }
    }

    /**
     * surfaceView实例回调
     */
    public class CameraSurfaceCallBack_Sub implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.e("TAG","------surfaceCreated------");
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.e("TAG","------surfaceChanged------");
            surfaceHolder_sub = holder;
            if(mCamera_sub == null)
                return;
            if(bIfPreview_sub && holder.isCreating())
                setPreviewDisplay(holder, mCamera_sub, camerID_Sub);
            else
//                startPreview();
                startPreview(mCamera_sub, camerID_Sub);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "Surface Destroyed");
        }
    }

    private void setPreviewDisplay(SurfaceHolder holder, Camera mCamera, int cameraId) {
        try {
            mCamera.setPreviewDisplay(holder);
            int mDisplayRotation = getDisplayRotation(this);
            int mDisplayOrientation = getDisplayOrientation(mDisplayRotation, cameraId);
            mCamera.setDisplayOrientation(mDisplayOrientation);

        } catch (IOException e) {
            closeCamera(mCamera);
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, double targetRatio) {
        final double ASPECT_TOLERANCE = 0.05;
        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        // Because of bugs of overlay and layout, we sometimes will try to
        // layout the viewfinder in the portrait orientation and thus get the
        // wrong size of mSurfaceView. When we change the preview size, the
        // new overlay will be created before the old one closed, which causes
        // an exception. For now, just get the screen size

        Display display = getWindowManager().getDefaultDisplay();
        int targetHeight = Math.min(display.getHeight(), display.getWidth());

        if (targetHeight <= 0) {
            // We don't know the size of SurefaceView, use screen height
            WindowManager windowManager = (WindowManager)
                    getSystemService(Context.WINDOW_SERVICE);
            targetHeight = windowManager.getDefaultDisplay().getHeight();
        }

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            Log.v(TAG, "No preview size match the aspect ratio");
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        Log.i(TAG, String.format(
                "Optimal preview size is %sx%s",
                optimalSize.height, optimalSize.height));
        return optimalSize;
    }

    private void setCameraParameters(Camera mCamera) {
        Camera.Parameters mParameters = mCamera.getParameters();

        // Reset preview frame rate to the maximum because it may be lowered by
        // video camera application.
        List<Integer> frameRates = mParameters.getSupportedPreviewFrameRates();
        if (frameRates != null) {
            Integer max = Collections.max(frameRates);
            mParameters.setPreviewFrameRate(max);
        }
        // Set picture size.
        if(mCamera == mCamera_main)
            mParameters.setPictureSize(surfaceView_main.getWidth(),surfaceView_main.getHeight());
        else
            mParameters.setPictureSize(surfaceView_sub.getWidth(),surfaceView_sub.getHeight());

        Camera.Size size = mParameters.getPictureSize();
        // Set a preview size that is closest to the viewfinder height and has
        // the right aspect ratio.
        List<Camera.Size> sizes = mParameters.getSupportedPreviewSizes();
        Camera.Size optimalSize = getOptimalPreviewSize(
                sizes, (double) size.width / size.height);
        if (optimalSize != null) {
            mParameters.setPreviewSize(optimalSize.width, optimalSize.height);
        }
    }

    public void startPreview(Camera mCamera, int cameraId) {

        if (mCamera == null) {
            try	{
                mCamera = Camera.open(cameraId);
                if(cameraId == camerID_Main)
                    mCamera_main = mCamera;
                else if(cameraId == camerID_Sub)
                    mCamera_sub = mCamera;
            }catch(Throwable ex) {
                Log.e(TAG, "Camera.open fail!");
            }
        }

        if(((cameraId == camerID_Main) && bIfPreview_main) ||
            ((cameraId == camerID_Sub) && bIfPreview_sub))
            stopPreview(mCamera);

        if (mCamera != null) {
            Log.i(TAG, "inside the camera");

            setCameraParameters(mCamera);
            if(mCamera == mCamera_main)
                setPreviewDisplay(surfaceHolder_main, mCamera_main, camerID_Main);
            else
                setPreviewDisplay(surfaceHolder_sub, mCamera_main, camerID_Main);

            try {
                mCamera.startPreview();
                Log.i(TAG, "start preview");
            } catch (Throwable ex) {
                closeCamera(mCamera);
                // TODO Auto-generated catch block
                //e.printStackTrace();
            }
            if (mCamera == mCamera_main)
                bIfPreview_main = true;
            if(mCamera == mCamera_sub)
                bIfPreview_sub = true;

            //Preview ok -> Success
            if(cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
                bHaveTakeFront = true;
            if(cameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
                bHaveTakeBack = true;
            if(bHaveTakeFront && bHaveTakeBack) {
//                mBtnPass.setEnabled(true);
            }
        }
    }


    private void stopMainPreview() {
        Log.v(TAG, "main:stopPreview");
        mCamera_main.stopPreview();
        bIfPreview_main = false;
    }

    private  void stopSubPreview() {
        Log.v(TAG, "sub:stopPreview");
        mCamera_sub.stopPreview();
        bIfPreview_sub = false;
    }

    private void stopPreview(Camera mCamera) {
        if(mCamera != null) {
            if ((mCamera == mCamera_main) && bIfPreview_main)
                stopMainPreview();
            if((mCamera == mCamera_sub) && bIfPreview_sub)
                stopSubPreview();
        }
    }

    //function to release the camera
    private void closeCamera(Camera mCamera){
        if(mCamera != null){
            mCamera.release();
            mCamera = null;
            if (mCamera == mCamera_main)
                bIfPreview_main = false;
            if(mCamera == mCamera_sub)
                bIfPreview_sub = false;
        }
    }

    private void setLightLevel(final int light_type)
    {
        final AlertDialog builder = new AlertDialog.Builder(this)
                .create();
        builder.show();
        builder.getWindow().setContentView(R.layout.light_seekbar_dialog);//设置弹出框加载的布局
        TextView title = (TextView) builder.findViewById(R.id.textView_dialog_title);
//        cross = (TextView) builder.findViewById(R.id.cross);
        Button confirm = (Button) builder.findViewById(R.id.button_dlg_confirm);
//        Button cancel = (Button) builder.findViewById(R.id.button_dlg_cancel);
        SeekBar seekBar = (SeekBar) builder.findViewById(R.id.seekBar_dlg_level);
        builder.getWindow()
                .findViewById(R.id.button_dlg_confirm)
                .setOnClickListener(new View.OnClickListener() {  //按钮点击事件
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });
        if(light_type == LIGHT_INFRA_TYPE)
            seekBar.setProgress(light_infra_value);
        if(light_type == LIGHT_AUX_TYPE)
            seekBar.setProgress(light_aux_value);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if(fromUser) {
                    if(light_type == LIGHT_INFRA_TYPE) {
                        mLibLeds.set_infra_led(progress);
                        light_infra_value = progress;
                    }
                    if(light_type == LIGHT_AUX_TYPE) {
                        mLibLeds.set_aux_led(progress);
                        light_aux_value = progress;
                    }
                }
            }
        });
        if(light_type == LIGHT_INFRA_TYPE)
            title.setText("红外灯调节");
        if(light_type == LIGHT_AUX_TYPE)
            title.setText("补光灯调节");
    }


}
