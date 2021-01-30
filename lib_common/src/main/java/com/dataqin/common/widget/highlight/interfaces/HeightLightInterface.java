package com.dataqin.common.widget.highlight.interfaces;

import android.view.View;

import com.dataqin.common.widget.highlight.HeightLight;
import com.dataqin.common.widget.highlight.view.HeightLightView;

/**
 * 控制高亮控件的接口
 */
public interface HeightLightInterface {
    /**
     * 移除
     */
    HeightLight remove();

    /**
     * 显示
     */
    HeightLight show();

    /**
     * 显示下一个布局
     *
     * @return
     */
    HeightLight next();

    /**
     * @return 高亮布局控件
     */
    HeightLightView getHeightLightView();

    /**
     * @return 锚点布局
     */
    View getAnchor();

    /**
     * 点击事件
     */
    interface OnClickCallback {
        void onClick();
    }

    /**
     * 显示回调监听
     * heightLightView 高亮布局控件
     */
    interface OnShowCallback {
        void onShow(HeightLightView heightLightView);
    }

    /**
     * 移除回调监听
     */
    interface OnRemoveCallback {
        void onRemove();
    }

    /**
     * 下一个回调监听 只有Next模式下生效
     *  heightLightView 高亮布局控件
     *  targetView     高亮目标控件
     *  tipView        高亮提示控件
     */
    interface OnNextCallback {
        void onNext(HeightLightView heightLightView, View targetView, View tipView);
    }

    /**
     * mAnchor全局布局监听器-布局结束
     */
    interface OnLayoutCallback {
        void onLayout();
    }

}