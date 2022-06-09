package com.dataqin.websocket.utils;

/**
 * 打印日志接口
 * <p>
 * Created by ZhangKe on 2019/4/29.
 */
public interface LogTableImpl {

    void v(String tag, String msg);

    void v(String tag, String msg, Throwable tr);

    void d(String tag, String text);

    void d(String tag, String text, Throwable tr);

    void i(String tag, String text);

    void i(String tag, String text, Throwable tr);

    void e(String tag, String text);

    void e(String tag, String msg, Throwable tr);

}