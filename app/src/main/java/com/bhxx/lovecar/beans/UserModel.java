package com.bhxx.lovecar.beans;

import java.io.Serializable;

/**
 * Created by bhxx on 2016/11/25.
 * 用户实体
 */
public class UserModel implements Serializable {

    private String userId;    //会员id
    private String mobile;		// 手机号
    private String pwd;		// 密码
    private String token;		// 令牌
    private String nickName;		// 昵称
    private String fullName;		// 会员姓名
    private String sex;		// 会员性别
    private String birthday;		// 出生日期
    private String avatar;		// 头像
    private String email;		// 会员邮箱
    private String address;		// 会员住址
    private String description;		// 个人描述
    private String city;		// 所在城市
    private String regionProvinceId;		// 所属省id
    private String regionCityId;		// 所属市id
    private String regionCountryId;		// 所属县id
    private Double account;		// 账户余额
    private String identity;		// 用户身份
    private Integer assessId;		// 评估师编号
    private String assessStatus;		// 评估师审核状态 0 已审核 1未审核
    private String systemReply;		// 审核回复
    private String administratorId;		// 审核人
    private String adminCheckTime;		// 审核时间
    private String creatTime;		// 会员信息创建时间
    private String revampTime;		// 信息修改时间
    private String loginPlatform;		// 登录平台
    private String lastLoginTime;		// 最后登录时间
    private String safeCode;		// 短信验证码
    private String sendScTime;		// 发送时间
    private String upCheckTime;		// 上传审核时间
    private String userStatus;		// 会员信息状态 0正常/1停用
    private String isOnline;		// 是否离线  预留
    private Double longitudeX;		// 距离经度
    private Double latitudeY;		// 距离纬度
    private String bindPlatform;		// 绑定平台
    private String openId;		// 登录标识
    private String registrationId;		// 极光推送标识
    private String rongcloudTonken;		// 融云聊天

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegionProvinceId() {
        return regionProvinceId;
    }

    public void setRegionProvinceId(String regionProvinceId) {
        this.regionProvinceId = regionProvinceId;
    }

    public String getRegionCityId() {
        return regionCityId;
    }

    public void setRegionCityId(String regionCityId) {
        this.regionCityId = regionCityId;
    }

    public String getRegionCountryId() {
        return regionCountryId;
    }

    public void setRegionCountryId(String regionCountryId) {
        this.regionCountryId = regionCountryId;
    }

    public Double getAccount() {
        return account;
    }

    public void setAccount(Double account) {
        this.account = account;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public Integer getAssessId() {
        return assessId;
    }

    public void setAssessId(Integer assessId) {
        this.assessId = assessId;
    }

    public String getAssessStatus() {
        return assessStatus;
    }

    public void setAssessStatus(String assessStatus) {
        this.assessStatus = assessStatus;
    }

    public String getSystemReply() {
        return systemReply;
    }

    public void setSystemReply(String systemReply) {
        this.systemReply = systemReply;
    }

    public String getAdministratorId() {
        return administratorId;
    }

    public void setAdministratorId(String administratorId) {
        this.administratorId = administratorId;
    }

    public String getAdminCheckTime() {
        return adminCheckTime;
    }

    public void setAdminCheckTime(String adminCheckTime) {
        this.adminCheckTime = adminCheckTime;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public String getRevampTime() {
        return revampTime;
    }

    public void setRevampTime(String revampTime) {
        this.revampTime = revampTime;
    }

    public String getLoginPlatform() {
        return loginPlatform;
    }

    public void setLoginPlatform(String loginPlatform) {
        this.loginPlatform = loginPlatform;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getSafeCode() {
        return safeCode;
    }

    public void setSafeCode(String safeCode) {
        this.safeCode = safeCode;
    }

    public String getSendScTime() {
        return sendScTime;
    }

    public void setSendScTime(String sendScTime) {
        this.sendScTime = sendScTime;
    }

    public String getUpCheckTime() {
        return upCheckTime;
    }

    public void setUpCheckTime(String upCheckTime) {
        this.upCheckTime = upCheckTime;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public Double getLongitudeX() {
        return longitudeX;
    }

    public void setLongitudeX(Double longitudeX) {
        this.longitudeX = longitudeX;
    }

    public Double getLatitudeY() {
        return latitudeY;
    }

    public void setLatitudeY(Double latitudeY) {
        this.latitudeY = latitudeY;
    }

    public String getBindPlatform() {
        return bindPlatform;
    }

    public void setBindPlatform(String bindPlatform) {
        this.bindPlatform = bindPlatform;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getRongcloudTonken() {
        return rongcloudTonken;
    }

    public void setRongcloudTonken(String rongcloudTonken) {
        this.rongcloudTonken = rongcloudTonken;
    }
}
