package com.voler.theirprivacy.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.voler.theirprivacy.AppContact;
import com.voler.theirprivacy.IProcessService;

/**
 * Created by Henry on 2016/11/27.
 */

public class RemoteService extends Service {
    String TAG = "RemoteService";
    private ServiceBinder mServiceBinder;

    private RemoteServiceConnection mRemoteServiceConn;

    @Override
    public void onCreate() {
        super.onCreate();

        mServiceBinder = new ServiceBinder();

        if (mRemoteServiceConn == null) {
            mRemoteServiceConn = new RemoteServiceConnection();
        }

        Log.i(TAG, TAG + " onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, TAG + " onStartCommand");

        bindService(new Intent(this, DaemonService.class), mRemoteServiceConn, Context.BIND_IMPORTANT);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mServiceBinder;
    }

    /**
     * 通过AIDL实现进程间通信
     */
    class ServiceBinder extends IProcessService.Stub {
        @Override
        public String getServiceName() throws RemoteException {
            return "RemoteService";
        }
    }


    /**
     * 连接远程服务
     */
    class RemoteServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {

                IProcessService process = IProcessService.Stub.asInterface(service);
                Log.i(TAG, "连接" + process.getServiceName() + "服务成功");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // RemoteException连接过程出现的异常，才会回调,unbind不会回调

            startService(new Intent(RemoteService.this, DaemonService.class));
            Intent blackIntent = new Intent();
            blackIntent.setAction(AppContact.BLACK_WAKE_ACTION);
            sendBroadcast(blackIntent);

            bindService(new Intent(RemoteService.this, DaemonService.class), mRemoteServiceConn, Context.BIND_IMPORTANT);
        }
    }
}
