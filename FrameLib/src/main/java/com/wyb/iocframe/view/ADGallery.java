package com.wyb.iocframe.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;

/**
 * Camera与Matrix的比较
 * Camera 		rotate() 指定某一维度上旋转指定的角度
 * Matrix		rotate() 顺时针旋转指定的角度，与Camera指定Z轴旋转效果相同，但方向相反
 * 
 * Camera		translate() 根据某一维度上的视点的位移实现图像的缩放
 * Matrix		scale()	指定缩放比例
 * 
 * Camera不支持倾斜操作，Matrix可以直接实现倾斜操作
 */

/**
 * onSizeChanged 	第一次执行，计算大小
 * getChildStaticTransformation	每次滑动执行，子视图转变写在这里
 * getChildDrawingOrder		改变子视图的顺序(层叠效果)
 * 
 * 里面调节的量，挺多的，这里可实现层叠和3D效果，开关分别是isZoom(配合间隔),isRotate
 */
@SuppressWarnings("deprecation")
public class ADGallery extends Gallery{
	/** 是否查看大量的log*/
	private final static boolean isDebug = false;
	/**
	 * z轴变化规律，严格满足  scale = H / (H + h); 576时是原图 1/2  -288时，原图的2倍
	 * mZoomUnit 越大，缩小的越厉害,具体使用可调节的量
	 */
	private int mZoomUnit = 150;
	public static boolean isZoom = false;
	/** mMaxRotationAngle是图片绕y轴最大旋转角度,也就是屏幕最边上那两张图片的旋转角度*/
	private int mMaxRotationAngle = 150;
	/** 视图开启3D效果开关（缩放）*/
	private boolean isRotate = false;  
	/**mCamera是用来做类3D效果处理,比如z轴方向上的平移,绕y轴的旋转  x 右 y 上 z 里辅助变量*/
	private Camera mCamera;
	/**gallery左侧到中心的距离 px辅助变量*/
	private int mCoveflowCenter;

	public ADGallery(Context context) {
		this(context, null);
	}

	public ADGallery(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}

	public ADGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mCamera = new Camera();
		this.setStaticTransformationsEnabled(true);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		mCoveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * 滑动过程中一直在执行，系统主动调用
	 */
	protected boolean getChildStaticTransformation(View child, Transformation t){
		final int childCenter = getCenterOfView(child);
		if (isDebug) {
			Log.i("AD - ADGallery", "getChildStaticTransformation getCenterOfView = " +  childCenter);
		}
		final int childWidth = child.getWidth();
		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);

		if (childCenter == mCoveflowCenter){ // 中心位置
			transformImageBitmap((View) child, t, 0, 0);
		} else{
			// 从左到右分别是  5 4 3 2 1 0 -1 -2 -3 -4 -5 等规律 * mZoomUnit
			float position = (mCoveflowCenter - childCenter) * 1.0f / childWidth;
			if (isDebug) {
				Log.i("F21 - F21Gallery", "position = " +  position);
			}

			// 处理缩小比例大小 	这个值的结果收到 图片间隔的影响较大，具体还需要调节
			int zoom = 0;
			zoom = (int) Math.abs(position * mZoomUnit); // 降低间隔的影响

			// 处理角度
			int rotationAngle = 0;
			rotationAngle = (int) (mMaxRotationAngle * position * childWidth / (mCoveflowCenter * 2));
			if (Math.abs(rotationAngle) > mMaxRotationAngle){  // 限定边界
				rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle : mMaxRotationAngle;
			}

			transformImageBitmap((View) child, t, zoom, rotationAngle);
		}

		return true;
	}

	/**
	 * 改变视图,x 右，y 上，z 里 为正方向
	 * @param child	子视图
	 * @param t	操作的结果
	 * @param rotationAngle	旋转角度（有正负）
	 */
	private void transformImageBitmap(View child, Transformation t, int zoom,int rotationAngle){
		mCamera.save();
		final Matrix imageMatrix = t.getMatrix();
		final int imageWidth = child.getWidth();  // 子视图宽度
		final int imageHeight = child.getHeight(); // 子视图高度
		final int rotation = (int) Math.abs(rotationAngle);

		if (isZoom) {
			mCamera.translate(0.0f,0.0f, zoom);  // 满足  scale = H / (H + h)
		}
		
		if (isRotate) {
			mCamera.rotateY(rotationAngle);
		}

		if (isDebug) {
			Log.i("AD - ADGallery", "zoom = " +  zoom);
			Log.i("AD - ADGallery", "rotationAngle = " +  rotationAngle);
		}

		mCamera.getMatrix(imageMatrix);

		imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));  // 以这两个确定以子视图中心为中心操作
		imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
		mCamera.restore();
	}

	/**
	 * 重载视图显示顺序
	 * 让左到中间显示，再右到中间显示，起到层叠效果
	 */
	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		if (i < childCount / 2) {  
			return i;  
		}else {
			return childCount - i - 1 + childCount / 2;  
		}
		//	原本
		//	return super.getChildDrawingOrder(childCount, i);
	}

	/**
	 * 返回 false ,就取消了 gallery的滚动惯性
	 * @注OnFling直接返回false也能实现类似效果，但那样需要滑动很大距离，图片才会切换，用户体验不好
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
		int keyCode;
		if (e1.getX() < e2.getX()) {	// 左滑
			keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
		}else {
			keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		return onKeyDown(keyCode, null);
		//		return true;
		//		return false;
		// 以上三个效果差不多
		// 这个就有滚动惯性	return super.onFling(e1, e2, velocityX, velocityY);
	}



	/**
	 * @return	gallery中心到屏幕左侧的距离   px
	 */
	private int getCenterOfCoverflow(){
		int distant = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
		Log.i("AD - ADGallery", "getCenterOfCoverflow distant = " +  distant);
		return distant;
	}

	/**
	 * @param view
	 * @return	子视图中心到屏幕最左侧的距离
	 */
	private static int getCenterOfView(View view){
		int distant = view.getLeft() + view.getWidth() / 2;
		return distant;
	}

	//	/**
	//	 * @param scale	需要计算的比例的100倍，即原来大小为 100
	//	 * @return	z轴对应移动的距离
	//	 */
	//	private float ScaleToZoomOut(int scale){
	//		return 576.0f * (100 - scale) / scale;
	//	}
}