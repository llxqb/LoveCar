package com.bhxx.lovecar.beans;

import java.io.Serializable;

/**
 * Created by bhxx on 2016/11/25.
 * 车辆评估信息表
 */
public class CarAssessInfoModel implements Serializable {
    private String car_assess_id; //ID
    // 会员id
    private String user_id;
    // 车源ID
    private String car_id;
    //评估车辆名称
    private String assess_name;
    //评估时间
    private String assess_time;
    //评估报告发布时间
    private String assess_up_time;
    //评估次数
    private String assess_number;
    //评估价格
    private String assess_price;
    //评估师id
    private String assess_id;
    //描述
    private String describe;


    public String getCar_assess_id() {
        return car_assess_id;
    }

    public void setCar_assess_id(String car_assess_id) {
        this.car_assess_id = car_assess_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public String getAssess_name() {
        return assess_name;
    }

    public void setAssess_name(String assess_name) {
        this.assess_name = assess_name;
    }

    public String getAssess_time() {
        return assess_time;
    }

    public void setAssess_time(String assess_time) {
        this.assess_time = assess_time;
    }

    public String getAssess_up_time() {
        return assess_up_time;
    }

    public void setAssess_up_time(String assess_up_time) {
        this.assess_up_time = assess_up_time;
    }

    public String getAssess_number() {
        return assess_number;
    }

    public void setAssess_number(String assess_number) {
        this.assess_number = assess_number;
    }

    public String getAssess_price() {
        return assess_price;
    }

    public void setAssess_price(String assess_price) {
        this.assess_price = assess_price;
    }

    public String getAssess_id() {
        return assess_id;
    }

    public void setAssess_id(String assess_id) {
        this.assess_id = assess_id;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
