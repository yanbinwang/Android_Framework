package com.dataqin.common.constant

/**
 * Created by wyb on 2016/6/28.
 * app内的跳转字段
 */
object Extras {
    const val REQUEST_ID = "requestId" //请求id
    const val BUNDLE_BEAN = "bundleBean" //跳转对象
    const val REQUEST_CODE = "requestCode" //页面跳转链接
    const val RESULT_CODE = "resultCode" //页面跳转链接
    const val PAGE_FROM = "pageFrom" //来自何种页面
    const val PAGE_TYPE = "pageType" //页面类型
    const val FILE_PATH = "filePath" //文件路径
    const val FILE_INDEX = "fileIndex" //文件下标

    const val PAYLOAD = "payLoad" //透传信息
    const val IS_EXISTS = "isExists" //是否创建
    const val IS_RUNNING = "isRunning" //是否正在运行

    const val WEB_URL = "webUrl" //网页链接
    const val WEB_STATE = "webState" //网页是否是深色
    const val WEB_COLOR = "webColor" //网页标题颜色
    const val MOBILE = "mobile" //手机号
    const val VERIFY_SMS = "verifySms" //短信验证码
}