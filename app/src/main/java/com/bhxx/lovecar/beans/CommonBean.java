package com.bhxx.lovecar.beans;

import java.io.Serializable;

public class CommonBean<T> implements Serializable {
    private T rows;
    private String resultDesc; //字符串返回
    private String resultCode;  //数字形式返回
    private int total;    //总共数据数量
    private int flg;    //


    public T getRows() {
        return rows;
    }

    public void setRows(T rows) {
        this.rows = rows;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getFlg() {
        return flg;
    }

    public void setFlg(int flg) {
        this.flg = flg;
    }
}
