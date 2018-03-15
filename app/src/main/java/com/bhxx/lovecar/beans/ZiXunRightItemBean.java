package com.bhxx.lovecar.beans;

import java.io.Serializable;

/**
 * 资讯右侧item菜单
 */
public class ZiXunRightItemBean implements Serializable {

    private int contentId;//内容id
    private String content;
    private String type;//我的栏目与添加栏目

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
