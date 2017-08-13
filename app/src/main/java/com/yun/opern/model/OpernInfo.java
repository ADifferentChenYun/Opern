package com.yun.opern.model;

import java.io.Serializable;
import java.util.ArrayList;

public class OpernInfo implements Serializable{
    private int id;
    private String title = "";
    private String wordAuthor = "";
    private String songAuthor = "";
    private String singer = "";
    private String format = "";
    private String uploadUserName = "";
    private String uploadDateTime = "";
    private String origin = "";
    private int views;
    private String html = "";
    private ArrayList<OpernImgInfo> imgs;
    private String dataOrigin = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWordAuthor() {
        return wordAuthor;
    }

    public void setWordAuthor(String wordAuthor) {
        this.wordAuthor = wordAuthor;
    }

    public String getSongAuthor() {
        return songAuthor;
    }

    public void setSongAuthor(String songAuthor) {
        this.songAuthor = songAuthor;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUploadUserName() {
        return uploadUserName;
    }

    public void setUploadUserName(String uploadUserName) {
        this.uploadUserName = uploadUserName;
    }

    public String getUploadDateTime() {
        return uploadDateTime;
    }

    public void setUploadDateTime(String uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public ArrayList<OpernImgInfo> getImgs() {
        return imgs;
    }

    public void setImgs(ArrayList<OpernImgInfo> imgs) {
        this.imgs = imgs;
    }

    public String getDataOrigin() {
        return dataOrigin;
    }

    public void setDataOrigin(String dataOrigin) {
        this.dataOrigin = dataOrigin;
    }

    @Override
    public String toString() {
        return "OpernInfo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", wordAuthor='" + wordAuthor + '\'' +
                ", songAuthor='" + songAuthor + '\'' +
                ", singer='" + singer + '\'' +
                ", format='" + format + '\'' +
                ", uploadUserName='" + uploadUserName + '\'' +
                ", uploadDateTime='" + uploadDateTime + '\'' +
                ", origin='" + origin + '\'' +
                ", views=" + views +
                ", html='" + html + '\'' +
                ", imgs=" + imgs +
                ", dataOrigin='" + dataOrigin + '\'' +
                '}';
    }
}
