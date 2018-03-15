package com.bhxx.lovecar.beans;

import java.io.Serializable;

/**
 * Created by bhxx on 2016/11/25.
 * 圈子model
 */

public class MomentsModel implements Serializable {
    private String circle_img;//圈子图片

    private int circle_id;//	ID
    private int user_id;//会员ID
    private String name;//圈子名称
    private String administrator_id;//创建人
    private String crete_time;//创建时间
    private int post_count;//帖数
    private int circle_count;//圈友数
    private String brief;//简介
    private int sortid;//排序ID

    public String getCircle_img() {
        return circle_img;
    }

    public void setCircle_img(String circle_img) {
        this.circle_img = circle_img;
    }

    public int getCircle_id() {
        return circle_id;
    }

    public void setCircle_id(int circle_id) {
        this.circle_id = circle_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdministrator_id() {
        return administrator_id;
    }

    public void setAdministrator_id(String administrator_id) {
        this.administrator_id = administrator_id;
    }

    public String getCrete_time() {
        return crete_time;
    }

    public void setCrete_time(String crete_time) {
        this.crete_time = crete_time;
    }


    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public int getPost_count() {
        return post_count;
    }

    public void setPost_count(int post_count) {
        this.post_count = post_count;
    }

    public int getCircle_count() {
        return circle_count;
    }

    public void setCircle_count(int circle_count) {
        this.circle_count = circle_count;
    }

    public int getSortid() {
        return sortid;
    }

    public void setSortid(int sortid) {
        this.sortid = sortid;
    }
}
