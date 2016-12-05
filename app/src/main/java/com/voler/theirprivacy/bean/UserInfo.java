package com.voler.theirprivacy.bean;

import cn.bmob.v3.BmobObject;

/**
 * 三尺春光驱我寒，一生戎马为长安
 * Created by Han on 16/12/2.
 */

public class UserInfo extends BmobObject {
    private String serialnumber;
    private String phone;
    private String IMSI;
    private String IMEI;

    public String getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIMSI() {
        return IMSI;
    }

    public void setIMSI(String IMSI) {
        this.IMSI = IMSI;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }
}
