package com.dataqin.common.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.File;

/**
 * 手机文件数据库
 */
@Entity
public class MobileFileDB {
    @Id
    private String sourcePath;//源文件路径
    private String userId;//用户id
    private String baoquan;//文件唯一识别码（保留字段）
    private int index;//当前下标
    private long filePointer;//记录切片的下标
    private boolean upload;//是否正在提交
    private boolean complete;//是否成功提交

    @Generated(hash = 1487927542)
    public MobileFileDB(String sourcePath, String userId, String baoquan, int index, long filePointer, boolean upload, boolean complete) {
        this.sourcePath = sourcePath;
        this.userId = userId;
        this.baoquan = baoquan;
        this.index = index;
        this.filePointer = filePointer;
        this.upload = upload;
        this.complete = complete;
    }
    @Generated(hash = 1915467265)
    public MobileFileDB() {
    }

    public String getSourcePath() {
        return this.sourcePath;
    }
    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getBaoquan() {
        return this.baoquan;
    }
    public void setBaoquan(String baoquan) {
        this.baoquan = baoquan;
    }
    public int getIndex() {
        return this.index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public long getFilePointer() {
        return this.filePointer;
    }
    public void setFilePointer(long filePointer) {
        this.filePointer = filePointer;
    }
    public boolean getUpload() {
        return this.upload;
    }
    public void setUpload(boolean upload) {
        this.upload = upload;
    }
    public boolean getComplete() {
        return this.complete;
    }
    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    //获取分片总数
    public int getTotal() {
        long targetLength = new File(sourcePath).length();
        return targetLength % getSize() == 0 ? (int) (targetLength / getSize()) : (int) (targetLength / getSize() + 1);
    }

    //配置分片大小
    public long getSize() {
        return 100 * 1024 * 1024;
    }
}