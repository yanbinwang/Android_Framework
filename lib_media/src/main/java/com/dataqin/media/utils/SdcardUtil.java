package com.dataqin.media.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.dataqin.base.utils.LogUtil;
import com.dataqin.media.model.SdcardStateModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SdcardUtil {
    public final static String DIR_SINGLE_SDCARD_NAME = "内置存储卡";
    public final static String DIR_SDCARD_NAME = "内置存储卡";
    public final static String DIR_EXT_SDCARD_NAME = "扩展存储卡";
    private final static long SD_PHY_SIZE_1G = 1000 * 1000 * 1000;
    private final static long SD_LOGIC_SIZE_1G = 1024 * 1024 * 1024;
    private final static double SD_LOGIC_DIFF = SD_LOGIC_SIZE_1G / (double) SD_PHY_SIZE_1G;
    private final static String SDCARD_PATH = getSDCardPath();
    private final static String TAG = "SdCardUtil";

    // <editor-fold defaultstate="collapsed" desc="sd卡容量的操作方法">
    /**
     * 获得内置sd卡剩余容量，即可用大小，单位M
     * @param context
     * @return
     */
    public static long getInnerSDAvailableSize(Context context) {
        ArrayList<SdcardStateModel> sdcardStateModelList = getSdCardStateModels(context);
        if (sdcardStateModelList.size() > 0) {
            return sdcardStateModelList.get(0).getFreeSize() / 1024 / 1024;
        } else {
            return 0;
        }
    }

    /**
     * 获得内置sd卡已用容量 单位M
     * @param context
     * @return
     */
    public static long getInnerSDUsedSize(Context context) {
        ArrayList<SdcardStateModel> sdcardStateModelList = getSdCardStateModels(context);
        if (sdcardStateModelList.size() > 0) {
            SdcardStateModel SdCardStateModel = sdcardStateModelList.get(0);
            return (SdCardStateModel.getTotalSize() - SdCardStateModel.getFreeSize()) / 1024 / 1024;
        } else {
            return 0;
        }
    }

    /**
     * 获得sd卡剩余容量，即可用大小，单位M
     * @param context
     * @return
     */
    public static long getSDAvailableSize(Context context) {
        ArrayList<SdcardStateModel> sdcardStateModelList = getSdCardStateModels(context);
        if (sdcardStateModelList.size() > 0) {
            return sdcardStateModelList.get(sdcardStateModelList.size() - 1).getFreeSize() / 1024 / 1024;
        } else {
            return 0;
        }
    }

    /**
     * 获得sd卡已用容量 单位M
     * @param context
     * @return
     */
    public static long getSDUsedSize(Context context) {
        ArrayList<SdcardStateModel> sdcardStateModelList = getSdCardStateModels(context);
        if (sdcardStateModelList.size() > 0) {
            SdcardStateModel SdCardStateModel = sdcardStateModelList.get(sdcardStateModelList.size() - 1);
            return (SdCardStateModel.getTotalSize() - SdCardStateModel.getFreeSize()) / 1024 / 1024;
        } else {
            return 0;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="sd卡基础属性获取方法">
    /**
     * sd卡是否可用
     * @return
     */
    public static boolean isMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取sd卡路径
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 传入的sd卡路径是否可再次写入
     * @param path
     * @return
     */
    public static boolean sdCardCanWrite(String path) {
        if (path == null) {
            return false;
        }

        File SdCardRoot = new File(path);
        if (!SdCardRoot.canWrite()) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= 19) {
            //canWrite() 在4.4系统不起作用，只要路径存在总是返回true
            File testPath = new File(new File(path), ".testwrite" + System.currentTimeMillis());
            if (testPath.mkdirs()) {
                testPath.delete();
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * 获取SD卡显示路径.<br>
     * 类似<strong>/storage/emulated/0</strong>不需要显示路径.<br>
     * 类似<strong>/storage/extSdCard/Android/data/{pageageName}/files</strong>
     * 只显示从<strong>/Android</strong>开头的路径.
     */
    public static String getShowSDPath(SdcardStateModel stat) {
        String showPath = "";
        String path = stat.getRootPath();
        if (Build.VERSION.SDK_INT >= 19 && !stat.getCanWrite()) {
            int index = path.indexOf("Android/data/");
            if (index != -1) {
                showPath = path.substring(index);
            }
        } else {
            showPath = path.substring(path.lastIndexOf(File.separator) + 1);
            if (showPath.equals("0")) {
                showPath = "";
            }
        }
        return showPath;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="sd卡详细的对象获取">
    public static ArrayList<SdcardStateModel> getSdCardStateModels(Context context) {
        ArrayList<SdcardStateModel> list = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("mount");
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str, lowerStr;
            while ((str = br.readLine()) != null) {
                lowerStr = str.toLowerCase();
                if (!testBasicFilter(lowerStr)) {
                    continue;
                }
                String[] cols = str.split("\\s+");
                if (cols == null) {
                    continue;
                }

                String path = findSDCardPath(cols);
                LogUtil.d(TAG, "path--------0-------" + path);
                if (TextUtils.isEmpty(path)) {
                    continue;
                }
                SdcardStateModel.Format format = findSDCardFormat(cols);
                if (format == null) {
                    continue;
                }

                int minorIdx = (SdcardStateModel.Format.vfat == format || SdcardStateModel.Format.exfat == format || SdcardStateModel.Format.texfat == format) ? findVoldDevNodeMinorIndex(cols) : -100;
                SdcardStateModel stat = new SdcardStateModel(path, format, minorIdx,"");
                LogUtil.d(TAG, "path--------1-------" + path);
                if (!compareData(list, stat.getTotalSize())) {
                    continue;
                }

                // 4.4以上版本修改trootPath路径，因为4.4及以上版本不支持往外置SD卡根目录写权限
                if (Build.VERSION.SDK_INT >= 19) {
                    if (!sdCardCanWrite(path)) {
                        stat.setCanWrite(false);
                        File[] filePath = ContextCompat.getExternalFilesDirs(
                                context, null);
                        if (filePath != null) {
                            for (File f : filePath) {
                                if (f != null) {
                                    if (f.getAbsolutePath().startsWith(path)) {
                                        stat.setRootPath(f.getAbsolutePath());
                                        LogUtil.d(TAG, "path--------if-------" + path);
                                        list.add(stat);
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        LogUtil.d(TAG, "path--------else-------" + path);
                        list.add(stat);
                    }
                } else {
                    LogUtil.d(TAG, "path--------other-------" + path);
                    list.add(stat);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return list;
        }

        list = sortSDCardList(list);
        for (int idx = 0, size = list.size(); idx < size; idx++) {
            if (idx == 0) {
                list.get(0).setName((size == 1) ? DIR_SINGLE_SDCARD_NAME : DIR_SDCARD_NAME);
            } else if (idx == 1) {
                list.get(1).setName(DIR_EXT_SDCARD_NAME);
            } else {
                list.get(idx).setName(DIR_EXT_SDCARD_NAME + idx);
            }
        }
        return list;
    }

    //是否能通过基本过滤
    private static boolean testBasicFilter(String str) {
        String[] keys = new String[]{"sd", "emmc", "hwuserdata", "udisk",
                "ext_card", "usbotg", "disk1", "disk2", "disk3", "disk4",
                "usbdrivea", "usbdriveb", "usbdrivec", "usbdrived", "storage",
                "external"};
        for (String key : keys) {
            if (str.contains(key)) {
                return true;
            }
        }
        return false;
    }

    //根据mount信息解析sdcard路径
    private static String findSDCardPath(String[] mountInfo) {
        String lowerStr;
        for (String col : mountInfo) {
            lowerStr = col.toLowerCase();
            // lenovo 部分手机会把扩展卡bind镜像 /mnt/extrasd_bind
            if ((lowerStr.contains("sd") && !lowerStr.contains("extrasd_bind"))
                    || lowerStr.contains("emmc")
                    || lowerStr.contains("ext_card")
                    || lowerStr.contains("external_sd")
                    || lowerStr.contains("usbstorage")) {
                String pDir = getParentPath(col);
                // onda平板 扩展卡 /mnt/sdcard/external_sdcard, 三星note扩展卡
                // /mnt/sdcard/external_sd
                // Sony C6603 扩展卡 /storage/removable/sdcard1
                if (pDir.equals(getParentPath(SDCARD_PATH))
                        || pDir.equals(SDCARD_PATH)
                        || pDir.equals(SDCARD_PATH + "/")
                        || pDir.equals("/storage/")
                        || pDir.equals("/storage/removable/")) {
                    return col;
                }
            }

            if ((col.contains("/storage/") && !col.contains("self") && !col.contains("legacy"))) {
                LogUtil.d(TAG, "storage--------------" + col);
                return col;
            }
            if (col.equals("/mnt/ext_sdcard")) {
                // 华为p6扩展卡
                return col;
            }
            if (col.equals("/udisk")) {
                // coolpad 内置卡 /udisk
                return col;
            }
            if (col.equals("/HWUserData")) {
                // 部分 huawei 内置卡 /HWUserData
                return col;
            }
            if (col.equals("/storage/external")) {
                // coolpad8720l 外置卡
                return col;
            }
            if (col.equals("/Removable/MicroSD")) {
                // ASUS_T00G
                return col;
            }
        }
        return null;
    }

    //取上一级路径
    private static String getParentPath(String path) {
        if (path != null && path.length() > 0) {
            path = path.substring(0, path.length() - 1); // 去掉最后一个字符 ， 以兼容以“/”
            // 结尾的路径
            return path.substring(0, path.lastIndexOf(File.separator) + 1);
        } else {
            return "";
        }
    }

    //根据mount信息解析sdcard分区格式
    private static SdcardStateModel.Format findSDCardFormat(String[] mountInfo) {
        int formatMinLength = 0;
        int formatMaxLength = 0;
        for (SdcardStateModel.Format format : SdcardStateModel.Format.values()) {
            int len = format.toString().length();
            if (len > formatMaxLength) {
                formatMaxLength = len;
            } else if (len < formatMinLength) {
                formatMinLength = len;
            }
        }

        for (String col : mountInfo) {
            if (col.length() < formatMinLength || col.length() > formatMaxLength) {
                continue;
            }
            for (SdcardStateModel.Format format : SdcardStateModel.Format.values()) {
                if (format.toString().equals(col)) {
                    return format;
                }
            }
        }
        return null;
    }

    //1.判断如果总容量小于2G,则排除 2.排除内置或外置重复路径
    public static boolean compareData(ArrayList<SdcardStateModel> list, long capacity) {
        //排除内置或外置重复路径
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getTotalSize() == capacity) {
                    LogUtil.d(TAG, "duplicate-------------------------");
                    return false;
                }
            }
        }
        //判断如果总容量小于2G
        if (capacity / 1073741824 < 2) {
            LogUtil.d(TAG, "capacity/ 1073741824-------------------------" + capacity / 1073741824);
            return false;
        }
        return true;
    }

    //解析Vold(vfat格式)次设备号
    private static int findVoldDevNodeMinorIndex(String[] mountInfo) {
        String voldInfo = findVoldDevNodeIndex(mountInfo);
        if (TextUtils.isEmpty(voldInfo)) {
            return -1;
        }

        String[] infos = voldInfo.split(":");
        if (infos == null || infos.length < 2) {
            return -1;
        }
        return Integer.valueOf(infos[1]);
    }

    //解析Vold设备号
    private static String findVoldDevNodeIndex(String[] mountInfo) {
        if (mountInfo == null || mountInfo.length <= 0) {
            return null;
        }

        String voldInfo = mountInfo[0];
        if (TextUtils.isEmpty(voldInfo)) {
            return null;
        }
        return voldInfo.replaceFirst("/dev/block/vold/", "");
    }

    //根据设备挂载次序排序SDCard
    private static ArrayList<SdcardStateModel> sortSDCardList(ArrayList<SdcardStateModel> list) {
        ArrayList<SdcardStateModel> resultList = new ArrayList<>();
        int minIdx = 0;
        for (SdcardStateModel stat : list) {
            if (minIdx == 0) {
                resultList.add(stat);
                minIdx = stat.getVoldMinorIdx();
                continue;
            }

            if (stat.getVoldMinorIdx() < minIdx || isInnerSdcard(stat.getRootPath(), stat.getTotalSize())) {
                resultList.add(0, stat);
                minIdx = stat.getVoldMinorIdx();
            } else {
                resultList.add(stat);
            }
        }
        return resultList;
    }

    private static boolean isInnerSdcard(String path, long totalSize) {
        try {
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            return !isPhySize(totalSize) && (Environment.getExternalStorageDirectory().getAbsoluteFile().getCanonicalPath() + "/").equals(path);
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean isPhySize(long totalSize) {
        boolean result = false;
        long count = totalSize / SD_PHY_SIZE_1G;
        if (count % 2 == 0) {
            count = count + 0;
        } else {
            count = count + 1;
        }
        if (!nCF3((int) count) || 0 >= totalSize) {
            return result;
        }
        double real_diff = SD_LOGIC_SIZE_1G * count / (double) totalSize;
        // 1.063 <= real_diff <= 1.083
        result = real_diff >= SD_LOGIC_DIFF - 0.01
                && real_diff <= SD_LOGIC_DIFF + 0.01;
        return result;
    }

    private static boolean nCF3(int n) {
        boolean boo = true;
        String s = Integer.toBinaryString(n);
        byte[] b = s.getBytes();
        for (int i = 1; i < b.length; i++) {
            if (b[i] != 48) {
                boo = false;
                break;
            }
        }
        return boo;
    }
    // </editor-fold>

}