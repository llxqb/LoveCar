package com.bhxx.lovecar.beans;

import java.io.Serializable;

/**
 * Created by bhxx on 2016/12/8.
 * 动态的评论 实体类
 * 评估师 评价
 */
public class DynamicCommentModel implements Serializable {
    private int commentId;		// id
    private int parentId;		// 父节点id
    private int dynamicId;		// 动态id
    private int userId;		// 会员id
    private String cmContent;		// 评论内容
    private String floorCount;		// 楼层
    private String careCount;		// 关注数
    private String createTime;		// 评论时间
    private String createType;		// 业务类型
    private String status;		// 评论状态

    private String avatar;//会员头像
    private String fullName;//会员名字
    private float starNum;//评论等级
//    UserModel userModel = new UserModel();

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(int dynamicId) {
        this.dynamicId = dynamicId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCmContent() {
        return cmContent;
    }

    public void setCmContent(String cmContent) {
        this.cmContent = cmContent;
    }

    public String getFloorCount() {
        return floorCount;
    }

    public void setFloorCount(String floorCount) {
        this.floorCount = floorCount;
    }

    public String getCareCount() {
        return careCount;
    }

    public void setCareCount(String careCount) {
        this.careCount = careCount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateType() {
        return createType;
    }

    public void setCreateType(String createType) {
        this.createType = createType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public float getStarNum() {
        return starNum;
    }

    public void setStarNum(float starNum) {
        this.starNum = starNum;
    }
}
