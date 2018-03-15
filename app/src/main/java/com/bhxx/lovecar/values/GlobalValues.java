package com.bhxx.lovecar.values;

public class GlobalValues {

    public static final String IP = "http://210.16.188.194:8080/intervface/v1.0/";
//        public static final String IP = "http://192.168.1.104:8080/jeeplus/intervface/v1.0/";
    public static final String IMG_IP = "http://210.16.188.194:8080/picture/intervface/v1.0/";//上传图片地址
    public static final String IP1 = "http://210.16.188.194:8080";//加载图片

    public static final String PAGE_SIZE = "10";
    public static final String resultCode = "resultCode";

    //注册
    public static final String REGISTER = IP + "member/save";
    //登录
    public static final String LOGIN = IP + "member/login";
    //退出登录
    public static final String OUTLOGIN = IP + "member/outLogin";
    //用户已经登录修改密码
    public static final String UPDATE_PASSWORD = IP + "member/updatePwd";
    //用户未登录忘记密码然后重新设置新密码
    public static final String FIND_PASSWORD = IP + "member/forgetPwd";
    //修改个人信息
    public static final String MODIFY_INFO = IP + "member/updateMember";
    //会员查询系统消息信息
    public static final String INFO_LIST = IP + "info/list";
    //用户意见
    public static final String IDEA_ADDINDEA = IP + "idea/addIdea";
    //我的-收藏
    public static final String COLLECT_LIST = IP + "collect/list";
    //我的-关注
    public static final String CARE_LIST = IP + "care/list";
    //我的-取消关注
    public static final String CARE_DELCARE = IP + "care/delCare";
    //车友圈-交友
    public static final String FRIEND_LIST = IP + "friend/list";
    //车友圈-拒绝
    public static final String FRIEND_REFUSEFRIEND = IP + "friend/refuseFriend";
    //车友圈-交友-添加
    public static final String FRIEND_AGREEFRIEND = IP + "friend/agreeFriend";
    //车友圈-交友-所有用户列表
    public static final String FRIEND_USERLIST = IP + "member/userList";
    //车友圈-交友-附近的人列表
    public static final String FRIEND_FRIENDXY = IP + "friendXY";
    //车友圈-交友-附近的人列表
    public static final String FRIEND_FRIENDUSER = IP + "friendUser";
    //main经纬度上传
    public static final String FRIEND_MEMBERXY = IP + "member/memberXY";
    //会员发起找回密码，将该会员对应的安全码存储起来。
    public static final String MEMBER_SENDMESSAGE = IP + "member/sendMessage";
    //我的-爱车
    public static final String CARMESSAGE_QUERYBYPAGE = IP + "carmessage/queryByPage";
    //单张图片上传
    public static final String PICTURE_SINGLEUPLOAD = IMG_IP + "picture/singleUpload";
    //查询评估师信息列表
    public static final String ASSESS_LIST = IP + "assess/list";
    //提交评估师信息
    public static final String ASSESS_SAVEASSESS = IP + "assess/saveAssess";
    //修改评估师申请信息
    public static final String ASSESS_UPDATEASSESS = IP + "assess/updateAssess";
    //评估师个人信息
    public static final String ASSESS_SELECTASSESS = IP + "assess/selectAssess";
    //评估师发布服务
    public static final String ASSESSSERVICE_ADDSERVICE = IP + "assessService/addService";
    //评估师查看当前支付订单记录列表
    public static final String ORDER_PRESENTLIST = IP + "order/presentlist";
    //评估师查看历史订单记录列表
    public static final String ORDER_HISTORYLIST = IP + "order/historylist";
    //评估师个人发布服务列表
    public static final String ASSESSSERVICE_MYLIST = IP + "assessService/mylist";
    //车型类别接口
    public static final String CARTYPE_LIST = IP + "carType/list";
    /**
     * 车友圈 - 会员交友时在交友圈查看动态信息
     */
    public static final String CIRCLE_MYDYNAMIC = IP + "dynamic/myDynamic";
    /**
     * 车友圈 - 帖子-我的回复
     */
    public static final String CIRCLE_MYDYNAMICCOMMEN = IP + "dynamic/comment/myDynamicCommen";


