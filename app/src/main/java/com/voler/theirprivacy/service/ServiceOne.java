package com.voler.theirprivacy.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.voler.theirprivacy.SplashActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Henry on 2016/11/27.
 */

public class ServiceOne extends Service{
    public final static String TAG = "ServiceOne";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");

        thread.start();
        return START_STICKY;
    }

    Thread thread = new Thread(new Runnable() {

        @Override
        public void run() {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    Log.e(TAG, "ServiceOne Run: "+System.currentTimeMillis());
                    boolean b = SplashActivity.isServiceWorked(ServiceOne.this, "com.example.servicedemo.ServiceTwo");
                    if(!b) {
                        Intent service = new Intent(ServiceOne.this, ServiceTwo.class);
                        startService(service);
                        Log.e(TAG, "Start ServiceTwo");
                    }
                }
            };
            timer.schedule(task, 0, 1000);
        }
    });

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
