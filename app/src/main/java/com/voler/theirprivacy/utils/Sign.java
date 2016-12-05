package com.voler.theirprivacy.utils;

import android.content.Context;
import android.util.Log;

import com.tencent.cos.COSClient;
import com.tencent.cos.COSClientConfig;
import com.tencent.cos.common.COSEndPoint;

import java.util.Random;


/**
 * 三尺春光驱我寒，一生戎马为长安
 * Created by Han on 16/12/1.
 */

public class Sign {
    // 腾讯云注册的appid
    String appid = "1252925913";
    String peristenceId = "持久化Id";
    // 设置要操作的bucket
    String bucket = "theirprivacy";

    String secretId = "AKIDpIbiGgX0sZDj5Gi2taEpGVZR7iBS4L1W";
    String secretKey = "WajojleP0OabQjtsETZXJ9sPlurcGpGV";

    /**
     * cos的用户接口
     */
    protected COSClient cos;

    /**
     * cosclient 配置设置; 根据需要设置；
     */
    protected COSClientConfig config;

    /**
     * 设置园区；根据创建的cos空间时选择的园区
     * 华南园区：COSEndPoint.COS_GZ(已上线)
     * 华北园区：COSEndPoint.COS_TJ(已上线)
     * 华东园区：COSEndPoint.COS_SH
     * 此处Demo中选择了 华东园区：COSEndPoint.COS_SH用于测试
     */
    protected COSEndPoint cosEndPoint;


    public static Sign instance;

    private Sign(Context context) {
        config = new COSClientConfig();
        cosEndPoint = COSEndPoint.COS_TJ;
        config.setEndPoint(cosEndPoint);
        cos = new COSClient(context, appid, config, ""+System.currentTimeMillis());
    }

    public static Sign getInstance(Context context) {
        if (instance == null) {
            synchronized (Sign.class) {
                instance = new Sign(context);
            }
        }
        return instance;
    }

    public String getBucket() {
        return bucket;
    }

    public COSClient getCOSClient() {
        return cos;
    }


    /**
     * 签名，此处使用多次签名
     */
    public String getSign() {
        String original = "a=" + appid + "&b=" + bucket + "&k=" + secretId + "&e=" + System.currentTimeMillis() / 1000 + "&t=770000&r=" + rand() + "&f=";
        String sign = "";
        try {
            byte[] hmacDigest = HMACSHA1.HmacSHA1Encrypt(original, secretKey);
            byte[] signContent = new byte[hmacDigest.length + original.getBytes().length];
            System.arraycopy(hmacDigest, 0, signContent, 0, hmacDigest.length);
            System.arraycopy(original.getBytes(), 0, signContent, hmacDigest.length, original.getBytes().length);
            sign = Base64.encode(signContent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.getClass().getName(), "签名异常");
        }
        return sign;
    }

    private String rand() {
        int rand = 100000000 + new Random().nextInt(900000000);
        return "" + rand;
    }

}
