package com.yun.opern.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.Date;


/**
 * Created by Yun on 2017/9/24 0024.
 */
@Entity
public class SearchHistory {
    @Id
    private String searchParameter;
    @NotNull
    private Date date;

    @Generated(hash = 1062841885)
    public SearchHistory(String searchParameter, @NotNull Date date) {
        this.searchParameter = searchParameter;
        this.date = date;
    }

    @Generated(hash = 1905904755)
    public SearchHistory() {
    }

    public String getSearchParameter() {
        return this.searchParameter;
    }

    public void setSearchParameter(String searchParameter) {
        this.searchParameter = searchParameter;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
