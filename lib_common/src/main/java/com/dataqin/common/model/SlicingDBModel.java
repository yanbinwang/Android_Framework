package com.dataqin.common.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by wangyanbin
 * 分片对象类
 */
@Entity
public class SlicingDBModel {
    @Id
    private String sourcePath;//主键标识(文件在手机中的路径)
    private String userId;//当前用户的id(区别不同身份)
    private String baoquan;//文件唯一识别码，用于发起自动上传
    private String slicingJson;//具体切片文件的json，转换为SlicingModel
    private String extrasJson;//文件详细信息的json，转换为ExtrasModel
    private boolean isSlicing;//标识当前数据是否是分片文件
    private boolean isSubmit;//标识当前数据的文件是否正在提交
    private boolean isComplete;//标识当前数据的文件是否已经完成上传

    @Generated(hash = 1714911247)
    public SlicingDBModel(String sourcePath, String userId, String baoquan, String slicingJson, String extrasJson, boolean isSlicing, boolean isSubmit, boolean isComplete) {
        this.sourcePath = sourcePath;
        this.userId = userId;
        this.baoquan = baoquan;
        this.slicingJson = slicingJson;
        this.extrasJson = extrasJson;
        this.isSlicing = isSlicing;
        this.isSubmit = isSubmit;
        this.isComplete = isComplete;
    }
    @Generated(hash = 1674225480)
    public SlicingDBModel() {
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
    public String getSlicingJson() {
        return this.slicingJson;
    }
    public void setSlicingJson(String slicingJson) {
        this.slicingJson = slicingJson;
    }
    public String getExtrasJson() {
        return this.extrasJson;
    }
    public void setExtrasJson(String extrasJson) {
        this.extrasJson = extrasJson;
    }
    public boolean getIsSlicing() {
        return this.isSlicing;
    }
    public void setIsSlicing(boolean isSlicing) {
        this.isSlicing = isSlicing;
    }
    public boolean getIsSubmit() {
        return this.isSubmit;
    }
    public void setIsSubmit(boolean isSubmit) {
        this.isSubmit = isSubmit;
    }
    public boolean getIsComplete() {
        return this.isComplete;
    }
    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

}
