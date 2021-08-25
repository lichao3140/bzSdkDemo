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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.EditText;
import android.widget.TextView;
import android.text.Editable;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.lang.Thread.sleep;


public class Uart_Test extends Activity {

    public static final String ARG_ITEM_ID = "uart_item_id";

    private static final String TAG = "Uart_Test/Activity";
    private static final String TAG_BG = "Uart_Test/BG";

    private static final int HANDLE_UPDATE_DATA = 10000;
    private static final int HANDLE_OP_ERROR = 10001;


    private Button mBtnSend = null;
    private Button mBtnRecieve = null;
    private Button mBtnStop = null;
    private Button mBtnClr = null;
    private OnClickListener mBtnClickListener;

    private TextView mTxtRecieveData = null;
    private EditText mEtSendData = null;

    private Spinner mSpUartPortType;
    ArrayAdapter<CharSequence> adapterPortPattern = null;

    private Spinner mSpUartRateType;
    ArrayAdapter<CharSequence> adapterPattern = null;

    private RadioButton mRadioUartOn;
    private RadioButton mRadioRs485On;

    File mFile = null;
    private static boolean isFlagSerial = false;
    private static SerialPort serialPort = null;
    private static InputStream inputStream = null;
    private static OutputStream outputStream = null;
    private static Thread receiveThread = null;
    private static String strData = "";
    private SerialPort mSerialPort = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("@M_" + TAG, "Enter onCreate  function of Main Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uart_demo);

        mBtnSend = (Button) findViewById(R.id.uart_send_btn);
        mBtnRecieve = (Button) findViewById(R.id.uart_recieve_btn);
        mBtnStop = (Button) findViewById(R.id.uart_stop_btn);
        mBtnClr = (Button) findViewById(R.id.uart_clr_btn );

        mTxtRecieveData = (TextView) findViewById(R.id.uart_recieve_data_content);
        mTxtRecieveData.setText("");
        mEtSendData = (EditText) findViewById(R.id.uart_tx_string_content);

        mSpUartPortType = (Spinner) findViewById(R.id.uart_port_spi);		
        adapterPortPattern = ArrayAdapter.createFromResource(this,
                R.array.uart_port_name,
                android.R.layout.simple_spinner_item);
        adapterPortPattern
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpUartPortType.setAdapter(adapterPortPattern);

        mSpUartRateType = (Spinner) findViewById(R.id.uart_rate_rw_type);		
        adapterPattern = ArrayAdapter.createFromResource(this,
                R.array.uart_baudrate,
                android.R.layout.simple_spinner_item);
        adapterPattern
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpUartRateType.setAdapter(adapterPattern);

        mRadioUartOn = (RadioButton) findViewById(R.id.uart_mode_enable_radio);
        mRadioRs485On = (RadioButton) findViewById(R.id.rs485_mode_enable_radio);
		mRadioUartOn.setChecked(true);
/*
        try {
            SerialPort mSerialPort = new SerialPort(mFile, 115200, 0);
        } catch (IOException e) {
            Toast.makeText(this, "invalid input value",
                    Toast.LENGTH_SHORT).show();
            return;
        }
*/
        mBtnClickListener = new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				Bundle extras = new Bundle();
				if (v == (View) mBtnSend) {
					Log.v("@M_" + TAG, "send button is pressed");
/*
					mTxtRecieveData.setText("RATE:"+Integer.parseInt(mSpUartRateType
                            .getSelectedItem().toString().trim())
                            +"Port Type:"+ (mRadioUartOn.isChecked() ? "UART" : "RS485")
					);
 */
                    mBtnSend.setEnabled(false);
                    mBtnRecieve.setEnabled(false);
                    uartSendDataTest();
                    mBtnSend.setEnabled(true);
                    mBtnRecieve.setEnabled(true);
				} else if (v == (View) mBtnRecieve) {
					Log.v("@M_" + TAG, "recieve button is pressed");
/*
					mTxtRecieveData.setText(mEtSendData.getText());
 */
                    mBtnSend.setEnabled(false);
                    mBtnRecieve.setEnabled(false);
                    uartReadDataTest();
                } else if (v == (View) mBtnStop) {
                    Log.v("@M_" + TAG, "Stop button is pressed");
/*
					mTxtRecieveData.setText(mEtSendData.getText());
 */
                    uartStopDataTest();
                    mBtnSend.setEnabled(true);
                    mBtnRecieve.setEnabled(true);
				} else if(v==(View)mBtnClr){
                    Log.v("@M_" + TAG, "Clear button is pressed");
                    mTxtRecieveData.setText("");
                }else {
					return;
				}
			}
		};

        mBtnSend.setOnClickListener(mBtnClickListener);
        mBtnRecieve.setOnClickListener(mBtnClickListener);
        mBtnStop.setOnClickListener(mBtnClickListener);
        mBtnClr.setOnClickListener(mBtnClickListener);
        mBtnSend.setEnabled(true);
        mBtnRecieve.setEnabled(true);
        mBtnStop.setEnabled(true);
        mBtnClr.setEnabled(true);
    }

    private void uartSendDataTest() {
		/* first get the rate and port info */
		String dev_path =  mSpUartPortType.getSelectedItem().toString();
		int dev_rate = Integer.parseInt(mSpUartRateType.getSelectedItem().toString().trim());
		Editable content = mEtSendData.getText();
		/* clear the prompt info */
        mTxtRecieveData.setTextColor(Color.BLACK);
        mTxtRecieveData.setText("");
		if((content != null) && (content.length() > 0)) {
			try {
                if(mSerialPort != null)
                    mSerialPort.close();
                mSerialPort = new SerialPort(new File(dev_path), dev_rate, 0);
                if(mRadioRs485On.isChecked())
                    mSerialPort.EnableRS485();
			    mSerialPort.getOutputStream().write(content.toString().getBytes());
			    mSerialPort.close();
                mSerialPort = null;
			} catch (IOException e) {
			    Toast.makeText(this, "Can not open the uart device",
			            Toast.LENGTH_SHORT).show();
                mTxtRecieveData.setTextColor(Color.RED);
                mTxtRecieveData.setText("Operating port: "+ dev_path + "  Exception!!");
			    return;
			}
		}
		else
		{
			mTxtRecieveData.setTextColor(Color.RED);
			mTxtRecieveData.setText("Send buffer is Empty or it is invalid!!");

		}
    }


    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
            case HANDLE_UPDATE_DATA: {
                String res = msg.obj.toString();
                mTxtRecieveData.append(res);
                mBtnSend.setEnabled(true);
//                mBtnRecieve.setEnabled(true);

            }
                break;
            case HANDLE_OP_ERROR: {
                String res = msg.obj.toString();
                mTxtRecieveData.setText(res);
                mBtnSend.setEnabled(true);
                mBtnRecieve.setEnabled(true);
                break;
            }
            default:
                break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 接收串口数据的方法
     */
    public void receive() {
        if (receiveThread != null && !isFlagSerial) {
            return;
        }
        receiveThread = new Thread() {
            @Override
            public void run() {
                while (isFlagSerial) {
                    try {
                        byte[] readData = new byte[256];
                        if (inputStream == null) {
                            return;
                        }
                        int size = inputStream.read(readData);
                        if (size > 0 && isFlagSerial) {
                            strData = new String(readData);
                            /*Message msg = new Message();
                            msg.what = HANDLE_UPDATE_DATA;
                            msg.obj = strData;
                            mHandler.sendMessage(msg);*/
                            mTxtRecieveData.append(strData);
                            Log.i("receive()", "readSerialData:" + strData);
                        }
//                        Thread.sleep(0x01);
                    } catch (IOException e) {
                        e.printStackTrace();
                        String err = new String("UART READ Exception!!");
                        Message msg = new Message();
                        msg.what = HANDLE_OP_ERROR;
                        msg.obj = err;
                        mHandler.sendMessage(msg);
                        isFlagSerial = false;
//                    } catch (InterruptedException e) {
//                    }
                    }
                }
                if(mSerialPort != null)
                    mSerialPort.close();
                mSerialPort = null;
                isFlagSerial = false;
            }
        };
        receiveThread.start();
    }

    private void uartReadDataTest() {
        String dev_path =  mSpUartPortType.getSelectedItem().toString();
        int dev_rate = Integer.parseInt(mSpUartRateType.getSelectedItem().toString().trim());
        /* clear the prompt info */
        mTxtRecieveData.setTextColor(Color.BLACK);
        mTxtRecieveData.setText("");

        try {
            if(mSerialPort != null)
                mSerialPort.close();
            mSerialPort = new SerialPort(new File(dev_path), dev_rate, 0);
            if(mRadioRs485On.isChecked())
                mSerialPort.EnableRS485();
            inputStream = mSerialPort.getInputStream();
            isFlagSerial = true;
            receive();
        } catch (IOException e) {
            mTxtRecieveData.setTextColor(Color.RED);
            mTxtRecieveData.setText("Operating port: "+ dev_path + "  Exception!!");
            return;
        }
    }

    private void uartStopDataTest() {
        if(isFlagSerial)
            isFlagSerial = false;
//        receiveThread.stop();

        try {
//            if(inputStream != null)
//                inputStream.close();
            sleep(0x1);
/*
        } catch (IOException  e) {
            mTxtRecieveData.setText("Operating port:  Exception!!");
 */
        } catch ( InterruptedException e) {
            mTxtRecieveData.setText("Operating port:  Exception!!");
        }

        if(mSerialPort != null)
            mSerialPort.close();
        mSerialPort = null;
    }
}
