package com.bhxx.lovecar.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bhxx on 2016/11/25.
 * 动态实体
 */
public class DynamicModel implements Serializable {

    private String dynamicImg;//动态图片
    private String avatar;//动态发布者头像
    private String fullName;//动态发布者昵称

    private Integer dynamicId;		// 动态信息id
    private Integer userId;		// 会员id
    private Integer circleId;		// 圈子id
    private String createTime;		// 创建时间
    private String content;		// 动态内容
    private String city;		// 所在城市
    private String dynamicStatus;		// 状态
    private Integer regionProvinceId;		// 所属省id
    private Integer regionCityId;		// 所属市id
    private Integer regionCountryId;		// 所属县id
    private String isHot;		// 是否置顶 0 置顶 1 不置顶
    private Integer sortid;		// 推荐id
    private Integer favoriteCount;		// 收藏数
    private Integer commentCount;		// 评论数
    private Integer careCount;		// 关注数
    private String extend1;		// 扩展字段1
    private String extend2;		// extend2
    private String extend3;		// extend3
    private AcpgPicture acpgPictures;

    private String collectStatus;//是否收藏  0收藏 1未收藏
    private String hitStatus;//是否点赞
    private String careStatus;//是否关注
    private String isFriend;//是否是好友

    private UserModel userModel;

    public String getDynamicImg() {
        return dynamicImg;
    }

    public void setDynamicImg(String dynamicImg) {
        this.dynamicImg = dynamicImg;
    }

    public Integer getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(Integer dynamicId) {
        this.dynamicId = dynamicId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCircleId() {
        return circleId;
    }

    public void setCircleId(Integer circleId) {
        this.circleId = circleId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDynamicStatus() {
        return dynamicStatus;
    }

    public void setDynamicStatus(String dynamicStatus) {
        this.dynamicStatus = dynamicStatus;
    }

    public Integer getRegionProvinceId() {
        return regionProvinceId;
    }

    public void setRegionProvinceId(Integer regionProvinceId) {
        this.regionProvinceId = regionProvinceId;
    }

    public Integer getRegionCityId() {
        return regionCityId;
    }

    public void setRegionCityId(Integer regionCityId) {
        this.regionCityId = regionCityId;
    }

    public Integer getRegionCountryId() {
        return regionCountryId;
    }

    public void setRegionCountryId(Integer regionCountryId) {
        this.regionCountryId = regionCountryId;
    }

    public String getIsHot() {
        return isHot;
    }

    public void setIsHot(String isHot) {
        this.isHot = isHot;
    }

    public Integer getSortid() {
        return sortid;
    }

    public void setSortid(Integer sortid) {
        this.sortid = sortid;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getCareCount() {
        return careCount;
    }

    public void setCareCount(Integer careCount) {
        this.careCount = careCount;
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


    public AcpgPicture getAcpgPictures() {
        return acpgPictures;
    }

    public void setAcpgPictures(AcpgPicture acpgPictures) {
        this.acpgPictures = acpgPictures;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCollectStatus() {
        return collectStatus;
    }

    public void setCollectStatus(String collectStatus) {
        this.collectStatus = collectStatus;
    }

    public String getHitStatus() {
        return hitStatus;
    }

    public void setHitStatus(String hitStatus) {
        this.hitStatus = hitStatus;
    }

    public String getCareStatus() {
        return careStatus;
    }

    public void setCareStatus(String careStatus) {
        this.careStatus = careStatus;
    }

    public String getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(String isFriend) {
        this.isFriend = isFriend;
    }
}
