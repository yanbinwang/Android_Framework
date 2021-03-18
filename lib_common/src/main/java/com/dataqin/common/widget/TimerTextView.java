package com.dataqin.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dataqin.common.utils.helper.TimeTaskHelper;

import java.text.MessageFormat;

/**
 * Created by wangyanbin
 * 倒计时textview
 * 配置enable的xml和默認text文案即可
 */
@SuppressLint("AppCompatCustomView")
public class TimerTextView extends TextView {

    public TimerTextView(Context context) {
        super(context);
    }

    public TimerTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TimerTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void countDown() {
        countDown(1000);
    }

    public void countDown(long second) {
        TimeTaskHelper.startCountDown(second, new TimeTaskHelper.OnCountDownListener() {
            @Override
            public void onFinish() {
                setEnabled(true);
                setText("重发验证码");
            }

            @Override
            public void onTick(long second) {
                setEnabled(false);
                setText(MessageFormat.format("已发送{0}S", second));
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        TimeTaskHelper.stopCountDown();
    }

}
