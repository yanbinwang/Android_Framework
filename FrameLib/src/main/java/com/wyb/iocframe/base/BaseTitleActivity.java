package com.wyb.iocframe.base;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wyb.iocframe.R;

/**
 * Created by android on 2017/3/3.
 * 所有子类所继承的基类
 */
public class BaseTitleActivity extends BaseActivity{
    //将各个页面需要添加的内容添加到该布局当中
    private LinearLayout addMainContext;
    //页面标题,右侧文字
    protected TextView mainTitle, mainRightText;
    //左侧按钮
    private ImageView mainLeftBtn, mainRightBtn;

    //设置头部标题
    public void setTitle(CharSequence title) {
        if (mainTitle != null) {
            mainTitle.setText(title);
        }
    }

    //设置返回按钮
    protected void hideBack() {
        if (mainLeftBtn != null) {
            mainLeftBtn.setVisibility(View.GONE);
        }
    }

    //复写find到xml的方法，给每个find的xml加上刷新球控件
    public void setContentView(int layoutID) {
        super.setContentView(R.layout.activity_basic_title);
        initTitleView();
        addMainContext.addView(getLayoutInflater().inflate(layoutID, null));
        FinalActivity.initInjectedView(this);
    }

    //初始化公共布局的一些方法
    private void initTitleView(){
        //初始化标题的一些操作
        mainTitle = (TextView) findViewById(R.id.mainTitle);
        mainRightText = (TextView) findViewById(R.id.mainRightText);
        mainLeftBtn = (ImageView) findViewById(R.id.mainLeftBtn);
        mainLeftBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                finish();
            }
        });
        mainRightBtn = (ImageView) findViewById(R.id.mainRightBtn);
        addMainContext = (LinearLayout) findViewById(R.id.addMainContext);
    }

}
