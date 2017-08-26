package com.yun.opern.model;

import java.io.Serializable;

public class OpernImgInfo implements Serializable{
    public static final String imgUrlHost = "http://www.qupu123.com";
    private String id;
    private int opernId;
    private int opernIndex;
    private String opernTitle;
    private String opernImg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOpernId() {
        return opernId;
    }

    public void setOpernId(int opernId) {
        this.opernId = opernId;
    }

    public int getOpernIndex() {
        return opernIndex;
    }

    public void setOpernIndex(int opernIndex) {
        this.opernIndex = opernIndex;
    }

    public String getOpernTitle() {
        return opernTitle;
    }

    public void setOpernTitle(String opernTitle) {
        this.opernTitle = opernTitle;
    }

    public String getOpernImg() {
        return opernImg;
    }

    public void setOpernImg(String opernImg) {
        this.opernImg = opernImg;
    }
}
