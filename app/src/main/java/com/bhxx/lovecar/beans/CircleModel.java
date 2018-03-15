package com.bhxx.lovecar.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bhxx on 2016/11/25.
 * 圈子model
 */

public class CircleModel implements Serializable {
    private String Circle_img;

    private Integer circleId;        // 兴趣圈id
    private Integer userId;        // 会员id
    private String name;        // 圈子名称
    private String administratorId;        // 创建人
    private String creteTime;        // 创建时间
    private String postCount;        // 帖数
    private Integer circleCount;        // 圈友数
    private String brief;        // 简介
    private Integer sortid;        // 排序id
    private String isRecommend;        // 是否推荐 0：推荐 1：不推荐
    private String isAdd; //是否已经是我加入的圈子 0：未添加   1：已添加
    private AcpgPicture acpgPictures;
    private String extend1;//扩展字段   圈子图片

    public Integer getCircleId() {
        return circleId;
    }

    public void setCircleId(Integer circleId) {
        this.circleId = circleId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdministratorId() {
        return administratorId;
    }

    public void setAdministratorId(String administratorId) {
        this.administratorId = administratorId;
    }

    public String getCreteTime() {
        return creteTime;
    }

    public void setCreteTime(String creteTime) {
        this.creteTime = creteTime;
    }

    public String getPostCount() {
        return postCount;
    }

    public void setPostCount(String postCount) {
        this.postCount = postCount;
    }

    public Integer getCircleCount() {
        return circleCount;
    }

    public void setCircleCount(Integer circleCount) {
        this.circleCount = circleCount;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public Integer getSortid() {
        return sortid;
    }

    public void setSortid(Integer sortid) {
        this.sortid = sortid;
    }

    public String getIsRecommend() {
        return isRecommend;
    }

    public void setIsRecommend(String isRecommend) {
        this.isRecommend = isRecommend;
    }

    public AcpgPicture getAcpgPictures() {
        return acpgPictures;
    }

    public void setAcpgPictures(AcpgPicture acpgPictures) {
        this.acpgPictures = acpgPictures;
    }

    public String getCircle_img() {
        return Circle_img;
    }

    public void setCircle_img(String circle_img) {
        Circle_img = circle_img;
    }

    public String getIsAdd() {
        return isAdd;
    }

    public void setIsAdd(String isAdd) {
        this.isAdd = isAdd;
    }

    public String getExtend1() {
        return extend1;
    }

    public void setExtend1(String extend1) {
        this.extend1 = extend1;
    }
}
