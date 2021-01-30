package com.dataqin.common.widget.highlight.position;

import android.graphics.RectF;

import com.dataqin.common.widget.highlight.HeightLight;

/**
 * Created by caizepeng on 16/8/20.
 */
public class OnLeftPosCallback extends OnBaseCallback {
    public OnLeftPosCallback() {
    }

    public OnLeftPosCallback(float offset) {
        super(offset);
    }

    @Override
    public void getPosition(float rightMargin, float bottomMargin, RectF rectF, HeightLight.MarginInfo marginInfo) {
        marginInfo.rightMargin = rightMargin + rectF.width() + offset;
        marginInfo.topMargin = rectF.top;
    }

}