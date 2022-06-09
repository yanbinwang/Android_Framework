package com.dataqin.websocket.utils;

import android.util.Log;

import com.dataqin.websocket.BuildConfig;

/**
 * Logable 默认实现类
 * <p>
 * Created by ZhangKe on 2019/4/29.
 */
public class LogTableUtil implements LogTableImpl {
    private final boolean debug = BuildConfig.ISDEBUG;

    @Override
    public void v(String tag, String msg) {
        if (debug) Log.v(tag, msg);
    }

    @Override
    public void v(String tag, String msg, Throwable tr) {
        if (debug) Log.v(tag, msg, tr);
    }

    @Override
    public void d(String tag, String text) {
        if (debug) Log.d(tag, text);
    }

    @Override
    public void d(String tag, String text, Throwable tr) {
        if (debug) Log.d(tag, text, tr);
    }

    @Override
    public void i(String tag, String text) {
        if (debug) Log.i(tag, text);
    }

    @Override
    public void i(String tag, String text, Throwable tr) {
        if (debug) Log.i(tag, text, tr);
    }

    @Override
    public void e(String tag, String text) {
        if (debug) Log.e(tag, text);
    }

    @Override
    public void e(String tag, String msg, Throwable tr) {
        if (debug) Log.e(tag, msg, tr);
    }

}