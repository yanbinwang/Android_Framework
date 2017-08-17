package com.example.testnew;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.wyb.iocframe.activity.WebviewActivity;
import com.wyb.iocframe.annotation.ViewInject;
import com.wyb.iocframe.base.BaseTitleActivity;
import com.wyb.iocframe.util.net.ConnectWebsiteUtil;
import com.wyb.iocframe.view.MyDialog;
import com.yanzhenjie.nohttp.RequestMethod;

import java.util.HashMap;

public class MainActivity extends BaseTitleActivity implements View.OnClickListener{
    @ViewInject(id = R.id.mBtn, click = "onClick")
    private Button mbtn;

    @ViewInject(id = R.id.mBtn2, click = "onClick")
    private Button mbtn2;

    @ViewInject(id = R.id.mBtn3, click = "onClick")
    private Button mBtn3;

    @ViewInject(id = R.id.mBtn4, click = "onClick")
    private Button mBtn4;

    @ViewInject(id = R.id.mBtn5, click = "onClick")
    private Button mBtn5;

    @ViewInject(id = R.id.mImg)
    private ImageView mImg;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("V4包框架");
        hideBack();
        mGlide.display(mImg, 0.8f, "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3241219306,1400876595&fm=23&gp=0.jpg");
    }

    private void postServer(){
        showDialog();
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("username", "123");
        paramsMap.put("password", "456");
        ConnectWebsiteUtil.getInstance().toCallWebsite(MainActivity.this, RequestMethod.POST, "http://qll.ckb.mobi/Test/test.html", null, paramsMap, new ConnectWebsiteUtil.NetworkCallback() {

            public void onSuccessCallBack(String result) {
                showToast(result);
                hideDialog();
            }

            public void onFailureCallBack(Exception e) {
                e.printStackTrace();
                hideDialog();
            }
        });
    }

    private void getServer(){
        showDialog();
        ConnectWebsiteUtil.getInstance().toCallWebsite(MainActivity.this,RequestMethod.GET, "http://www.qidian.com/", null, null, new ConnectWebsiteUtil.NetworkCallback() {

            public void onSuccessCallBack(String result) {
                showToast(result);
                hideDialog();
            }

            public void onFailureCallBack(Exception e) {
                e.printStackTrace();
                hideDialog();
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mBtn:

                break;
            case R.id.mBtn2:
                MyDialog.getDialogAndshow(MainActivity.this, new MyDialog.DialogCallBack() {

                    public void dialogSure() {

                    }

                    public void dialogCancle() {

                    }
                },"内容内容内容内容内容内容内容内容内容","确定","取消","标题");
                break;
            case R.id.mBtn3:
                Intent intent = new Intent(MainActivity.this, WebviewActivity.class);
                intent.putExtra("appTitle", "网页标题");
                intent.putExtra("intentUrl","http://www.qidian.com");
                startActivity(intent);
                break;
            case R.id.mBtn4:
                postServer();
                break;
            case R.id.mBtn5:
                getServer();
                break;
        }

    }

}
