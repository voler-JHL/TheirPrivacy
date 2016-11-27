package com.voler.theirprivacy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.voler.theirprivacy.service.DaemonService;
import com.voler.theirprivacy.service.RemoteService;
import com.voler.theirprivacy.service.ServiceOne;
import com.voler.theirprivacy.service.ServiceTwo;

import java.util.ArrayList;

/**
 * Created by Henry on 2016/11/27.
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent One = new Intent();
        One.setClass(SplashActivity.this, DaemonService.class);
        startService(One);

        Intent two = new Intent();
        two.setClass(SplashActivity.this, RemoteService.class);
        startService(two);

        Intent serviceOne = new Intent();
        serviceOne.setClass(this, ServiceOne.class);
        startService(serviceOne);

        Intent serviceTwo = new Intent();
        serviceTwo.setClass(this, ServiceTwo.class);
        startService(serviceTwo);
    }

    public static boolean isServiceWorked(Context context, String serviceName) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
}
