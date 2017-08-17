package com.wyb.iocframe.util.glide;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.wyb.iocframe.R;
import com.wyb.iocframe.config.CommonConfig;

/**
 * 加载图片工具类
 *
 * @author wyb
 *         .skipMemoryCache(true)可以跳过缓存
 *         一般APP需要有三级缓存（网络，数据库，缓存），所以基本不配置
 */
public class GlideUtil {
    private Context context;
    private RequestManager glideRequest;

    //控件实例化
    public GlideUtil(Context context) {
        this.context = context;
        glideRequest = Glide.with(context);
    }

    //普通的加载图片
    public void display(ImageView container, String url) {
        glideRequest
                .load(url)
                .placeholder(R.drawable.img_loading) //加载中的图片
                .error(R.mipmap.img_error) //加载失败的图片
                .crossFade()//渐隐动画
                .into(container);
    }

    //普通的加载图片---指定宽高比
    public void display(ImageView container, float scaleFactor, String url) {
        glideRequest
                .load(url)
                .placeholder(R.drawable.img_loading) //加载中的图片
                .error(R.mipmap.img_error) //加载失败的图片
                .crossFade()
                .into(container);
        setScaleFactor(container, scaleFactor);
    }

    //监听图片是否加载完成
    public void display(ImageView container, String url, final GlideLoadingCallback callback) {
        glideRequest
                .load(url)
                .placeholder(R.drawable.img_loading) //加载中的图片
                .error(R.mipmap.img_error) //加载失败的图片
                .crossFade()//渐隐动画
                .into(new GlideDrawableImageViewTarget(container) {
                    public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                        super.onResourceReady(drawable, anim);
                        if(null != callback){
                            callback.onLoadingOverCallBack();
                        }
                    }
                });
    }

    //弧形的加载图片
    public void displayRound(ImageView container, String url) {
        glideRequest
                .load(url)
                .transform(new GlideRoundTransform(context))
                .placeholder(R.drawable.img_loading)
                .error(R.mipmap.img_error)
                .crossFade()
                .into(container);
    }

    //弧形的加载图片---指定宽高比
    public void displayRound(ImageView container, float scaleFactor, String url) {
        glideRequest
                .load(url)
                .transform(new GlideRoundTransform(context))
                .placeholder(R.drawable.img_loading)
                .error(R.mipmap.img_error)
                .crossFade()
                .into(container);
        setScaleFactor(container, scaleFactor);
    }

    //弧形的加载图片(设置弧形角度)
    public void displayRound(ImageView container, String url, int roundNum) {
        glideRequest
                .load(url)
                .transform(new GlideRoundTransform(context, roundNum))
                .placeholder(R.drawable.img_loading)
                .error(R.mipmap.img_error)
                .crossFade()
                .into(container);
    }

    //弧形的加载图片(设置弧形角度)
    public void displayRound(ImageView container, String url, float scaleFactor, int roundNum) {
        glideRequest
                .load(url)
                .transform(new GlideRoundTransform(context, roundNum))
                .placeholder(R.drawable.img_loading)
                .error(R.mipmap.img_error)
                .crossFade()
                .into(container);
        setScaleFactor(container, scaleFactor);
    }

    // 圆形的加载图片
    public void displayCircle(ImageView container, String url) {
        glideRequest
                .load(url)
                .transform(new GlideCircleTransform(context))
                .placeholder(R.drawable.img_loading_circle)
                .error(R.mipmap.img_error_circle)
                .crossFade()
                .into(container);
    }

    // 圆形的加载图片
    public void displayCircle(ImageView container, float scaleFactor, String url) {
        glideRequest
                .load(url)
                .transform(new GlideCircleTransform(context))
                .placeholder(R.drawable.img_loading_circle)
                .error(R.mipmap.img_error_circle)
                .crossFade()
                .into(container);
        setScaleFactor(container, scaleFactor);
    }

    public void setScaleFactor(ImageView container, float scaleFactor){
        ViewGroup.LayoutParams layoutParamsl = container.getLayoutParams();
        layoutParamsl.width = CommonConfig.screenW;
        //scaleFactor（图片高/图片宽）
        layoutParamsl.height = (int)(CommonConfig.screenW * scaleFactor);
        container.setLayoutParams(layoutParamsl);
    }

    //监听图片是否加载完成
    public interface GlideLoadingCallback {
        void onLoadingOverCallBack();
    }

}
