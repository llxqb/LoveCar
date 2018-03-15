package com.bhxx.lovecar.beans;

import java.io.Serializable;

/**
 * Created by bhxx on 2016/12/28.
 * 轮播图片实体
 */
public class bannerModel implements Serializable {

    private int id;//id
    private String title;  // 标题
    private String abstr;  // 摘要
    private String fulltext;  // 文本
    private String picture;  // 图片
    private String url;  // 路径
    private String starttime;  // 开始时间
    private String endtime;  // 结束时间
    private Integer sortid;  // 排序id
    private String createtime;  // 创建时间
    private Integer objectid;  // 业务id
    private String createtype;  // 业务类型
    private Integer hittimes;  // 次数
    private Integer isset;  // isset
    private String local;  // local

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstr() {
        return abstr;
    }

    public void setAbstr(String abstr) {
        this.abstr = abstr;
    }

    public String getFulltext() {
        return fulltext;
    }

    public void setFulltext(String fulltext) {
        this.fulltext = fulltext;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public Integer getSortid() {
        return sortid;
    }

    public void setSortid(Integer sortid) {
        this.sortid = sortid;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public Integer getObjectid() {
        return objectid;
    }

    public void setObjectid(Integer objectid) {
        this.objectid = objectid;
    }

    public String getCreatetype() {
        return createtype;
    }

    public void setCreatetype(String createtype) {
        this.createtype = createtype;
    }

    public Integer getHittimes() {
        return hittimes;
    }

    public void setHittimes(Integer hittimes) {
        this.hittimes = hittimes;
    }

    public Integer getIsset() {
        return isset;
    }

    public void setIsset(Integer isset) {
        this.isset = isset;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }
}
