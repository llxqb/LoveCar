package com.bhxx.lovecar.beans;

import java.io.Serializable;

/**
 * Created by bhxx on 2016/11/25.
 * 车辆评估信息详情表  ...
 */
public class CarAssessInfodetailModel implements Serializable {
    private String detail_id;//	ID
    private String parent_id;//	父节点
    private String assess_name;//	检查项名称
    private String assess_value;//	评估项值  正常/异常
    private String sortid;//	排序ID

    public String getDetail_id() {
        return detail_id;
    }

    public void setDetail_id(String detail_id) {
        this.detail_id = detail_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getAssess_name() {
        return assess_name;
    }

    public void setAssess_name(String assess_name) {
        this.assess_name = assess_name;
    }

    public String getAssess_value() {
        return assess_value;
    }

    public void setAssess_value(String assess_value) {
        this.assess_value = assess_value;
    }

    public String getSortid() {
        return sortid;
    }

    public void setSortid(String sortid) {
        this.sortid = sortid;
    }
}
