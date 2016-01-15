package com.zhibinw.smsproxy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

import android.net.Uri;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.app.Activity;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//sendhMessage("", "test msg 1 ");
		startService(new Intent(this,SmsService.class));
	}
	@Override
	protected void onDestroy() {
//		startService(new Intent(this,SmsService.class).putExtra(SmsService.FLAG_EXIT, true));
		super.onDestroy();
	}
	

}
