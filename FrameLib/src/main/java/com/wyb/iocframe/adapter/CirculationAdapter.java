package com.wyb.iocframe.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import java.util.List;

/** 无限循环的viewpage适配器，适配好后需要让viewpage选择一个比较大的值*/
public class CirculationAdapter extends PagerAdapter {

	private List<ImageView> mListViews ;

	public CirculationAdapter(List<ImageView> mListViews) {
		this.mListViews = mListViews;
	}

	public int getCount() {
		return Integer.MAX_VALUE;
	}

	public boolean isViewFromObject(View view, Object object) {
		return (view == object);
	}

	public void destroyItem(ViewGroup container, int position, Object object) {}

	public Object instantiateItem(ViewGroup container, int position) {
		//对ViewPager页号求模取出View列表中要显示的项
        position %= mListViews.size();
        if (position < 0) {
            position = mListViews.size() + position;
        }
        ImageView view = mListViews.get(position);
        view.setScaleType(ScaleType.CENTER_CROP);
        //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        ViewParent vp = view.getParent();
        if (vp != null) {
            ViewGroup parent = (ViewGroup) vp;
            parent.removeView(view);
        }
        container.addView(view);
        return view;
	}

}
