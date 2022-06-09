package com.dataqin.websocket.utils;

import android.util.Log;

/**
 * Logable 默认实现类
 * <p>
 * Created by ZhangKe on 2019/4/29.
 */
public class LogTableUtil implements LogTableImpl {

    @Override
    public void v(String tag, String msg) {
        Log.v(tag, msg);
    }

    @Override
    public void v(String tag, String msg, Throwable tr) {
        Log.v(tag, msg, tr);
    }

    @Override
    public void d(String tag, String text) {
        Log.d(tag, text);
    }

    @Override
    public void d(String tag, String text, Throwable tr) {
        Log.d(tag, text, tr);
    }

    @Override
    public void i(String tag, String text) {
        Log.i(tag, text);
    }

    @Override
    public void i(String tag, String text, Throwable tr) {
        Log.i(tag, text, tr);
    }

    @Override
    public void e(String tag, String text) {
        Log.e(tag, text);
    }

    @Override
    public void e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
    }

}