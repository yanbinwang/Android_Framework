package com.dataqin.common.widget.highlight.position;

import android.graphics.RectF;

import com.dataqin.common.widget.highlight.HeightLight;

/**
 * Created by caizepeng on 16/8/20.
 */
public abstract class OnBaseCallback implements HeightLight.OnPosCallback {
    protected float offset;

    public OnBaseCallback() {
    }

    public OnBaseCallback(float offset) {
        this.offset = offset;
    }

    //如果需要调整位置,重写该方法
    public void posOffset(float rightMargin, float bottomMargin, RectF rectF, HeightLight.MarginInfo marginInfo) {
    }

    @Override
    public void getPos(float rightMargin, float bottomMargin, RectF rectF, HeightLight.MarginInfo marginInfo) {
        getPosition(rightMargin, bottomMargin, rectF, marginInfo);
        posOffset(rightMargin, bottomMargin, rectF, marginInfo);
    }

    public abstract void getPosition(float rightMargin, float bottomMargin, RectF rectF, HeightLight.MarginInfo marginInfo);

}
