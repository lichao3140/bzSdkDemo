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
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class NFC_Test extends Activity {

    public static final String ARG_ITEM_ID = "nfc_item_id";

    private static final String TAG = "NFC_Test/Activity";
    private static final String TAG_BG = "NFC_Test/BG";
	private static final int PSAM_CARD_TYPE = 9600;

	//    private Button mBtnGetVersion = null;
	private Button mBtnClear = null;
	private OnClickListener mBtnClickListener;
	private TextView mTxtCardINfo = null;

	public static NfcAdapter mNfcAdapter;
	public static IntentFilter[] mIntentFilter = null;
	public static PendingIntent mPendingIntent = null;
	public static String[][] mTechList = null;
	private IntentFilter[] mFilters;
	private StringBuffer msgBuffer;
	private NfcAdapter mAdapter;
	String[][] mTechLists;

//	public NFC_Test(Activity activity) {
//		mNfcAdapter = NfcCheck(this);
//		NfcInit(this);
//	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("@M_" + TAG, "Enter onCreate  function of Main Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_demo);
//		Toast.makeText(this, "此项功能暂时无效！！", Toast.LENGTH_SHORT).show();
//		finish();

		mBtnClear = (Button) findViewById(R.id.button_clearcardinfo);
		mTxtCardINfo = (TextView) findViewById(R.id.textView_cardinfo);
		mTxtCardINfo.setText("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

		msgBuffer = new StringBuffer();
		mAdapter = NfcAdapter.getDefaultAdapter(this);
		mPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		try {
			ndef.addDataType("*/*");

		} catch (IntentFilter.MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		mFilters = new IntentFilter[]{ndef,};
		mTechLists = new String[][]{{IsoDep.class.getName()}, {NfcA.class.getName()},};
		Log.d(" mTechLists", NfcF.class.getName() + mTechLists.length);

		if (mAdapter == null) {
			Toast.makeText(this, "设备不支持NFC！", Toast.LENGTH_LONG).show();
			msgBuffer.append("\r\n").append("设备不支持NFC！");
			handler.sendEmptyMessage(0);
			return;
		}else {
			msgBuffer.append("\r\n").append("设备支持NFC！");
			handler.sendEmptyMessage(0);
		}
		if (!mAdapter.isEnabled()) {
			Toast.makeText(this, "请在系统设置中先启用NFC功能！", Toast.LENGTH_LONG).show();
			return;
		}

        mBtnClickListener = new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				Bundle extras = new Bundle();
				if (v == (View) mBtnClear) {
					Log.v("@M_" + TAG, "Start mBtnClear is pressed");
					mTxtCardINfo.setText("");
//				} else if (v == (View) mBtnSendPackage) {
//					Log.v("@M_" + TAG, "Stop mBtnSendPackage is pressed");
				} else {
					return;
				}
			}
		};

		mBtnClear.setOnClickListener(mBtnClickListener);
		mBtnClear.setEnabled(true);
    }


	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};

	private void checkNFCFunction() {
		// TODO Auto-generated method stub
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		// check the NFC adapter first
		if (mNfcAdapter == null) {
			// mTextView.setText("NFC apdater is not available");
			Dialog dialog = null;
			AlertDialog.Builder customBuilder = new AlertDialog.Builder(
					this);
			customBuilder
					.setTitle("很遗憾")
					.setMessage("没发现NFC设备，请确认您的设备支持NFC功能!")
//					.setIcon(R.drawable.ic_banner)
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int which) {
									dialog.dismiss();
									finish();
								}
							});
			dialog = customBuilder.create();
			dialog.setCancelable(false);// back
			dialog.setCanceledOnTouchOutside(false);
			SetDialogWidth(dialog).show();
			return;
		} else {
			if (!mNfcAdapter.isEnabled()) {
				Dialog dialog = null;
				AlertDialog.Builder customBuilder = new AlertDialog.Builder(
						this);
				customBuilder
						.setTitle("提示")
						.setMessage("请确认NFC功能是否开启!")
//						.setIcon(R.drawable.ic_banner)
						.setPositiveButton("现在去开启......",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
														int which) {
										dialog.dismiss();
										Intent setnfc = new Intent(
												Settings.ACTION_NFC_SETTINGS);
										startActivity(setnfc);
									}
								});
				dialog = customBuilder.create();
				dialog.setCancelable(false);// back
				dialog.setCanceledOnTouchOutside(false);
				SetDialogWidth(dialog).show();
				return;
			}
		}
	}


	private Dialog SetDialogWidth(Dialog dialog) {
		// TODO 自动生成的方法存根
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		if (screenWidth > screenHeight) {
			params.width = (int) (((float) screenHeight) * 0.875);

		} else {
			params.width = (int) (((float) screenWidth) * 0.875);
		}
		dialog.getWindow().setAttributes(params);

		return dialog;
	}


	@Override
	protected void onNewIntent(Intent intent) {
		// TODO 自动生成的方法存根
//		super.onNewIntent(intent);
		String action = intent.getAction();
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		String CardId =ByteArrayToHexString(tag.getId());
		mTxtCardINfo.append("\nCARD ID: "+CardId+"\n");

		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
			MifareClassic mifareClassic = MifareClassic.get(tag);
			try
			{
				mifareClassic.connect();
				//获取扇区数量
				int count = mifareClassic.getSectorCount();
				Log.e("onNewIntent:", "扇区数量 ===" + count);
				//用于判断时候有内容读取出来
				for (int i = 0; i < count; i++) {
					boolean isOpen = mifareClassic.authenticateSectorWithKeyA(i, MifareClassic.KEY_DEFAULT);
					if (isOpen) {
						//获取扇区里面块的数量
						int bCount = mifareClassic.getBlockCountInSector(i);
						Log.e("onNewIntent:", "扇区里面块的数量 ===" + bCount);
						//获取扇区第一个块对应芯片存储器的位置
						//存储器的位置为第一扇区为0后面叠加4直到60为止
						int bIndex = mifareClassic.sectorToBlock(i);
						for (int j = 0; j < bCount; j++) {
							Log.e("onNewIntent:", "存储器的位置 ===" + bIndex + "当前块 === " + (bIndex + j));
							byte[] data = mifareClassic.readBlock(bIndex + j);//进行了读卡
							msgBuffer.append("块" + (bIndex + j) + "数据:").append(ByteArrayToHexString(data)).append("\r\n");
							handler.sendEmptyMessage(0);
							Log.e("数据", "第" + (bIndex + j) + "块" + ByteArrayToHexString(data));

							//修改KeyA和KeyB
							if ((bIndex + j) == (4 * i + 3)) {
								//将所有扇区的最后一个Block修改为111111111111ff078069111111111111
								mifareClassic.writeBlock(bIndex + j, new byte[]{(byte) 0x11, (byte) 0x11, (byte) 0x11, (byte) 0x11, (byte) 0x11, (byte) 0x11, (byte) 0xff, 0x07, (byte) 0x80, (byte) 0x69, (byte) 0x11, (byte) 0x11, (byte) 0x11, (byte) 0x11, (byte) 0x11, (byte) 0x11});
								Log.e("onNewIntent:", (bIndex + j) + "块加密成功");

							}

						}
					} else {
						msgBuffer.append("第" + (i + 1) + "扇区" + "验证新卡密码失败\r\n");
						handler.sendEmptyMessage(0);
						Log.e("失败 ", "验证密码");
					}
				}
			} catch (IOException e){
				Log.e("错误", "IO错误!");
			}

		}
	}

	/**
	 * 检查NFC是否打开
	 */
	public static NfcAdapter NfcCheck(Activity activity) {
		NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
		if (mNfcAdapter == null) {
			return null;
		} else {
			if (!mNfcAdapter.isEnabled()) {
				Intent setNfc = new Intent(Settings.ACTION_NFC_SETTINGS);
				activity.startActivity(setNfc);
			}
		}
		return mNfcAdapter;
	}

	/**
	 * 初始化nfc设置
	 */
	public static void NfcInit(Activity activity) {
		mPendingIntent = PendingIntent.getActivity(activity, 0, new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		IntentFilter filter2 = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		try {
			filter.addDataType("*/*");
		} catch (IntentFilter.MalformedMimeTypeException e) {
			e.printStackTrace();
		}
		mIntentFilter = new IntentFilter[]{filter, filter2};
		mTechList = null;
	}

	/**
	 * 读取NFC的数据
	 */
	public static String readNFCFromTag(Intent intent) throws UnsupportedEncodingException {
		Parcelable[] rawArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (rawArray != null) {
			NdefMessage mNdefMsg = (NdefMessage) rawArray[0];
			NdefRecord mNdefRecord = mNdefMsg.getRecords()[0];
			if (mNdefRecord != null) {
				String readResult = new String(mNdefRecord.getPayload(), "UTF-8");
				return readResult;
			}
		}
		return "数据为空";
	}

	/**
	 * 往nfc写入数据
	 */
	public static void writeNFCToTag(String data, Intent intent) throws IOException, FormatException {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		Ndef ndef = Ndef.get(tag);
		ndef.connect();
		NdefRecord ndefRecord = NdefRecord.createTextRecord(null, data);
		NdefRecord[] records = {ndefRecord};
		NdefMessage ndefMessage = new NdefMessage(records);
		ndef.writeNdefMessage(ndefMessage);
	}

	/**
	 * 读取nfcID
	 */
	public static String readNFCId(Intent intent) throws UnsupportedEncodingException {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		String id = ByteArrayToHexString(tag.getId());
		return id;
	}

	/**
	 * 将字节数组转换为字符串
	 */
	private static String ByteArrayToHexString(byte[] inarray) {
		int i, j, in;
		String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
		String out = "";

		for (j = 0; j < inarray.length; ++j) {
			in = (int) inarray[j] & 0xff;
			i = (in >> 4) & 0x0f;
			out += hex[i];
			i = in & 0x0f;
			out += hex[i];
		}
		return out;
	}

	@Override
	protected void onResume() {
		super.onResume();
		enableForegroundDispatch();
	}

	private void enableForegroundDispatch() {
		if (mAdapter != null) {
			mAdapter.enableForegroundDispatch(this, mPendingIntent,
					mFilters, mTechLists);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		disableForegroundDispatch();
	}

	private void disableForegroundDispatch() {
		// TODO 自动生成的方法存根
		if (mAdapter != null) {
			mAdapter.disableForegroundDispatch(this);
		}
	}

}
