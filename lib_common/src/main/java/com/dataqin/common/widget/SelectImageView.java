package com.dataqin.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.dataqin.common.R;

/**
 * Created by wangyanbin
 * 选择按钮，设定图片后点击切换
 */
@SuppressLint("AppCompatCustomView")
public class SelectImageView extends ImageView {
    private boolean select;//false代表未开始(normalRes)，true代表开始(focusedRes)
    private Drawable normalRes, focusedRes;//正常时的背景ID,选中时的背景ID
    private OnItemClickListener onItemClickListener;//点击返回状态

    public SelectImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    public SelectImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs);
    }

    private void initialize(AttributeSet attrs) {
        TypedArray mTypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SelectImageView);
        normalRes = mTypedArray.getDrawable(R.styleable.SelectImageView_normalRes);
        focusedRes = mTypedArray.getDrawable(R.styleable.SelectImageView_focusedRes);
        mTypedArray.recycle();
        setScaleType(ScaleType.FIT_XY);
        setOnClickListener(v -> {
            select = !select;
            setBackground(select ? focusedRes : normalRes);
            if (null != onItemClickListener) {
                onItemClickListener.onItemClickListener(select);
            }
        });
    }

    //设置监听
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        void onItemClickListener(boolean select);

    }

}
