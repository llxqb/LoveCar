package com.bhxx.lovecar.beans;

import java.io.Serializable;

/**
 * Created by bhxx on 2016/11/25.
 * 车辆评估信息详情数值表  ...
 */
public class CarAssessInfodetailcontentModel implements Serializable {
    private String content_id;//ID
    private String detail_id;//	评估详情ID
    private String order_id;//订单id
    private String user_id;//会员id
    private String assess_value;//	评估项值
    private String describe;//	描述

    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }

    public String getDetail_id() {
        return detail_id;
    }

    public void setDetail_id(String detail_id) {
        this.detail_id = detail_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAssess_value() {
        return assess_value;
    }

    public void setAssess_value(String assess_value) {
        this.assess_value = assess_value;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
