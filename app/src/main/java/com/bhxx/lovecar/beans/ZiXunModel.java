package com.bhxx.lovecar.beans;

import java.io.Serializable;

/**
 * Created by bhxx on 2016/11/25.
 * 资讯实体类
 */
public class ZiXunModel implements Serializable {

    private String zxImg;

    private Integer messageId;		// 资讯信息id
    private String title;		// 资讯标题
    private String fullText;		// 资讯内容
    private String picture;		// 资讯图片
    private String createTime;		// 发布时间
    private String messageStatus;		// 发布状态
    private Integer administratorId;		// 管理员id


    private Integer typeId;		// 资讯类别id
    private String typeName;		// 类别名称
    private Integer sortid;		// 排序id
    private String isDefault;		// 默认推荐


    private String collectStatus;//是否收藏  0收藏 1未收藏
    private String hitStatus;//是否点赞
    private String careStatus;//是否关注

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }

    public Integer getAdministratorId() {
        return administratorId;
    }

    public void setAdministratorId(Integer administratorId) {
        this.administratorId = administratorId;
    }

    public String getZxImg() {
        return zxImg;
    }

    public void setZxImg(String zxImg) {
        this.zxImg = zxImg;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getSortid() {
        return sortid;
    }

    public void setSortid(Integer sortid) {
        this.sortid = sortid;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
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
}
