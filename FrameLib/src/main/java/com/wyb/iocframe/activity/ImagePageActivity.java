package com.wyb.iocframe.activity;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.wyb.iocframe.R;
import com.wyb.iocframe.adapter.ExtendedViewPageAdapter;
import com.wyb.iocframe.base.BaseTitleActivity;
import com.wyb.iocframe.util.MediaScanner;
import com.wyb.iocframe.util.ToastUtil;
import com.wyb.iocframe.view.extendedviewpager.ExtendedViewPager;
import com.wyb.iocframe.view.extendedviewpager.TouchImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * public String img_path[]
 * Intent intent=new Intent(this, ImagePageActivity.class);
		intent.putExtra("IMAGE_PAGE", 1);
		intent.putExtra("IMAGE_SIZE", img_path.length);
		Bundle bundle = new Bundle();
        bundle.putSerializable("IMAGE_LIST", (Serializable) img_path);
        intent.putExtras(bundle);
        startActivity(intent);
 * @author wyb
 *
 */
public class ImagePageActivity extends BaseTitleActivity {
	// 切换图片的viewpage
	private ExtendedViewPager imagePagePic;
	// 图片集合
	public List<TouchImageView> mTouchList = new ArrayList<>();
	// 切换图片显示的页数
	private TextView imagePageTxt;
	//当前页数以和总页数
	private int imagePage = 0, imageSize = 0;
	// 图片地址集合
	public String imageList[];
	// 图片适配器
	private ExtendedViewPageAdapter imageAdapter;
	// 需要保存的图片
	private Bitmap bitmap = null;
	public static final String IMAGE_PAGE = "imagePage"; //页数
	public static final String IMAGE_SIZE = "imageSize"; //总页数
	public static final String IMAGE_LIST = "imageList"; //路径集合
	
	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_page);
		imagePage = getIntent().getIntExtra(IMAGE_PAGE, 0);
		imageSize = getIntent().getIntExtra(IMAGE_SIZE, 0);
		imageList = (String[]) getIntent().getSerializableExtra(IMAGE_LIST);
		//直接传图片可省流量，但部分点击事件不好处理
		for (int i = 0; i < imageList.length; i++) {
			TouchImageView img = new TouchImageView(this);
			mGlide.display(img, imageList[i]);
			mTouchList.add(img);
		}
		initViews();
		initData();
	}
	
	public void initViews(){
		setTitle("图片轮播");
		//保存到本地sd卡
		mainRightText.setText("保存");
		mainRightText.setVisibility(View.VISIBLE);
		mainRightText.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				ToastUtil.mackToastSHORT("正在保存图片,请稍后...", getApplicationContext());
				//开启一个异步线程
				new Thread(runnable).start();
			}
		});
		imagePagePic = (ExtendedViewPager) findViewById(R.id.imagePagePic);
		imagePageTxt = (TextView) findViewById(R.id.imagePageTxt);
	}

	@SuppressWarnings("deprecation")
	public void initData(){
		imagePageTxt.setText((imagePage+1)+"/" + imageSize);
		imageAdapter = new ExtendedViewPageAdapter(mTouchList);
		imagePagePic.setAdapter(imageAdapter);
		imagePagePic.setCurrentItem(imagePage);
		imagePagePic.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			public void onPageSelected(int arg0) {
				imagePageTxt.setText((arg0+1)+"/"+imageSize);
			}
			
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			
			public void onPageScrollStateChanged(int arg0) {}
		});
	}

	Runnable runnable = new Runnable(){
	    public void run() {
	        try {
				Message msg = new Message();
				try {
					bitmap = Glide.with(getApplicationContext()).load(imageList[imagePage]).asBitmap()
							.into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}	
	    }
	};
	
	Handler handler = new Handler(){
		
	    public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        if (bitmap != null) {
				// 在这里执行图片保存方法  
				saveScan();
			}
	    }
	};
	
	/** 
     * 保存后用MediaScanner扫描，通用的方法 
     * @author WYB
     */  
    private void saveScan(){  
        String filePath = saveImg(true);  
        MediaScanner mediaScanner = new MediaScanner(this);
        String[] filePaths = new String[]{filePath};  
        String[] mimeTypes = new String[]{MimeTypeMap.getSingleton().getMimeTypeFromExtension("png")};
        mediaScanner.scanFiles(filePaths, mimeTypes);  
//        Toast.makeText(this, "保存成功：" + filePath, Toast.LENGTH_LONG).show();
		showToast("图片保存成功");
    }
	
    /** 
     * 保存图片到SD卡 
     * @author WYB
     * @param isInsertGallery 是否保存到图库 
     * @return 
     */  
    private String saveImg(boolean isInsertGallery){  
    	//图库下建立一个wyb的文件夹放置下载的图片
        File myappDir = new File(Environment.getExternalStorageDirectory(), "WYB");
        if(myappDir.exists() && myappDir.isFile()) {  
            myappDir.delete();  
        }  
        if (!myappDir.exists()) {  
            myappDir.mkdir();  
        }  
        String fileName = System.currentTimeMillis() + ".png";  
        File file = new File(myappDir, fileName);  
        if(file.exists()) {  
            file.delete();  
        }  
        try {  
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();  
            fos.close();  
        } catch (FileNotFoundException e) {
            e.printStackTrace();  
        } catch (IOException e) {
            e.printStackTrace();  
        }  
        if(isInsertGallery) {  
            try {  
                MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {  
                e.printStackTrace();  
            }  
        }  
        return file.getAbsolutePath();  
    }  
    
    /**
     *  销毁bitmap做优化
     */
    protected void onDestroy() {  
        super.onDestroy();  
        if(bitmap != null) {  
            bitmap.recycle();  
            bitmap = null;  
        }
		if (null != handler) {
			handler.removeCallbacksAndMessages(null);
			handler = null;
		}
	}

}
