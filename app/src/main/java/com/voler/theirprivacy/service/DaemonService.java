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

import com.voler.theirprivacy.IProcessService;

/**
 * Created by Henry on 2016/11/27.
 */

public class DaemonService extends Service {
    String TAG = "DaemonService";

    private DaemonBinder mDaemonBinder;

    private DaemonServiceConnection mDaemonServiceConn;

    public DaemonService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDaemonBinder = new DaemonBinder();

        if (mDaemonServiceConn == null) {
            mDaemonServiceConn = new DaemonServiceConnection();
        }

        Log.i(TAG, TAG + " onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, TAG + " onStartCommand");
        bindService(new Intent(this, RemoteService.class), mDaemonServiceConn, Context.BIND_IMPORTANT);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mDaemonBinder;
    }

    /**
     * 通过AIDL实现进程间通信
     */
    class DaemonBinder extends IProcessService.Stub {
        @Override
        public String getServiceName() throws RemoteException {
            return TAG;
        }
    }

    /**
     * 连接远程服务
     */
    class DaemonServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                // 与远程服务通信
                IProcessService process = IProcessService.Stub.asInterface(service);
                Log.i(TAG, "连接" + process.getServiceName() + "服务成功");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // RemoteException连接过程出现的异常，才会回调,unbind不会回调
            startService(new Intent(DaemonService.this, RemoteService.class));
            bindService(new Intent(DaemonService.this, RemoteService.class), mDaemonServiceConn, Context.BIND_IMPORTANT);
        }
    }
}
