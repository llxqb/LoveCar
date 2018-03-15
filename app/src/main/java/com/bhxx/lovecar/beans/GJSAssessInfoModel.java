package com.bhxx.lovecar.beans;

import java.io.Serializable;

/**
 * Created by bhxx on 2016/11/25.
 * 评估师评价信息表
 */
public class GJSAssessInfoModel implements Serializable {
    private String comment_id;//	ID
    private String deal_id;//交易记录ID
    private String user_id;//会员ID
    private String assess_id;//	评估师ID
    private String picture_id;//图片id
    private String assess_star;//服务星级
    private String assess_content;//服务评价内容
    private String service_features;//	服务特点
    private String upload_type;//服务类型
    private String create_time;//创建时间

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getDeal_id() {
        return deal_id;
    }

    public void setDeal_id(String deal_id) {
        this.deal_id = deal_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAssess_id() {
        return assess_id;
    }

    public void setAssess_id(String assess_id) {
        this.assess_id = assess_id;
    }

    public String getPicture_id() {
        return picture_id;
    }

    public void setPicture_id(String picture_id) {
        this.picture_id = picture_id;
    }

    public String getAssess_star() {
        return assess_star;
    }

    public void setAssess_star(String assess_star) {
        this.assess_star = assess_star;
    }

    public String getAssess_content() {
        return assess_content;
    }

    public void setAssess_content(String assess_content) {
        this.assess_content = assess_content;
    }

    public String getService_features() {
        return service_features;
    }

    public void setService_features(String service_features) {
        this.service_features = service_features;
    }

    public String getUpload_type() {
        return upload_type;
    }

    public void setUpload_type(String upload_type) {
        this.upload_type = upload_type;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
