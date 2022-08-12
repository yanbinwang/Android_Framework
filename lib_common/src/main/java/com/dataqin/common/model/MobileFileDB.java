package com.dataqin.common.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 手机文件数据库
 */
@Entity
public class MobileFileDB {
    @Id
    private String sourcePath;//源文件路径
    private String userId;//用户id
    private String baoquan;//文件唯一识别码（保留字段）
    private String extras;//历史json
    private int index;//当前下标
    private long filePointer;//记录切片的下标
    private boolean upload;//是否正在提交
    private boolean complete;//是否成功提交

    @Generated(hash = 1281364706)
    public MobileFileDB(String sourcePath, String userId, String baoquan, String extras, int index, long filePointer, boolean upload, boolean complete) {
        this.sourcePath = sourcePath;
        this.userId = userId;
        this.baoquan = baoquan;
        this.extras = extras;
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
    public String getExtras() {
        return this.extras;
    }
    public void setExtras(String extras) {
        this.extras = extras;
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

}