package com.yun.opern.model;

import java.io.Serializable;
import java.util.ArrayList;

public class CategoryInfo implements Serializable {
    private String category;
    private ArrayList<CategoryInfo> categoryInfos;

    public CategoryInfo() {
    }

    public CategoryInfo(String category, ArrayList<CategoryInfo> categoryInfos) {
        this.category = category;
        this.categoryInfos = categoryInfos;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ArrayList<CategoryInfo> getCategoryInfos() {
        return categoryInfos;
    }

    public void setCategoryInfos(ArrayList<CategoryInfo> categoryInfos) {
        this.categoryInfos = categoryInfos;
    }

}
