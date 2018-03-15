package com.bhxx.lovecar.values;

import android.os.Environment;

/**
 * TOAST常量
 * Created by liuli on 2016/10/13.
 */
public class Constant {
    public static final String ERROR_WEB = "连接超时";
    public static final String NO_DATA = "暂无数据";//listview 无数据
    public static final String FINISH = "加载完成";//成功显示
    public static final String NO_DATA_REFRESH = "无刷新数据";//刷新无数据加载


    public static final String SDPATH = Environment.getExternalStorageDirectory() + "/lovecar/";
    public static final String CACHE_DIR = SDPATH + "cache";

    //common params
    public static final String CAR_RESPONSE_OK = "0000";//服务器正常响应
    public static final String CAR_MOBILE = "mobile";//手机号
    public static final String CAR_PWD = "pwd";//密码
    public static final String CAR_UPWD = "upwd";//原密码
    public static final String CAR_SAFE_CODE = "safeCode";//验证码
    public static final String CAR_TYPE = "type";//0：密码找回 1:注册
    public static final String CAR_LOGIN_PLATFORM = "loginPlatform";
    public static final String CAR_REGISTRATION_ID = "registrationId";
    public static final String CAR_RCLOUD_TOKEN = "rongcloudTonken";
    public static final String CAR_KEY = "sign";
    public static final String CAR_ROWS = "rows";
    public static final String CAR_USER_ID = "userId";//用户唯一id
    public static final String CAR_USER_MOBILE = "mobile";//用户手机号
    public static final String CAR_USER_PWD = "pwd";//用户密码
    public static final String CAR_USER_AVATAR = "avatar";//用户头像
    public static final String CAR_USER_FULLNAME = "fullName";//用户姓名
    public static final String CAR_USER_NICKNAME = "nickName";//用户昵称
    public static final String CAR_USER_SEX = "sex";//用户性别
    public static final String CAR_USER_BIRTHDAY = "birthday";//用户出生年月
    public static final String CAR_USER_CITY = "city";//用户所在城市
    public static final String CAR_USER_LONGITUDEX = "longitudeX";//经度
    public static final String CAR_USER_LATITUDEY = "latitudeY";//纬度
    public static final String CAR_USER_IDENTITY = "identity";//是否认证0会员1评估师
    public static final String CAR_USER_ASSESSSTATUS = "assessStatus";//评估师审核状态0审核通过1审核中2驳回
    public static final String CAR_USER_ASSESSID = "assessId";
    public static final String CAR_USER_SYSTEMREPLY = "systemReply";//评估师驳回原因
    public static final String CAR_USER_CONTENT = "content";//用户提交内容
    public static final String CAR_DESCRIBE = "describe";
    public static final String CAR_NAME = "carName";
    public static final String CAR_ISFRIEND = "isFriend";//是否是好友0好友1不是好友
    public static final String CAR_USER_CREATETYPE = "createType";//1：资讯;0：车源
    public static final String CAR_USER_CREATETIME = "createTime";
    public static final String CAR_ASSESS_PRICE = "assessPice";
    public static final String CAR_LICENSE_TIME = "carLicenseTime";
    public static final String CAR_LICENSE_ADDRESS = "carLicenseAddress";
    public static final String CAR_ADDRESS = "carAddress";
    public static final String CAR_KM_NUMBER = "kmNumber";
    public static final String CAR_ACPG_PICTURES = "acpgPictures";
    public static final String CAR_ACPG_PICTURES_URL = "url";
    public static final String CAR_ZX_TITLE = "title";
    public static final String CAR_ZX_TYPENAME = "typeName";
    public static final String CAR_CARID = "carId";
    public static final String CAR_OBJECTID = "objectId";
    public static final String CAR_SALETIMES = "saleTimes";//过户次数
    public static final String CAR_TRANSMISSIONCASE = "transmissionCase";//变速箱
    public static final String CAR_EXPECTATIONPRICE = "expectationPrice";//期望价
    public static final String CAR_STATUS = "status";//0已添加 1已添加等待同意 2不同意
    public static final String CAR_TOUSER = "toUser";//还有Id
    public static final String CAR_ISAGREE = "isAgree";//0同意
    public static final String CAR_USERIDS = "userIds";
    public static final String CAR_IDENTITY = "identity";
    public static final String CAR_AGESTART = "ageStart";
    public static final String CAR_AGEEND = "ageEnd";
    public static final String CAR_ISDEFAULT = "isDefault";
    public static final String CAR_ASSESSNAME = "assessName";
    public static final String CAR_BACKGROUND = "background";
    public static final String CAR_GRADE = "grade";
    public static final String CAR_WORKINGAGE = "workingAge";
    public static final String CAR_EXTEND1 = "extend1";
    public static final String CAR_TYPEID = "carTypeId";
    public static final String CAR_TYPENAME = "typeName";
    public static final String CAR_CARTYPENAME = "carTypeName";
    public static final String CAR_SERVICEPRICE = "servicePrice";
    public static final String CAR_DESCRIPTION = "description";
    public static final String CAR_SERVICEREGION = "serviceRegion";
}
