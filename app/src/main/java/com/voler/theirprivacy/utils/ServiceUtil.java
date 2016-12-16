package com.voler.theirprivacy.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.ArrayList;

/**
 * 三尺春光驱我寒，一生戎马为长安
 * Created by Han on 16/12/15.
 */

public class ServiceUtil {
    //本方法判断自己的一个Service是否在运行
    public static boolean isWorked(Context context, String serviceName) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(30);
        if (runningService.size() <= 0) {
            return false;
        }
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
}
