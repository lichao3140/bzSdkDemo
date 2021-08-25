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
import android.widget.RadioButton;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.Editable;
import android.widget.Toast;


public class GpioTest extends Activity {

    private static final String TAG = "GpioTest/Activity";

    private Button mBtnWrite = null;
    private Button mBtnRead = null;
    private OnClickListener mBtnClickListener;

    private EditText mEtGpioPort = null;
    private TextView mTxtGpioPortState = null;

    private RadioButton mRadioGpioHigh;
    private RadioButton mRadioGpioLow;

    private static LibGpio mLibGpio = new LibGpio();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("@M_" + TAG, "Enter onCreate  function of Main Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gpio_demo);

        mBtnWrite = (Button) findViewById(R.id.gpio_test_write_btn);
        mBtnRead = (Button) findViewById(R.id.gpio_test_read_btn);

        mEtGpioPort = (EditText) findViewById(R.id.gpio_value_content);
        mEtGpioPort.setText("0");
        mTxtGpioPortState = (TextView) findViewById(R.id.gpio_test_value_content);
        mTxtGpioPortState.setText("0");
			
        mRadioGpioHigh = (RadioButton) findViewById(R.id.gpio_high_radio);
        mRadioGpioLow = (RadioButton) findViewById(R.id.gpio_low_radio);
		mRadioGpioHigh.setChecked(true);

        mBtnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v == (View) mBtnWrite) {
					Log.v("@M_" + TAG, "Start button is pressed");
					writeGPIOTest();
				} else if (v == (View) mBtnRead) {
					Log.v("@M_" + TAG, "Stop button is pressed");
					readGPIOTest();
				} else {
					return;
				}
			}
		};

        mBtnWrite.setOnClickListener(mBtnClickListener);
        mBtnRead.setOnClickListener(mBtnClickListener);
        mBtnWrite.setEnabled(true);
        mBtnRead.setEnabled(true);
    }

    private void writeGPIOTest() {
        try {
				Editable content = mEtGpioPort.getText();
                if((content != null) && (content.length() > 0)) {
                    int gpio_value = Integer.parseInt(mEtGpioPort.getText().toString());
                    if (mLibGpio.is_gpio_valid(gpio_value)) {
                        if(mLibGpio.set_gpio_output(gpio_value, mRadioGpioHigh.isChecked() ? 1 : 0))
                            mTxtGpioPortState.setText("error!");
                        else
                            mTxtGpioPortState.setText(mRadioGpioHigh.isChecked() ? "1" : "0");
                    }
                }
        } catch (NumberFormatException e) {
                Toast.makeText(this, "invalid input value",
                        Toast.LENGTH_SHORT).show();
                return;
        }
    }

    private void readGPIOTest() {
		Editable content = mEtGpioPort.getText();		
		if((content != null) && (content.length() > 0)) {
            int gpio_value = Integer.parseInt(mEtGpioPort.getText().toString());
            if (mLibGpio.is_gpio_valid(gpio_value)) {
                mLibGpio.set_gpio_dir(gpio_value, mLibGpio.GPIO_INPUT_MODE);
                mTxtGpioPortState.setText(mLibGpio.get_gpio_value(gpio_value) ? "1" : "0");
            }
        }
    }

}
