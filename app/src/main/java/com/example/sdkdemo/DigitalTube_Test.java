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
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class DigitalTube_Test extends Activity {

    public static final String ARG_ITEM_ID = "digitaltube_item_id";
    private static final String TAG = "DigitalTube_Test/Activity";
    private static final String TAG_BG = "DigitalTube_Test/BG";

	private Button mBtnWrite = null;
	private OnClickListener mBtnClickListener;

	private TextView mTxtPrompt = null;
	private EditText mEtNumberData = null;

	private static LibDigitaltube mLibDigital = new LibDigitaltube();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("@M_" + TAG, "Enter onCreate  function of Main Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.digitaltube_demo);

		mBtnWrite = (Button) findViewById(R.id.button_digital_write);

		mTxtPrompt = (TextView) findViewById(R.id.textView_digitalprompt);
		mEtNumberData = (EditText) findViewById(R.id.editText_digital);

		mBtnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle extras = new Bundle();
				if (v == (View) mBtnWrite) {
					Log.v("@M_" + TAG, "Start WriteDigitaltubeValueToDevice is pressed");
					WriteDigitaltubeValueToDevice();
				} else {
					return;
				}
			}
		};

		mBtnWrite.setOnClickListener(mBtnClickListener);
		mBtnWrite.setEnabled(true);
    }

    private void WriteDigitaltubeValueToDevice() {
		if(mLibDigital.digitaltube_open()) {
			String input_str = mEtNumberData.getText().toString();
			if(!input_str.isEmpty()) {
				float input = Float.valueOf(input_str);
				input = input * 100;
				int value = new Double(input).intValue();
				int ver = mLibDigital.digitaltube_write_data(value);
				if (ver < 0) {
					Log.d("@M_" + TAG, "写数码值错误!");
				} else {
					Log.d("@M_" + TAG, "写数码值成功!");
				}
				mLibDigital.digitaltube_close();
			}
			else {
				Log.d("@M_" + TAG, "输入数据为空!");
			}
		}
		else {
			Log.d("@M_" + TAG, "打开底层设备错误!");
		}
	}

}
