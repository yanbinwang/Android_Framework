package com.wyb.iocframe.base;

import android.view.View;
import android.view.ViewGroup;

import com.wyb.iocframe.util.glide.GlideUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * abstract修饰类，会使这个类成为一个抽象类，这个类将不能生成对象实例，
 * 但可以做为对象变量声明的类型，也就是编译时类型，抽象类就像当于一类的
 * 半成品，需要子类继承并覆盖其中的抽象方法。
 * 所有listview，gridview的父类
 * @author wyb
 *
 * @param <T>
 */
public abstract class BaseAdapter<T extends BaseEntity>  extends android.widget.BaseAdapter {
    protected List<T> mData = new ArrayList<>();
    protected GlideUtil mGlide;

    public BaseAdapter(List<T> mData) {
        this.mData = mData;
    }

    public int getCount() {
        return mData.size();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // 图片加载需要new出工具类
    public View getView(int position, View convertView, ViewGroup parent) {
        setGlide(parent);
        return setView(position, convertView, parent);
    }

    public View setView(int position, View convertView, ViewGroup parent) {
        return convertView;
    }

    public void setGlide(ViewGroup parent){
        if(mGlide == null){
            mGlide = new GlideUtil(parent.getContext());
        }
    }

    public void setData(List<T> mData){
        this.mData = mData;
        notifyDataSetChanged();
    }

}
