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
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;


public class BarCode_Scan_Test extends Activity {

    public static final String ARG_ITEM_ID = "barcode_item_id";
    private static final String TAG = "BarCode_Scan_Test/Activity";
    private static final String TAG_BG = "BarCode_Scan_Test/BG";

	private  static final  int TEXT_MAX = 1600;
	private  static final  int MAX_HID_LENGTH= TEXT_MAX;
	private  static final  int USB_VENDOR_ID = 0x0525;
	private  static final  int USB_PRODUCT_ID = 0x7a42;
	private	 static final  int HANDLE_UPDATE_DATA = 60000;
	private  static final  int HANDLE_OP_ERROR = 60001;


	private Button mBtnCleanResult = null;
    private Button mBtnScan = null;
	private Button mBtnStop = null;
    private OnClickListener mBtnClickListener;

	private TextView mTxtOperationInfo = null;
	private TextView mTxtResultBarCode = null;

	private LibBarCode mLibBarCode = new LibBarCode();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("@M_" + TAG, "Enter onCreate  function of BarCode Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_scan_demo);

		mBtnCleanResult = (Button) findViewById(R.id.button_list_usbdevices);
		mBtnScan = (Button) findViewById(R.id.button_scan_barcode);
		mBtnStop = (Button) findViewById(R.id.button_stop_scan);

		mTxtResultBarCode = (TextView) findViewById(R.id.textView_barcode_result);
		mTxtResultBarCode.setText("");

		mTxtOperationInfo = (TextView) findViewById(R.id.textView_barcode_prompt);
		mTxtOperationInfo.setText("点击'开始读条码'来启动扫码功能！");

        mBtnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle extras = new Bundle();
				if (v == (View) mBtnScan) {
					Log.v("@M_" + TAG, "Start scan the bar code");
					toScanBarCode();
					mTxtOperationInfo.setText("正的等待扫码......");
//					mBtnScan.setEnabled(false);
					mBtnStop.setEnabled(true);
				} else if (v == (View) mBtnStop) {
					Log.v("@M_" + TAG, "Stop to scan bar code");
					toStopScan();
					mTxtOperationInfo.setText("扫码已停止！！");
					mBtnScan.setEnabled(true);
					mBtnStop.setEnabled(false);
				} else if (v == (View) mBtnCleanResult) {
					Log.v("@M_" + TAG, "Clean the barcode Result");
					mTxtResultBarCode.setText("");
				} else {
					return;
				}
			}
		};
		mBtnScan.setOnClickListener(mBtnClickListener);
		mBtnStop.setOnClickListener(mBtnClickListener);
		mBtnCleanResult.setOnClickListener(mBtnClickListener);
		mBtnCleanResult.setEnabled(true);
		mBtnScan.setEnabled(true);
		mBtnStop.setEnabled(false);
    }


	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
				case HANDLE_UPDATE_DATA: {
					String res = msg.obj.toString();
					mTxtResultBarCode.append(res);
				}
				break;
				case HANDLE_OP_ERROR: {
					String res = msg.obj.toString();
					mTxtResultBarCode.append(res);
					break;
				}
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};

	private void toScanBarCode() {

		if(mLibBarCode.init_barcode())
		{
			byte[] tmp = new String("asdfasdfasdfas").getBytes();
//			boolean ret = mLibBarCode.barcode_write(tmp);
		}

		mLibBarCode.barCodeRead(new LibBarCode.OnBarCodeProgressListener() {
//			public int onProgressChange(final String barcode, final int len) {
			public int onProgressChange(final byte[] barcode, final int len) {

				Log.v("@M_" + TAG, "barcode size: " + barcode.length);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						String info = new String("");
						info += "len=" + len + "\n";
						info += "barcode.length=" + barcode.length + "\n";
						for(int i = 0 ; i < barcode.length; i ++)
							info += "0x" + Integer.toHexString(barcode[i]) + ", ";
						mTxtResultBarCode.append(info);
//						mTxtResultBarCode.append("\n");
//						mTxtResultBarCode.append(new String(barcode));
					}
				});
				return 0;
			}
		});
	}

	private void toStopScan() {
		toCloseBarCode();
		mTxtResultBarCode.setText("");

	}

	private void toCloseBarCode() {

		if (mLibBarCode.close_barcode()) {

		}
	}
	@Override
	protected void onDestroy() {
		toCloseBarCode();
		super.onDestroy();
	}
}
