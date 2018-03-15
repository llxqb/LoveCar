package com.bhxx.lovecar.entity;

/**
 * Created by bhxx on 2016/12/1.
 * 车辆信息
 */
public class CarEntity {
    public  String  key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public CarEntity(String key) {
        super();
        this.key = key;
    }
}
