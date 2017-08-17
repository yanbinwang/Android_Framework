package com.wyb.iocframe.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import java.util.List;

/** 图片轮播适配器*/
public class ImagePageAdapter extends PagerAdapter {

	private List<ImageView> mListViews ;

	public ImagePageAdapter(List<ImageView> mListViews) {
		this.mListViews = mListViews;
	}

	public void setData(List<ImageView> mListViews) {
		this.mListViews = mListViews;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mListViews.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return (view == object);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mListViews.get(position));// 删除页卡
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView img = mListViews.get(position);
		img.setScaleType(ScaleType.CENTER_CROP);
		((ViewPager) container).addView(img);// 添加页卡
		return img;
	}

}
