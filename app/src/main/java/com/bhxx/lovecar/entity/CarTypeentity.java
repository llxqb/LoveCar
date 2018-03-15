package com.bhxx.lovecar.entity;

/**
 * Created by bhxx on 2016/12/1.
 * 车辆品牌信息  2处地方  选车和添加爱车
 */
public class CarTypeentity {
    public String key1;
    public String key2;


    public String getKey1() {
        return key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    public String getKey2() {
        return key2;
    }

    public void setKey2(String key2) {
        this.key2 = key2;
    }

    public CarTypeentity(String key1, String key2) {
        super();
        this.key1 = key1;
        this.key2 = key2;
    }
}
