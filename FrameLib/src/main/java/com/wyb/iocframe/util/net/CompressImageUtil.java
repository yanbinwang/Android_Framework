package com.wyb.iocframe.util.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;


import com.wyb.iocframe.MyApplication;
import com.wyb.iocframe.util.log.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author fubing 图片压缩工具类
 */
public class CompressImageUtil {

	public static ByteArrayOutputStream compressImg(Bitmap image) {
		image = compressImgBysize(image);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		while (baos.toByteArray().length / 1024 > 512) {
			baos.reset();
			options -= 10;

			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
		}
		LogUtil.e("length", baos.toByteArray().length + "");

		return baos;
	}

	public static ByteArrayOutputStream compressImg(Bitmap image, int length) {
		image = compressImgBysize(image);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		while (baos.toByteArray().length / 1024 > length) {
			baos.reset();
			options -= 10;
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
		}
		LogUtil.e("length", baos.toByteArray().length + "");

		return baos;
	}

	public static void clearSdcardCache() {
		File cacheDir = new File("/mnt/sdcard/tempImg");

		File[] files = cacheDir.listFiles();
		if (files == null) {
			return;
		}

		for (File file : files) {
			if ("image.jpg".equals(file.getName())) {
				file.delete();
				break;
			}
		}
	}

	public static Bitmap compressImgBysize(Bitmap image) {
		float size = 1f;
		int w = image.getWidth();
		int h = image.getHeight();
		Matrix m = new Matrix();
		if (w > 720) {
			size = 720f / w;
			LogUtil.e("w", image.getWidth() + "");
			LogUtil.e("h", image.getHeight() + "");
		} else if (h > 1280) {
			size = 1280f / h;
			LogUtil.e("w", image.getWidth() + "");
			LogUtil.e("h", image.getHeight() + "");
		}
		m.postScale(size, size);
		image = Bitmap.createBitmap(image, 0, 0, w, h, m, true);
		return image;
	}

	public static File scal(File mfile) {
		File outputFile = mfile;
		long fileSize = outputFile.length();
		float scaleSize = 1;
		LogUtil.e("fileSize_old", mfile.length() / 1024 + "kb");
		final long fileMaxSize = 512 * 1024;
		if (fileSize >= fileMaxSize) {
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				Bitmap bitmap = BitmapFactory.decodeFile(mfile.getPath(),
						options);
				int w = options.outWidth;
				int h = options.outHeight;
				if (w > 720) {
					scaleSize = w / 480;
				} else if (h > 1280) {
					scaleSize = w / 1080;
				}
				options.inJustDecodeBounds = false;
				options.inSampleSize = (int) (scaleSize + 0.5);
				// byte[] data = getBytes(mfile.)
				// bitmap = BitmapFactory.decodeFile(mfile.getPath(), options);
				bitmap = BitmapFactory.decodeFile(mfile.getPath(), options);
				bitmap = compressImgBysize(bitmap);

				File ftemp = new File(MyApplication.getInstance()
						.getApplicationContext().getExternalCacheDir(),
						System.currentTimeMillis() + "img.jpg");
				FileOutputStream fOut = null;
				try {
					fOut = new FileOutputStream(ftemp);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

				fOut.flush();
				fOut.close();
				bitmap.recycle();
				LogUtil.e("fileSize", ftemp.length() / 1024 + "kb");
				return ftemp;
			} catch (IOException e) {
				e.printStackTrace();
				return outputFile;
			}

		} else {
			LogUtil.e("fileSize", outputFile.length() / 1024 + "kb");
			return outputFile;
		}

	}

	public static byte[] getBytes(InputStream is) throws IOException {
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024]; // 用数据装
		int len = -1;
		while ((len = is.read(buffer)) != -1) {
			outstream.write(buffer, 0, len);
		}
		outstream.close();
		// 关闭流一定要记得。
		return outstream.toByteArray();
	}
}
