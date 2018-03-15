package com.bhxx.lovecar.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bhxx on 2016/11/25.
 * 汽车实体
 */
public class CarModel implements Serializable {
    private boolean ismore =false;//判断是跳转车辆详情还是进入联系
    private String carImg;

    private Integer carId;		// 车源信息id
    private Integer userId;		// 会员ID
    private Integer carUniqueId;		// 车辆品牌车型id
    private Integer carTypeId;		// 车辆类型
    private String carLicenseTime;		// 上牌时间
    private String carLicenseAddress;		// 上牌所在地
    private String carAddress;		// 车辆所在地
    private Double kmNumber;		// 公里数
    private Integer saleTimes;		// 过户次数
    private String transmissionCase;		// 变速箱类型  默认:0  手动/自动
    private Double expectationPrice;		// 用户期望价
    private Double assessPice;		// 平台评估价
    private String contacts;		// 联系人
    private String mobile;		// 联系手机
    private Integer administratorId;		// 评估人
    private String assessTime;		// 评估时间
    private String isAssess; // 是否已评估 0已评估/1为评估
    private String Remarks;//车辆描述

    private String isDefault; //是否推荐 0推荐 1不推荐
    private String isRecommend; //备用字段
    private String extend1;		// 扩展字段1  描述
    private String extend2;		// extend2
    private String extend3;		// extend3
    private String carName;   //汽车名称
    private String createTime;  //创建时间
    private String upTime;     //修改时间
    private String isPublish;  //是否发布
    private double expectationPriceMin; //较低价格（查询使用）
    private double expectationPriceMax; //较高价格（查询使用）
    private AcpgPicture acpgPictures; //图片对象

    public boolean getismore() {
        return ismore;
    }

    public void setIsmore(boolean ismore) {
        this.ismore = ismore;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCarUniqueId() {
        return carUniqueId;
    }

    public void setCarUniqueId(Integer carUniqueId) {
        this.carUniqueId = carUniqueId;
    }

    public Integer getCarTypeId() {
        return carTypeId;
    }

    public void setCarTypeId(Integer carTypeId) {
        this.carTypeId = carTypeId;
    }

    public String getCarLicenseTime() {
        return carLicenseTime;
    }

    public void setCarLicenseTime(String carLicenseTime) {
        this.carLicenseTime = carLicenseTime;
    }

    public String getCarLicenseAddress() {
        return carLicenseAddress;
    }

    public void setCarLicenseAddress(String carLicenseAddress) {
        this.carLicenseAddress = carLicenseAddress;
    }

    public String getCarAddress() {
        return carAddress;
    }

    public void setCarAddress(String carAddress) {
        this.carAddress = carAddress;
    }

    public Double getKmNumber() {
        return kmNumber;
    }

    public void setKmNumber(Double kmNumber) {
        this.kmNumber = kmNumber;
    }

    public Integer getSaleTimes() {
        return saleTimes;
    }

    public void setSaleTimes(Integer saleTimes) {
        this.saleTimes = saleTimes;
    }

    public String getTransmissionCase() {
        return transmissionCase;
    }

    public void setTransmissionCase(String transmissionCase) {
        this.transmissionCase = transmissionCase;
    }

    public Double getExpectationPrice() {
        return expectationPrice;
    }

    public void setExpectationPrice(Double expectationPrice) {
        this.expectationPrice = expectationPrice;
    }

    public Double getAssessPice() {
        return assessPice;
    }

    public void setAssessPice(Double assessPice) {
        this.assessPice = assessPice;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getAdministratorId() {
        return administratorId;
    }

    public void setAdministratorId(Integer administratorId) {
        this.administratorId = administratorId;
    }

    public String getAssessTime() {
        return assessTime;
    }

    public void setAssessTime(String assessTime) {
        this.assessTime = assessTime;
    }

    public String getIsAssess() {
        return isAssess;
    }

    public void setIsAssess(String isAssess) {
        this.isAssess = isAssess;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getIsRecommend() {
        return isRecommend;
    }

    public void setIsRecommend(String isRecommend) {
        this.isRecommend = isRecommend;
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

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpTime() {
        return upTime;
    }

    public void setUpTime(String upTime) {
        this.upTime = upTime;
    }

    public String getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(String isPublish) {
        this.isPublish = isPublish;
    }

    public double getExpectationPriceMin() {
        return expectationPriceMin;
    }

    public void setExpectationPriceMin(double expectationPriceMin) {
        this.expectationPriceMin = expectationPriceMin;
    }

    public double getExpectationPriceMax() {
        return expectationPriceMax;
    }

    public void setExpectationPriceMax(double expectationPriceMax) {
        this.expectationPriceMax = expectationPriceMax;
    }


    public AcpgPicture getAcpgPictures() {
        return acpgPictures;
    }

    public void setAcpgPictures(AcpgPicture acpgPictures) {
        this.acpgPictures = acpgPictures;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getCarImg() {
        return carImg;
    }

    public void setCarImg(String carImg) {
        this.carImg = carImg;
    }

}
