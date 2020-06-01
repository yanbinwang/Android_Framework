package com.example.common.utils.helper;

import android.content.Context;

import com.example.common.R;
import com.example.framework.utils.SHPUtil;

/**
 * Created by WangYanBin on 2020/6/1.
 * 存储应用配置
 */
public class ConfigHelper {
    private static SHPUtil appInfoSHP;
    private static SHPUtil appConfigSHP;

    public static void init(Context context) {
        appInfoSHP = new SHPUtil(context, context.getString(R.string.shp_app_info_fileName));
        appConfigSHP = new SHPUtil(context, context.getString(R.string.shp_app_configure_fileName));
    }

}
