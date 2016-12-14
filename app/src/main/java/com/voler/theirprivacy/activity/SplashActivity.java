package com.voler.theirprivacy.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.voler.theirprivacy.AppContact;
import com.voler.theirprivacy.R;
import com.voler.theirprivacy.weather.ui.activity.WeatherActivity;

import java.util.ArrayList;

/**
 * Created by Henry on 2016/11/27.
 */

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        Intent One = new Intent();
//        One.setClass(SplashActivity.this, DaemonService.class);
//        startService(One);
//
//        Intent two = new Intent();
//        two.setClass(SplashActivity.this, RemoteService.class);
//        startService(two);

        Intent blackIntent = new Intent();
        blackIntent.setAction(AppContact.BLACK_WAKE_ACTION);
        sendBroadcast(blackIntent);


        startActivity(new Intent(SplashActivity.this,WeatherActivity.class));
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
