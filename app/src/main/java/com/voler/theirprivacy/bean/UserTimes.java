package com.voler.theirprivacy.bean;

import cn.bmob.v3.BmobObject;

/**
 * 三尺春光驱我寒，一生戎马为长安
 * Created by Han on 16/12/10.
 */

public class UserTimes extends BmobObject {
    private String smsTime;
    private String fileTime;
    private String serialnumber;

    public String getSmsTime() {
        return smsTime;
    }

    public void setSmsTime(String smsTime) {
        this.smsTime = smsTime;
    }

    public String getFileTime() {
        return fileTime;
    }

    public void setFileTime(String fileTime) {
        this.fileTime = fileTime;
    }

    public String getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber;
    }
}
