package com.voler.theirprivacy;

import android.app.Application;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

import cn.bmob.v3.Bmob;

/**
 * Created by Henry on 2016/11/27.
 */

public class MyApplacation extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        //第一：默认初始化.bmob云数据库
        Bmob.initialize(this, "2cbf044b0ebaa1b8d2acea05f19a22d7");

        //百度推送
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "Gk5Z2BE1lbjzYtgnI1KzqCfo");

    }
}
