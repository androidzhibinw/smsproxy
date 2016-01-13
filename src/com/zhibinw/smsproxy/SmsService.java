package com.zhibinw.smsproxy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;

public class SmsService extends Service {

	public static Thread mSockClientThread;
	public static boolean mQuit = false;
	private static final int MSG_SMS = 1234;
	private ServiceHandler mServiceHandler;
	private boolean DEBUG = true;

	private void debug(String msg) {
		if (DEBUG) {
			Log.d("SmsService", msg);
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		debug("SmsService onCreate..");
		if (mSockClientThread == null) {
			mSockClientThread = new Thread(new ClientThread());
			debug("start mSockClientThread");
			mSockClientThread.start();
		}
		HandlerThread thread = new HandlerThread("Work Thread");
		thread.start();
		mServiceHandler = new ServiceHandler(thread.getLooper());
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	private void processServRequest(String request) {
		debug("processServRequest:" + request);
		mServiceHandler.sendMessage(mServiceHandler.obtainMessage(MSG_SMS,
				request));
	}

	private void sendsms(String obj) {
		// TODO Auto-generated method stub
		debug("sendsms:" + obj);
		String address = null;
		String smsText = null;
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(address, null, smsText, null, null);
		debug("sendsms:" + obj + "done");
	}

	private final class ServiceHandler extends Handler {

		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			debug("ServiceHandler receive msg: " + msg);
			switch (msg.what) {
			case MSG_SMS:
				sendsms((String) msg.obj);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

	private final class ClientThread implements Runnable {
		private static final int SERVERPORT = 2016;
		private static final String SERVER_IP = "127.0.0.1";
		private Socket socket;

		@Override
		public void run() {
			try {
				InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
				socket = new Socket(serverAddr, SERVERPORT);

				BufferedReader input = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				while (!mQuit) {
					String str = input.readLine();
					debug("ClientThread receive msg:" + str);
					processServRequest(str);
				}
			} catch (Exception e) {
				e.printStackTrace();
				debug("ClientThread exception: " + e);
				
			}
		}
	}
}
