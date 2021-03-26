package com.dataqin.testnew.widget.scale;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * Created by wangyanbin
 * 伸缩图片适配器
 */
public class ScaleAdapter extends PagerAdapter {
    private final List<ScaleImageView> mData;

    public ScaleAdapter(List<ScaleImageView> mData) {
        this.mData = mData;
    }

    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(mData.get(position));// 删除页卡
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ScaleImageView img = mData.get(position);
        container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);// 添加页卡
        return img;
    }

}
