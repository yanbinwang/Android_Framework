package com.dataqin.media.model;

import android.os.StatFs;

import java.io.File;

/**
 * Created by wangyanbin
 * Android4.4增加了SD卡读写权限设置，分为内置存储和外置SD卡，对权限见下表：<br>
 * <table width="60%" border="1" align="center">
 * <tr>
 * <th align="center">Action</th>
 * <th align="center">Primary</th>
 * <th align="center">Secondary</th>
 * </tr>
 * <tbody>
 * <tr>
 * <td>Read Top-Level Directories</td>
 * <td align="center">R</td>
 * <td align="center">R</td>
 * </tr>
 * <tr>
 * <td>Write Top-Level Directories</td>
 * <td align="center">W</td>
 * <td align="center">N</td>
 * </tr>
 * <tr>
 * <td>Read My Package&#8217;s Android Data Directory</td>
 * <td align="center">Y</td>
 * <td align="center">Y</td>
 * </tr>
 * <tr>
 * <td>Write My Package&#8217;s Android Data Directory</td>
 * <td align="center">Y</td>
 * <td align="center">Y</td>
 * </tr>
 * <tr>
 * <td>Read Another Package&#8217;s Android Data Directory</td>
 * <td align="center">R</td>
 * <td align="center">R</td>
 * </tr>
 * <tr>
 * <td>Write Another Package&#8217;s Android Data Directory</td>
 * <td align="center">W</td>
 * <td align="center">N</td>
 * </tr>
 * </tbody>
 * </table>
 * <p style="text-align: center;">
 * <strong>R = With Read Permission, W = With Write Permission, Y =
 * Always, N = Never </strong>
 * </p>
 * 根据上面表格判断SD类型，这个属性代表了Write Top-Level Directories的Secondary(外置SD卡).<br>
 * 由于部分手机厂商没有遵循Google新的SD卡规范，所以在部分Android4.4手机上外置SD卡的根目录仍然有读写
 * 权限.所以只有在Android4.4以上手机，并且外置SD卡不可写的情况此属性才为<strong>false</strong>.
 */
public class SdCardStateModel {
    public int voldMinorIdx;
    public long totalSize;
    public long freeSize;
    public boolean canWrite = true;
    public boolean isCaseSensitive;
    public String rootPath;
    public String excludePath; //排除路径，某些手机会将扩展卡挂载在sdcard下面
    public String name;
    public Format format;

    public enum Format {
        vfat, exfat, ext4, fuse, sdcardfs, texfat
    }

    public SdCardStateModel(String path, Format format, int voldMinorIdx, String excludePath) {
        DiskStateModel stat = getDiskCapacity(path);
        if (stat != null) {
            this.freeSize = stat.getFree();
            this.totalSize = stat.getTotal();
        }
        this.rootPath = path;
        this.format = format;
        this.isCaseSensitive = checkCaseSensitive(format);
        this.voldMinorIdx = voldMinorIdx;
        this.excludePath = excludePath;
    }

    public SdCardStateModel(String path, Format format, int voldMinorIdx) {
        this(path, format, voldMinorIdx, "");
    }

    public boolean checkCaseSensitive(Format format) {
        return format != Format.vfat && format != Format.exfat;
    }

    public void setExcludePath(String excludePath) {
        DiskStateModel excludeStat = getDiskCapacity(excludePath);
        if (excludeStat != null) {
            this.freeSize -= excludeStat.getFree();
            this.totalSize -= excludeStat.getTotal();
        }
        this.excludePath = excludePath;
    }

    public void refreshDiskCapacity() {
        DiskStateModel stat = getDiskCapacity(this.rootPath);
        if (stat != null) {
            this.freeSize = stat.getFree();
            this.totalSize = stat.getTotal();
        }
    }

    //计算目标路径的磁盘使用情况
    private static DiskStateModel getDiskCapacity(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long totalBlockCount = stat.getBlockCount();
        long feeBlockCount = stat.getAvailableBlocks();
        return new DiskStateModel(blockSize * feeBlockCount, blockSize * totalBlockCount);
    }

}
