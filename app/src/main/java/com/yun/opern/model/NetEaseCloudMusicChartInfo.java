package com.yun.opern.model;

import java.io.Serializable;

public class NetEaseCloudMusicChartInfo implements Serializable {
    private int id;
    private String img = "";
    private String href = "";
    private String name = "";
    private String updateTimeInfo = "";
    private String flag = "";
    private int sort;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpdateTimeInfo() {
        return updateTimeInfo;
    }

    public void setUpdateTimeInfo(String updateTimeInfo) {
        this.updateTimeInfo = updateTimeInfo;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
