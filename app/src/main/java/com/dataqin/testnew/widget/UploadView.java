package com.dataqin.testnew.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.dataqin.base.widget.SimpleViewGroup;
import com.dataqin.common.imageloader.ImageLoader;
import com.dataqin.common.utils.helper.TimeTaskHelper;
import com.dataqin.testnew.R;

/**
 * Created by wangyanbin
 * 图片上传view
 * 常态，上传中，完成
 */
public class UploadView extends SimpleViewGroup {
    private View view;
    private ImageView ivTips;//贴士图片
    private ImageView ivUpload;//完成时的背景图
    private RelativeLayout rlContainer;//未上传时的展示
    private ProgressBar pbTips;
    private int upProgress = 10;//每秒增长值

    public UploadView(Context context) {
        super(context);
        initialize();
    }

    public UploadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public UploadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.view_upload, null);
        ivTips = view.findViewById(R.id.iv_tips);
        ivUpload = view.findViewById(R.id.iv_upload);
        rlContainer = view.findViewById(R.id.rl_container);
        pbTips = view.findViewById(R.id.pb_tips);
        onNormal();
    }

    @Override
    public void draw() {
        if (detectionInflate()) addView(view);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopLoading();
    }

    //默认常态
    public void onNormal() {
        ivUpload.setVisibility(View.GONE);
        pbTips.setVisibility(View.GONE);
        rlContainer.setVisibility(View.VISIBLE);
    }

    //加载中
    public void onLoading() {
        ivUpload.setVisibility(View.GONE);
        pbTips.setVisibility(View.VISIBLE);
        rlContainer.setVisibility(View.VISIBLE);
        pbTips.setProgress(0);
        startLoading();
    }

    //加载完成
    public void onComplete(String url) {
        ivUpload.setVisibility(View.VISIBLE);
        rlContainer.setVisibility(View.GONE);
        stopLoading();
        ImageLoader.getInstance().displayImage(ivUpload, url);
    }

    private void startLoading() {
        TimeTaskHelper.startTask(1000, () -> {
            int progress = pbTips.getProgress();
            if (progress <= 100) {
                pbTips.setProgress(progress + upProgress);
            } else {
                stopLoading();
            }
        });
    }

    private void stopLoading() {
        TimeTaskHelper.stopTask();
    }

    //小贴士背景
    public void setTips(int resId) {
        ivTips.setImageResource(resId);
    }

}
