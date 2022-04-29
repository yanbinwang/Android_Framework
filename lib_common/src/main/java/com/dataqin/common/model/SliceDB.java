package com.dataqin.common.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SliceDB {
    @Id
    private String baoquan;//主键标识（唯一）-》保全号
    private String sourcePath;//主键标识(文件在手机中的路径)
    private String userId;//当前用户的id
    private String extras;//详情历史展示json
    private boolean complete;//用于表示当前这个切片整体是否成功
    private boolean upload;//是否在提交
    private String tmpPath;//分片路径
    private int sliceCount;//分片总数
    private int index;//当前下标
    private long endPointer;//文件下标

    @Generated(hash = 1191197049)
    public SliceDB(String baoquan, String sourcePath, String userId, String extras, boolean complete, boolean upload, String tmpPath, int sliceCount, int index, long endPointer) {
        this.baoquan = baoquan;
        this.sourcePath = sourcePath;
        this.userId = userId;
        this.extras = extras;
        this.complete = complete;
        this.upload = upload;
        this.tmpPath = tmpPath;
        this.sliceCount = sliceCount;
        this.index = index;
        this.endPointer = endPointer;
    }
    @Generated(hash = 1475253643)
    public SliceDB() {
    }

    public String getBaoquan() {
        return this.baoquan;
    }
    public void setBaoquan(String baoquan) {
        this.baoquan = baoquan;
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
    public String getExtras() {
        return this.extras;
    }
    public void setExtras(String extras) {
        this.extras = extras;
    }
    public boolean getComplete() {
        return this.complete;
    }
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
    public boolean getUpload() {
        return this.upload;
    }
    public void setUpload(boolean upload) {
        this.upload = upload;
    }
    public String getTmpPath() {
        return this.tmpPath;
    }
    public void setTmpPath(String tmpPath) {
        this.tmpPath = tmpPath;
    }
    public int getSliceCount() {
        return this.sliceCount;
    }
    public void setSliceCount(int sliceCount) {
        this.sliceCount = sliceCount;
    }
    public int getIndex() {
        return this.index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public long getEndPointer() {
        return this.endPointer;
    }
    public void setEndPointer(long endPointer) {
        this.endPointer = endPointer;
    }
}