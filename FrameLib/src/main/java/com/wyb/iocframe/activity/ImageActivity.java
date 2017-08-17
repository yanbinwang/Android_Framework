package com.wyb.iocframe.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wyb.iocframe.R;
import com.wyb.iocframe.base.BaseTitleActivity;
import com.wyb.iocframe.view.SmoothCheckBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 2017/3/20.
 * 选择图片页
 * private static final int REQUEST_PHOTO = 10000;
 * Intent intent = new Intent(getApplicationContext(),ImageActivity.class);
 *intent.putExtra("IMAGE_NUM",6);
 *intent.putExtra("IMAGE_LINE",3);
 *startActivityForResult(intent,REQUEST_PHOTO);
 *
 * private ArrayList<String> path = new ArrayList<>();
 *  if(REQUEST_PHOTO == requestCode) {
 *path = data.getStringArrayListExtra("IMAGE_RESULT");
 *
 *String num = null;
 *for (int i = 0; i < path.size(); i++) {
 *num = num + path.get(i) + ",";
 *}
 *
 *Toast.makeText(getApplicationContext(),num,Toast.LENGTH_SHORT).show();
 *}
 */
public class ImageActivity extends BaseTitleActivity {
    private static final int REQUEST_PHOTO = 10000;
    //拿多少图片，每行显示多少图片
    private int getImgNum,imgLineNum;
    //底部内容适配
    private GridView mGridView;

    //图片长宽
    private int image_w,image_h;
    //存储图片地址
    private List<ImageEntity> mData = new ArrayList<>();
    //传回去的集合
    private ArrayList<String> path = new ArrayList<>();

    public static final String IMAGE_NUM = "imageNum"; //能选择的数量
    public static final String IMAGE_LINE  = "imageLine"; //一行显示的数量

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        getImgNum = getIntent().getIntExtra(IMAGE_NUM ,0);
        imgLineNum = getIntent().getIntExtra(IMAGE_LINE,0);
        initView();
    }

    private void initView(){
        setTitle("相册");
        WindowManager wm = getWindowManager();
        image_w = wm.getDefaultDisplay().getWidth() / imgLineNum;
        image_h = image_w;

        mGridView = (GridView) findViewById(R.id.mGridView);
        mGridView.setNumColumns(imgLineNum);

        mData = getImgPathList();
        final ImageAdapter mAdapter = new ImageAdapter();
        mGridView.setAdapter(mAdapter);

        if(getImgNum == 1){
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    path.add(mData.get(position).img_Path);
                    Intent intent = new Intent(ImageActivity.this, getIntent().getClass());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("IMAGE_RESULT", path);
                    intent.putExtras(bundle);
                    setResult(REQUEST_PHOTO,intent);
                    finish();
                }
            });
        }else{
            mainRightText.setText("确定");
            mainRightText.setVisibility(View.VISIBLE);
            mainRightText.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    for (int i = 0; i < mData.size(); i++) {
                        if(mData.get(i).isChoose){
                            path.add(mData.get(i).img_Path);
                        }
                    }
                    Intent intent = new Intent(ImageActivity.this, getIntent().getClass());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("IMAGE_RESULT", path);
                    intent.putExtras(bundle);
                    setResult(REQUEST_PHOTO,intent);
                    finish();
                }
            });
        }

        mAdapter.mClickListen = new View.OnClickListener() {

            public void onClick(View v) {
                //得到选中的下标
                int index = Integer.valueOf(v.getTag().toString());
                //得到选择的控件
                SmoothCheckBox ck = (SmoothCheckBox) v;
                //如果控件处于选中状态则复位
                if(ck.isChecked()){
                    ck.setChecked(false,true);
                    mData.get(index).isChoose = false;
                }else{
                    //求集合中总的选择数
                    int num = 0;
                    for (int i = 0; i < mData.size(); i++) {
                        if(mData.get(i).isChoose){
                            num ++ ;
                        }
                    }
                    if(num ++ == getImgNum){
                        showToast("只能选择" + getImgNum + "张图");
                    }else{
                        ck.setChecked(true,true);
                        mData.get(index).isChoose = true;
                    }
                }
            }
        };
    }

    //获取图片地址列表
    private List<ImageEntity> getImgPathList() {
        List<ImageEntity> list = new ArrayList<>();
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { "_id", "_data" }, null, null, null);
        while (cursor.moveToNext()) {
            ImageEntity img = new ImageEntity();
            img.img_Path = cursor.getString(1);
            list.add(img);// 将图片路径添加到list中
        }
        cursor.close();
        return list;
    }


    public class ImageAdapter extends BaseAdapter {
        public View.OnClickListener mClickListen;

        public int getCount() {
            return mData.size();
        }

        public ImageEntity getItem(int position) {
            if(mData == null || mData.size() == 0) {
                return null;
            }
            return mData.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;
            if (view == null) {
                holder = new ViewHolder();
                view = getLayoutInflater().inflate(R.layout.item_photo, parent, false);
                holder.imageView = (ImageView) view.findViewById(R.id.photo_image);
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
                rl.width = image_w;
                rl.height = image_h;
                holder.imageView.setLayoutParams(rl);
                holder.clickView = (SmoothCheckBox) view.findViewById(R.id.photo_select);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            if(getImgNum > 1){
                holder.clickView.setVisibility(View.VISIBLE);
                holder.clickView.setTag(position);
                holder.clickView.setOnClickListener(mClickListen);
            }else{
                holder.clickView.setVisibility(View.GONE);
            }
            ImageEntity img = getItem(position);
            if(img.isChoose){
                holder.clickView.setChecked(true);
            }else{
                holder.clickView.setChecked(false);
            }
            mGlide.display(holder.imageView,img.img_Path);
            return view;
        }

        class ViewHolder {
            ImageView imageView;
            SmoothCheckBox clickView;
        }
    }

    public static class ImageEntity {
        public String img_Path;
        public boolean isChoose;
    }

}
