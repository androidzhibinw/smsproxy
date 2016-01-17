
package com.zhibinw.smsproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SmsService extends Service {

    public static Thread mSockClientThread;
    public static boolean mQuit = false;
    private static final int MSG_SMS = 1234;
    private static final int MSG_TOAST = 2345;
    private static final int MSG_EXIT = 6789;


    private ServiceHandler mServiceHandler;
    private boolean DEBUG = true;
    public static final String FLAG_EXIT = "EXIT";
    public MainHandler mHandler;
    private HandlerThread mWorkerThread;
    private Context mContext;

    private void debug(String msg) {
        if (DEBUG) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void postDebug(String msg) {
        if (DEBUG) {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_TOAST, msg));
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
        mContext = this;
        mWorkerThread = new HandlerThread("Work Thread");
        mWorkerThread.start();
        mServiceHandler = new ServiceHandler(mWorkerThread.getLooper());
        mHandler = new MainHandler(this.getMainLooper());
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        debug("SmsService onDestroy");

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getBooleanExtra(FLAG_EXIT, false)) {
            mWorkerThread.getLooper().quit();
            mSockClientThread.interrupt();
            mHandler.sendEmptyMessageDelayed(MSG_EXIT, 3000);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void processServRequest(String request) {
        mServiceHandler.sendMessage(mServiceHandler.obtainMessage(MSG_SMS,
                request));
    }

    private void sendsms(final String obj) {
        sendhMessage("18601618863", obj);
        postDebug("send sms" + obj);
    }

    public void sendhMessage(String phoneNumber, String textMessage) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, textMessage, null, null);
    }

    private final class MainHandler extends Handler {

        public MainHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            // debug("ServiceHandler receive msg: " + msg);
            switch (msg.what) {
                case MSG_TOAST:
                    Toast.makeText(mContext, ((String) msg.obj), Toast.LENGTH_SHORT).show();
                    break;
                case MSG_EXIT:
                    stopSelf();
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // debug("ServiceHandler receive msg: " + msg);
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
                postDebug("Socket Created!");
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                while (true) {
                    postDebug("going reading line..");
                    String str = input.readLine();
                    postDebug("received:" + str);
                    processServRequest(str);
                }
            } catch (Exception e) {
                e.printStackTrace();
                postDebug("ClientThread exception: " + e);
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}
