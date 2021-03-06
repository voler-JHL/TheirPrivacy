package com.voler.theirprivacy.bean;

import cn.bmob.v3.BmobObject;

/**
 * 三尺春光驱我寒，一生戎马为长安
 * Created by Han on 16/12/6.
 */

public class FileInfo extends BmobObject {
    private String serialnumber;
    private String IMEI;
    private String fileType;
    private String filePath;
    private long fileUpdate;

    public String getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileUpdate() {
        return fileUpdate;
    }

    public void setFileUpdate(long fileUpdate) {
        this.fileUpdate = fileUpdate;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }
}
