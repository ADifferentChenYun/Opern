package com.yun.opern.model;

public class NetEaseCloudMusicChartMusicInfo {
    private int id;
    private int chartId;
    private String img;
    private String name;
    private String alias;
    private String artistNames;
    private int duration;
    private int sort;

    private String playUrl;

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChartId() {
        return chartId;
    }

    public void setChartId(int chartId) {
        this.chartId = chartId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getArtistNames() {
        return artistNames;
    }

    public void setArtistNames(String artistNames) {
        this.artistNames = artistNames;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "NetEaseCloudMusicChartMusicInfo{" +
                "id=" + id +
                ", chartId=" + chartId +
                ", img='" + img + '\'' +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", artistNames='" + artistNames + '\'' +
                ", duration=" + duration +
                ", sort=" + sort +
                ", playUrl='" + playUrl + '\'' +
                '}';
    }
}
