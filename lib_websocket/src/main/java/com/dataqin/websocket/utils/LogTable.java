package com.dataqin.websocket.utils;

import com.dataqin.websocket.WebSocketHandler;

/**
 * 日志工具类
 * <p>
 * Created by ZhangKe on 2019/3/21.
 */
public class LogTable {

    public static void v(String tag, String msg) {
        WebSocketHandler.getLogTable().v(tag, msg);
    }

    public static void v(String tag, String msg, Throwable tr) {
        WebSocketHandler.getLogTable().v(tag, msg, tr);
    }

    public static void d(String tag, String text) {
        WebSocketHandler.getLogTable().d(tag, text);
    }

    public static void d(String tag, String text, Throwable tr) {
        WebSocketHandler.getLogTable().d(tag, text, tr);
    }

    public static void i(String tag, String text) {
        WebSocketHandler.getLogTable().i(tag, text);
    }

    public static void i(String tag, String text, Throwable tr) {
        WebSocketHandler.getLogTable().i(tag, text, tr);
    }

    public static void e(String tag, String text) {
        WebSocketHandler.getLogTable().e(tag, text);
    }

    public static void e(String tag, String msg, Throwable tr) {
        WebSocketHandler.getLogTable().e(tag, msg, tr);
    }

}