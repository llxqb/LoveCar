package com.bhxx.lovecar.beans;

import java.io.Serializable;

/**
 * Created by bhxx on 2016/11/25.
 * 估价师model
 */

public class GJSModel implements Serializable {

    private Integer assessId;  // 评估师id
    private String assessName;  // 姓名
    private String mobile;  // 联系方式
    private String grade;  // 等级
    private String background;  // 学历
    private Integer workingAge;  // 工作年限
    private String profile;  // 个人简介
    private String gainTime;  // 取证时间
    private Integer assessTimes;  // 评估次数
    private String serviceRegion;  // 服务地区
    private String assessStatus;  // 审核状态  0审核通过/1未通过
    private String status;  // 状态 控制已经审核通过的评估师的信息使用状态
    private String isonline;  // 是否在线  预留
    private String isDefault;       //是否推荐
    private String extend1;  // 扩展字段1
    private String extend2;  // extend2
    private String extend3;  // extend3
    private String beginGainTime;  // 开始 取证时间
    private String endGainTime;  // 结束 取证时间

    //-------------评估师的会员信息扩充字段---------------
    private String userId;  //会员id
    private String avatar;  // 头像
    private String sex;  // 会员性别
    private String birthday;  // 出生日期
    private String identity;  // 身份
    private String age;  // 年龄
    private String carTypeName;  // 车型名称
    private Double servicePrice;  // 服务价格


    public Integer getAssessId() {
        return assessId;
    }

    public void setAssessId(Integer assessId) {
        this.assessId = assessId;
    }

    public String getAssessName() {
        return assessName;
    }

    public void setAssessName(String assessName) {
        this.assessName = assessName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public Integer getWorkingAge() {
        return workingAge;
    }

    public void setWorkingAge(Integer workingAge) {
        this.workingAge = workingAge;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getGainTime() {
        return gainTime;
    }

    public void setGainTime(String gainTime) {
        this.gainTime = gainTime;
    }

    public Integer getAssessTimes() {
        return assessTimes;
    }

    public void setAssessTimes(Integer assessTimes) {
        this.assessTimes = assessTimes;
    }

    public String getServiceRegion() {
        return serviceRegion;
    }

    public void setServiceRegion(String serviceRegion) {
        this.serviceRegion = serviceRegion;
    }

    public String getAssessStatus() {
        return assessStatus;
    }

    public void setAssessStatus(String assessStatus) {
        this.assessStatus = assessStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsonline() {
        return isonline;
    }

    public void setIsonline(String isonline) {
        this.isonline = isonline;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
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

    public String getBeginGainTime() {
        return beginGainTime;
    }

    public void setBeginGainTime(String beginGainTime) {
        this.beginGainTime = beginGainTime;
    }

    public String getEndGainTime() {
        return endGainTime;
    }

    public void setEndGainTime(String endGainTime) {
        this.endGainTime = endGainTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
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
}
