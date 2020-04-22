package com.example.common.constant;

/**
 * Created by wyb on 2016/6/28.
 * app内跳转回调编码
 */
public interface RequestCode {
    //app内请求编号
    int CODE_100 = 100;//个人中心
    int CODE_103 = 103;//提交实名申请
    int CODE_105 = 105;//修改登录密码
    int CODE_112 = 112;//获取实名认证信息
    int CODE_400 = 400;//生成RSA公私钥
    int CODE_402 = 402;//版本检查
    int CODE_406 = 406;//H5
    int CODE_408 = 408;//获取计价货币
    int CODE_409 = 409;//设置计价货币
    int CODE_600 = 600;//发送短信验证码
    int CODE_601 = 601;//短信验证码验证
    int CODE_602 = 602;//手机号登录
    int CODE_604 = 604;//登出
    int CODE_605 = 605;//ip解锁
    int CODE_607 = 607;//注册
    //app内页面跳转回调编号
    int FINISH_REQUEST = 10000;//批量关闭回调编码
    int PHOTO_REQUEST = 10001;//图片回调编码
}
