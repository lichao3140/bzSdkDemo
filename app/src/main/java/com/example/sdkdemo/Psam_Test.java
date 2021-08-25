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


public class Psam_Test extends Activity {

    public static final String ARG_ITEM_ID = "psam_item_id";

    private static final String TAG = "Psam_Test/Activity";
    private static final String TAG_BG = "Psam_Test/BG";
	private static final int PSAM_CARD_TYPE = 9600;

    private Button mBtnGetVersion = null;
	private Button mBtnSetBandrate = null;
    private Button mBtnSetClock = null;
	private Button mBtnSendPackage = null;
    private OnClickListener mBtnClickListener;

	private TextView mTxtVersionState = null;
	private EditText mEtSendData = null;
    private TextView mTxtRespondState = null;

	private Spinner mSpClockType;
	ArrayAdapter<CharSequence> adapterClockPattern = null;

	private Spinner mSpBandRateType;
	ArrayAdapter<CharSequence> adapterRatePattern = null;

	private Spinner mSpChannelType;
	ArrayAdapter<CharSequence> adapterChannelPattern = null;

	private static LibPsam mLibPsam = new LibPsam();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("@M_" + TAG, "Enter onCreate  function of Main Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.psam_demo);

		mBtnGetVersion = (Button) findViewById(R.id.button_psam_get_ver);
		mBtnSetBandrate = (Button) findViewById(R.id.button_psam_set_bandrate);
		mBtnSetClock = (Button) findViewById(R.id.button_psam_set_clk);
		mBtnSendPackage = (Button) findViewById(R.id.button_psam_send);

		mTxtVersionState = (TextView) findViewById(R.id.textView_psam_version);
		mTxtVersionState.setText("");
		mTxtRespondState = (TextView) findViewById(R.id.textView_psam_package_respond);
		mTxtRespondState.setText("");
		mEtSendData = (EditText) findViewById(R.id.editText_psam_package);

		mSpClockType = (Spinner) findViewById(R.id.spinner_psam_clock);
		adapterClockPattern = ArrayAdapter.createFromResource(this,
				R.array.psam_clock,
				android.R.layout.simple_spinner_item);
		adapterClockPattern
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpClockType.setAdapter(adapterClockPattern);

		mSpBandRateType = (Spinner) findViewById(R.id.spinner_psam_bandrate);
		adapterRatePattern = ArrayAdapter.createFromResource(this,
				R.array.uart_baudrate,
				android.R.layout.simple_spinner_item);
		adapterRatePattern
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpBandRateType.setAdapter(adapterRatePattern);

		mSpChannelType = (Spinner) findViewById(R.id.spinner_psam_card);
		adapterChannelPattern = ArrayAdapter.createFromResource(this,
				R.array.psam_card,
				android.R.layout.simple_spinner_item);
		adapterChannelPattern
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpChannelType.setAdapter(adapterChannelPattern);

        mBtnClickListener = new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				Bundle extras = new Bundle();
				if (v == (View) mBtnGetVersion) {
					Log.v("@M_" + TAG, "Start mBtnGetVersion is pressed");
					String ver = getPsamModuleVersion();
					mTxtVersionState.setText("Version: " + ver);
				} else if (v == (View) mBtnSetBandrate) {
					Log.v("@M_" + TAG, "Stop mBtnSetBandrate is pressed");
					setPsamBandrate();
				} else if (v == (View) mBtnSetClock) {
					Log.v("@M_" + TAG, "Stop mBtnSetClock is pressed");
					setPsamClock();
				} else if (v == (View) mBtnSendPackage) {
					Log.v("@M_" + TAG, "Stop mBtnSendPackage is pressed");
					sendPackageToCard();
				} else {
					return;
				}
			}
		};

		mBtnGetVersion.setOnClickListener(mBtnClickListener);
		mBtnSetBandrate.setOnClickListener(mBtnClickListener);
		mBtnSetClock.setOnClickListener(mBtnClickListener);
		mBtnSendPackage.setOnClickListener(mBtnClickListener);
		mBtnGetVersion.setEnabled(true);
		mBtnGetVersion.setEnabled(true);
		mBtnSetClock.setEnabled(true);
		mBtnSendPackage.setEnabled(true);
    }

    private String getPsamModuleVersion() {
		if(mLibPsam.psam_open()) {
			int ver = mLibPsam.psam_get_version();
			if(ver < 0) {
				Log.v("@M_" + TAG, "Get psam version ERROR!");
				mTxtVersionState.setText("获取PSAM版本错误！！");
			}
			else {
				mTxtVersionState.setText("PSAM模块当前版本为：" + ver);
			}
			mLibPsam.psam_close();

			return Integer.toString(ver);
		}
		else {
			mTxtVersionState.setText("打开PSAM错误！！");
		}
		return null;
	}

	private void setPsamBandrate() {
		if(mLibPsam.psam_open()) {
			int rate = Integer.parseInt(mSpBandRateType.getSelectedItem().toString().trim());
			int ret = mLibPsam.psam_set_bandrate(rate);
			if(ret != 0) {
				Log.v("@M_" + TAG, "Set psam bandrate ERROR!");
				mTxtVersionState.setText("设备PSAM通讯波特率错误！！");
			}
			else {
				mTxtVersionState.setText("设备PSAM通讯波特率‘" + rate + "’成功！！");
			}
			mLibPsam.psam_close();
		}
		else {
			mTxtVersionState.setText("打开PSAM错误！！");
		}
	}

	private void setPsamClock() {
		if(mLibPsam.psam_open()) {
			String sel = mSpClockType.getSelectedItem().toString().trim();
			sel = sel.substring(0, sel.length()-1);
			int clk = Integer.parseInt(sel.trim());
			int ret = mLibPsam.psam_set_clock(clk);
			if(ret != 0) {
				Log.v("@M_" + TAG, "Set psam clock ERROR!");
				mTxtVersionState.setText("设备PSAM时钟错误！！");
			}
			else {
				mTxtVersionState.setText("设备PSAM时钟‘" + clk + "’成功！！");
			}
			mLibPsam.psam_close();
		}
		else {
			mTxtVersionState.setText("打开PSAM错误！！");
		}
	}

	private void sendPackageToCard() {
		if(mLibPsam.psam_open()) {
			String data = mEtSendData.getText().toString();
			String sel = mSpChannelType.getSelectedItem().toString().trim();
			Log.v("@M_" + TAG, "sel="+sel + ",len=" + sel.length());
			//sel = sel.substring(4, sel.length()-4);
			sel = sel.substring(4);
			int sam = Integer.parseInt(sel.trim());
			Log.v("@M_" + TAG, "sam=" + sam);
			byte[] respond = mLibPsam.psam_send_package(data.getBytes(), data.getBytes().length, sam, PSAM_CARD_TYPE);
			if(respond == null) {
//			if(respond.length <= 0) {
				Log.v("@M_" + TAG, "Send Package to Card ERROR!");
				mTxtVersionState.setText("发送数据到"+ sam + "错误！！");
			}
			else {
				mTxtVersionState.setText("发送数据到卡" + sam + "类型为‘" + PSAM_CARD_TYPE + "’成功！！");
				mTxtRespondState.setText("");
			}
			mLibPsam.psam_close();
		}
		else {
			mTxtVersionState.setText("打开PSAM错误！！");
		}
	}
}
