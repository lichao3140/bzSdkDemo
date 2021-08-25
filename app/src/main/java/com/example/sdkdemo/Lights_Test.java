/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to MediaTek Inc. and/or its licensors.
 * Without the prior written permission of MediaTek inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of MediaTek Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 */
/* MediaTek Inc. (C) 2010. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER ON
 * AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH RESPECT TO THE
 * SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY, INCORPORATED IN, OR
 * SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES TO LOOK ONLY TO SUCH
 * THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO. RECEIVER EXPRESSLY ACKNOWLEDGES
 * THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES
 * CONTAINED IN MEDIATEK SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK
 * SOFTWARE RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S ENTIRE AND
 * CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE RELEASED HEREUNDER WILL BE,
 * AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE MEDIATEK SOFTWARE AT ISSUE,
 * OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY RECEIVER TO
 * MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek Software")
 * have been modified by MediaTek Inc. All revisions are subject to any receiver's
 * applicable license agreements with MediaTek Inc.
 */

package com.example.sdkdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class Lights_Test extends Activity {

    public static final String ARG_ITEM_ID = "light_item_id";

    private static final String TAG = "Lights_Test/Activity";
    private static final String TAG_BG = "Lights_Test/BG";

//    private Button mBtnEnable = null;
//    private Button mBtnDisable = null;
//    private OnClickListener mBtnClickListener;

    private TextView mTxtLightState = null;

    private static final int mLightGpioPort = 150;		// only for demo

    private static LibGpio mLibGpio = new LibGpio();
    private static LibLeds mLibLeds = new LibLeds();
    private SeekBar mSeekBar;
    private CheckBox mCheckBoxsLed;
    private CheckBox mCheckBoxsLcd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("@M_" + TAG, "Enter onCreate  function of Main Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lights_demo);

//        mBtnEnable = (Button) findViewById(R.id.light_enable_btn);
//        mBtnDisable = (Button) findViewById(R.id.light_disable_btn);
//        mBtnDisable.setEnabled(false);

        mCheckBoxsLed = findViewById(R.id.checkBox_ledbl);
        mCheckBoxsLed.toggle();
        mCheckBoxsLcd = findViewById(R.id.checkBox_lcdbl);

        mSeekBar = findViewById(R.id.Light_seekBar);
        mSeekBar.setMax(255);
        mSeekBar.setProgress(0);
        mSeekBar.setSecondaryProgress(0);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
                mTxtLightState.setText("Value:" + Integer.toString(progress));
                if(fromUser) {
                    if(mCheckBoxsLed.isChecked())
                        mLibLeds.set_aux_led(progress);
                    if(mCheckBoxsLcd.isChecked())
                        mLibLeds.set_lcd_backlight(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("------------", "开始滑动！");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("------------", "停止滑动！");
            }
        });

        mTxtLightState = (TextView) findViewById(R.id.fillin_light_state_content);
        mTxtLightState.setText("VALUE: ");
/*
        mBtnClickListener = new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				Bundle extras = new Bundle();
				if (v == (View) mBtnEnable) {
					Log.v("@M_" + TAG, "Start button is pressed");
					mBtnEnable.setEnabled(false);
			        mBtnDisable.setEnabled(true);
                    LightPowerStartOn();
                    mTxtLightState.setText("Light is Enabled!");
				} else if (v == (View) mBtnDisable) {
					Log.v("@M_" + TAG, "Stop button is pressed");
					mBtnEnable.setEnabled(true);
			        mBtnDisable.setEnabled(false);
                    LightPowerStopOff();
                    mTxtLightState.setText("Light is Disabled!");
				} else {
					return;
				}
			}
		};

        mBtnEnable.setOnClickListener(mBtnClickListener);
        mBtnDisable.setOnClickListener(mBtnClickListener);
        mBtnEnable.setEnabled(true);
        mBtnDisable.setEnabled(true);
 */
    }

    private void LightPowerStartOn() {
        if (mLibGpio.is_gpio_valid(mLightGpioPort)) {
            mLibGpio.set_gpio_output(mLightGpioPort, 1);
            mTxtLightState.setText("Light power is ON, please to check it!");
        }
        else
            mTxtLightState.setText("Thoe port of controled by light is not valid gpio port!");
    }

    private void LightPowerStopOff() {

        if (mLibGpio.is_gpio_valid(mLightGpioPort)) {
            mLibGpio.set_gpio_output(mLightGpioPort, 0);
            mTxtLightState.setText("Light power is OFF, please to check it!");
        }
        else
            mTxtLightState.setText("Thoe port of controled by light is not valid gpio port!");
    }

}
