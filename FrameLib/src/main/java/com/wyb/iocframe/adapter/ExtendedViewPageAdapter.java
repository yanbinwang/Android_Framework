package com.wyb.iocframe.adapter;



import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.wyb.iocframe.view.extendedviewpager.TouchImageView;

import java.util.ArrayList;
import java.util.List;


/** 图片轮播适配器*/
public class ExtendedViewPageAdapter extends PagerAdapter {
	private List<TouchImageView> mListViews = new ArrayList<>();

	public ExtendedViewPageAdapter(List<TouchImageView> mListViews) {
		this.mListViews = mListViews;
	}

	public int getCount() {
		return mListViews.size();
	}

	public boolean isViewFromObject(View view, Object object) {
		return (view == object);
	}

	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mListViews.get(position));// 删除页卡
	}

	public Object instantiateItem(ViewGroup container, int position) {
//		ImageView img = mListViews.get(position);
//		((ViewPager) container).addView(img);// 添加页卡
//		 TouchImageView img = new TouchImageView(container.getContext());
//         img.setImageResource(images[position]);
//         container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//         return img;
		TouchImageView img = mListViews.get(position);
		((ViewPager) container).addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);// 添加页卡
		return img;
	}

}
