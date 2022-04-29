package com.dataqin.testnew.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.text.format.Formatter;

import com.dataqin.base.utils.LogUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MemoryHelper {

    /**
     * 获取android总运行内存大小
     *
     * @param context
     */
    public static String getTotalMemory(Context context) {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                LogUtil.i(str2, num + "\t");
            }
            //获得系统总内存，单位是KB
            int i = Integer.parseInt(arrayOfString[1]);
            //int值乘以1024转换为long类型
            initial_memory = (long) i * 1024;
            localBufferedReader.close();
        } catch (IOException ignored) {
        }
        return Formatter.formatFileSize(context, initial_memory);//Byte转换为KB或者MB，内存大小规格化
    }

    /**
     * 获取android当前可用运行内存大小
     *
     * @param context
     */
    public static String getAvailMemory(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(memoryInfo);
        return Formatter.formatFileSize(context, memoryInfo.availMem);//将获取的内存大小规格化
    }

    /**
     * 获取当前应用使用的内存大小
     *
     * @return 单位 MB
     */
    public static double sampleMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        double mem = 0.0D;
        try {
            final Debug.MemoryInfo[] memInfo = activityManager.getProcessMemoryInfo(new int[]{android.os.Process.myPid()});
            if (memInfo.length > 0) {
                final int totalPss = memInfo[0].getTotalPss();
                if (totalPss >= 0) {
                    mem = totalPss / 1024.0D;
                }
            }
        } catch (Exception ignored) {
        }
        return mem;
    }

}