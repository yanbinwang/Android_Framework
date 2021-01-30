package com.dataqin.common.widget.highlight.position;

import android.graphics.RectF;

import com.dataqin.common.widget.highlight.HeightLight;


/**
 * Created by caizepeng on 16/8/20.
 */
public class OnTopPosCallback extends OnBaseCallback {
    public OnTopPosCallback() {
    }

    public OnTopPosCallback(float offset) {
        super(offset);
    }

    @Override
    public void getPosition(float rightMargin, float bottomMargin, RectF rectF, HeightLight.MarginInfo marginInfo) {
        marginInfo.leftMargin = rectF.right - rectF.width() / 2;
        marginInfo.bottomMargin = bottomMargin + rectF.height() + offset;
    }

}