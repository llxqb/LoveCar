package com.bhxx.lovecar.beans;

import java.io.Serializable;

/**
 * Created by bhxx on 2016/11/25.
 * 估价师发布服务信息表
 */
public class GJSPublishServiceModel implements Serializable {
    private Integer serviceId;  // 服务id
    private Integer userId;  // 会员id
    private Integer assessId;  // 评估师id
    private Integer carTypeId;  // 车型id
    private String carTypeName;  // 车型名称
    private Double servicePrice;  // 服务价格
    private String description;  // 服务描述
    private String createTime;  // 发布时间
    private String serviceStatus;  // 服务状态
    private String sortid;  // 排序id
    private String extend1;  // 扩展字段1
    private String extend2;  // extend2
    private String extend3;  // extend3

    private String mobile;  // 联系方式
    private String assessName;  // 姓名
    private String avatar;  // 头像
    private String grade;  // 等级
    private String serviceRegion;  // 服务地区
    private Integer assessTimes;  // 评估次数

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAssessId() {
        return assessId;
    }

    public void setAssessId(Integer assessId) {
        this.assessId = assessId;
    }

    public Integer getCarTypeId() {
        return carTypeId;
    }

    public void setCarTypeId(Integer carTypeId) {
        this.carTypeId = carTypeId;
    }

    public String getCarTypeName() {
        return carTypeName;
    }

    public void setCarTypeName(String carTypeName) {
        this.carTypeName = carTypeName;
    }

    public Double getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(Double servicePrice) {
        this.servicePrice = servicePrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getSortid() {
        return sortid;
    }

    public void setSortid(String sortid) {
        this.sortid = sortid;
    }

    public String getExtend1() {
        return extend1;
    }

    public void setExtend1(String extend1) {
        this.extend1 = extend1;
    }

    public String getExtend2() {
        return extend2;
    }

    public void setExtend2(String extend2) {
        this.extend2 = extend2;
    }

    public String getExtend3() {
        return extend3;
    }

    public void setExtend3(String extend3) {
        this.extend3 = extend3;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAssessName() {
        return assessName;
    }

    public void setAssessName(String assessName) {
        this.assessName = assessName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getServiceRegion() {
        return serviceRegion;
    }

    public void setServiceRegion(String serviceRegion) {
        this.serviceRegion = serviceRegion;
    }

    public Integer getAssessTimes() {
        return assessTimes;
    }

    public void setAssessTimes(Integer assessTimes) {
        this.assessTimes = assessTimes;
    }
}
