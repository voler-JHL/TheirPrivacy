package com.voler.theirprivacy.receiver;

import android.content.Context;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.voler.theirprivacy.activity.MyMainActivity;

import java.lang.ref.WeakReference;
import java.util.List;

public class PushReceiver extends PushMessageReceiver {

    private static WeakReference<MyMainActivity> mMainActivity;

    public static void setActivity(WeakReference<MyMainActivity> mainActivityWeakReference) {
        PushReceiver.mMainActivity = mainActivityWeakReference;
    }


    @Override
    public void onBind(Context context, int i, String s, String s1, String s2, String s3) {
        String responseString = "onBind errorCode =  " + i + "  appid =  " + s + "  userId =  " + s1 + "  channeId =  " + s2 + "  requesId =  " + s3;

        //获取channelId
        String cId = s2;

        Log.i("push",responseString);

    }


    @Override
    public void onUnbind(Context context, int i, String s) {

        Log.i("push","unbind");
    }

    @Override
    public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {
        Log.i("push","setTag");
    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {

        Log.i("push","delTag");
    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {

    }


    @Override
    public void onMessage(Context context, String s, String s1) {

        Log.i("push","onmessage:"+s);
        /*if (s != null && s != "") {
            int a = 0;
            switch (a) {
                case 0:
                    //问询消息
                    // 存储最新消息到本地
                    UserManager.setMsg(s, "0");
                    break;
                case 1:
                    //评价消息
                    break;
                case 2:
                    //订单消息
                    break;
                case 3:
                    //通知消息
                    break;

                default:
                    break;
            }
        }*/

    }

    @Override
    public void onNotificationClicked(Context context, String s, String s1, String s2) {
        Log.i("push","clicked:"+s+s1+s2);

    }


    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {

        Log.i("push","arrived:"+s+s1+s2);
    }


}