    /**
     * 图片上传
     */
    public static final String UPLOAD_IMAGE = IMG_IP + "picture/upload";
    /**
     * 首页  好车推荐
     */
    public static final String HOME_CAR_TUIJIAN = IP + "carmessage/queryByPage";
    /**
     * 首页  搜索
     */
    public static final String HOME_SEARCH = IP + "carmessage/queryByPage";
    /**
     * 选车
     */
    public static final String SELECT_CAR = IP + "carmessage/queryByPage";
    /**
     * 增加车辆信息(平台估价)
     */
    public static final String ASSESS_ADD_CAR = IP + "carmessage/saveCarMessage";
    /**
     * 会员修改车源信息。
     */
    public static final String ASSESS_UPDATE_CAR = IP + "carmessage/updateCarMessage";
    /**
     * 爱车估价列表
     */
    public static final String ASSESS_LIST_CAR = IP + "carmessage/queryByPage";
    /**
     * 车友圈 -搜索
     */
    public static final String CIRCLE_TUIJIAN = IP + "circle/list";
    /**
     * 车友圈 - 动态详情
     */
    public static final String CIRCLE_DETAIL = IP + "dynamic/list";
    /**
     * 我的动态详情
     * dynamic
     */
    public static final String MY_CIRCLE_DYNAMIC_DETAIL = IP + "dynamic/myDynamic";
    /**
     * 发动态
     */
    public static final String WRITE_POST = IP + "dynamic/updynamic";
    /**
     * 发布车源
     */
    public static final String ASSESS_LIST_CAR_PUBLISH = IP + "carmessage/issueCarMessage";
    /**
     * 加入圈子
     */
    public static final String CIRCLE_ADD = IP + "circle/addJoin";
    /**
     * 我已经加入的圈子
     */
    public static final String CIRCLE_ADD_LIST = IP + "circle/myCircleList";
    /**
     * 资讯类别
     */
    public static final String ZIXUN_TYPE = IP + "massageType/list";
    /**
     * 资讯类别item信息
     */
    public static final String ZIXUN_TYPE_ITEM = IP + "message/list";
    /**
     * 车辆详情webview
     */
    public static final String CAR_DETAIL = IP + "carmessage/queryByCarId";
    /**
     * 车辆详细---查看是否收藏
     */
    public static final String ISCONNECTION = IP + "collect/selectCollect";
    /**
     * 车辆详细---收藏
     */
    public static final String CONNECTION = IP + "collect/addCollect";
    /**
     * 车辆详细---取消收藏
     */
    public static final String UNCONNECTION = IP + "collect/cancelCollect";
    /**
     * 退出圈子
     */
    public static final String EXITCIRCLE = IP + "circle/delete";
    /**
     * 关注  某人
     */
    public static final String FOCUS_PEOPLE = IP + "care/addCare";
    /**
     * 动态  收藏
     */
    public static final String DYNAMIC_CONNECTION = IP + "collect/addCollect";
    /**
     * 动态  收藏
     */
    public static final String DYNAMIC_LIKE = IP + "hit/addHit";
    /**
     * 资讯  详情
     */
    public static final String ZIXUN_DETAIL = IP + "message/selectById";
    /**
     * 资讯  查看是否收藏
     */
    public static final String ZIXUN_IS_CONNECTION = IP + "message/selectStatus";
    /**
     * 资讯  收藏
     */
    public static final String ZIXUN_CONNECTION = IP + "collect/addCollect";
    /**
     * 资讯  点赞
     */
    public static final String ZIXUN_LIKE = IP + "hit/addHit";
    /**
     * 动态 评论
     */
    public static final String DYNAMIC_COMMENT = IP + "dynamic/comment/addComment";
    /**
     * 资讯 评论
     */
    public static final String ZIXUN_COMMENT = IP + "dynamic/comment/addMessage";
    /**
     * 会员用户查看动态评论信息。
     */
    public static final String DYNAMIC_COMMENT_LIST = IP + "dynamic/comment/list";
    /**
     * 会员查看资讯信息评论
     */
    public static final String ZIXUN_COMMENT_LIST = IP + "dynamic/comment/messageList";
    /**
     * 根据uid查询用户信息
     */
    public static final String USERINFO = IP + "member/selectMember";
    /**
     * 添加好友
     */
    public static final String ADDFRIEND = IP + "friend/addfriend";
    /**
     * 首页轮播图
     */
    public static final String HOME_BANNER = IP + "banner";
    /**
     * 资讯轮播图
     */
    public static final String ZIXUN_BANNER = IP + "message/thomebanner";
    /**
     * 首页评估师列表
     */
    public static final String HOME_GJSLIST = IP + "assess/list";
    /**
     * 我发布的服务列表接口
     */
    public static final String GJS_SERVER = IP + "assessService/mylist";
    /**
     * 车型类别接口
     */
    public static final String GJS_CARTYPE = IP + "carType/list";
    /**
     * 会员查看评估师的评估记录
     */
    public static final String GJS_RECORD = IP + "carAssess/list";
    /**
     * 评估师查看用户对个人服务的所有评价。
     */
    public static final String GJS_WORKASSESS = IP + "assessComment/list";
    /**
     *所有评估师发布的服务信息。server
     */
    public static final String GJS_ALLSERVER = IP + "assessService/list";
}
