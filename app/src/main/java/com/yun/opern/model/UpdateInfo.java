package com.yun.opern.model;

public class UpdateInfo {
    private int versionCode;
    private String versionName;
    private String fileName;
    private String downloadUrl;
    private String downloadOSSUrl;
    private String updateMessage;
    private String updateType;  //0 推荐更新 1 强制更新
    private String updateDataTime;

    public String getDownloadOSSUrl() {
        return downloadOSSUrl;
    }

    public void setDownloadOSSUrl(String downloadOSSUrl) {
        this.downloadOSSUrl = downloadOSSUrl;
    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUpdateMessage() {
        return updateMessage;
    }

    public void setUpdateMessage(String updateMessage) {
        this.updateMessage = updateMessage;
    }

    public String getUpdateDataTime() {
        return updateDataTime;
    }

    public void setUpdateDataTime(String updateDataTime) {
        this.updateDataTime = updateDataTime;
    }
}
