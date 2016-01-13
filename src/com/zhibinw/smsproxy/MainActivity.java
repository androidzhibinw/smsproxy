package com.zhibinw.smsproxy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.telephony.SmsManager;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//startService(new Intent(this,SmsService.class));
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage("10086", null, "10086", null, null);

	}

}
