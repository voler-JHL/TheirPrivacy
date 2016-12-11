package com.voler.theirprivacy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.voler.theirprivacy.service.WeatherService;

public class WakeReceiver extends BroadcastReceiver {

    private final static String TAG = WakeReceiver.class.getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "wake !! wake !! ");

        Intent wakeIntent = new Intent(context, WeatherService.class);
        context.startService(wakeIntent);
    }

}
