package com.example.common.widget.xrecyclerview.refresh;

import android.content.Context;
import android.view.View;

import com.example.common.R;
import com.lcodecore.tkrefreshlayout.IHeaderView;
import com.lcodecore.tkrefreshlayout.OnAnimEndListener;


/**
 * author:wyb
 * 自定义刷新控件头部
 */
public class RefreshHeaderView implements IHeaderView {
    private Context context;
//    private GifDrawable gifDrawable;

    public RefreshHeaderView(Context context) {
        this.context = context;
    }

    //获取刷新的整体view
    @Override
    public View getView() {
        View rootView = View.inflate(context, R.layout.view_refresh_header, null);
//        GifImageView loadingGif = rootView.findViewById(R.id.loading_gif);
//        try {
//            gifDrawable = new GifDrawable(context.getResources(), R.drawable.bg_loading);
//            loadingGif.setImageDrawable(gifDrawable);
//            gifDrawable.start();
//        } catch (Exception ignored) {
//        }
        return rootView;
    }

    //当控件下拉时触发
    @Override
    public void onPullingDown(float fraction, float maxHeadHeight, float headHeight) {
//        if (!gifDrawable.isRunning()) {
//            gifDrawable.seekTo(0);
//            gifDrawable.start();
//        }
    }

    //刷新被复原时--复位
    @Override
    public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {
//        if (gifDrawable.isRunning()) {
//            gifDrawable.seekTo(0);
//            gifDrawable.stop();
//        }
    }

    //触发执行动画时，文字和图片的样式
    @Override
    public void startAnim(float maxHeadHeight, float headHeight) {
    }

    //动画执行完毕时，结束
    @Override
    public void onFinish(OnAnimEndListener animEndListener) {
        animEndListener.onAnimEnd();
    }

    //重置复位
    @Override
    public void reset() {
    }

}
