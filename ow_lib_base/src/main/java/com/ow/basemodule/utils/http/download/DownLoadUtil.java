package com.ow.basemodule.utils.http.download;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Patterns;

import androidx.core.content.FileProvider;

import com.ow.basemodule.R;
import com.ow.basemodule.constant.Constants;
import com.ow.basemodule.utils.NotificationUtil;
import com.ow.basemodule.utils.http.download.callback.OnDownloadFactoryListener;
import com.ow.basemodule.utils.http.download.callback.OnDownloadListener;
import com.ow.framework.utils.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * author:wyb
 * 下载工具类
 */
public class DownLoadUtil {
    private int oldProgress;
    private WeakReference<Activity> mActivity;
    private NotificationUtil notificationUtil;

    public DownLoadUtil(Activity activity) {
        super();
        mActivity = new WeakReference<>(activity);
        notificationUtil = new NotificationUtil(mActivity.get());
    }

    //下载图片，文件等
    public void download(String downloadUrl, String fileName, OnDownloadListener onDownloadListener) {
        if (matcherUrl(downloadUrl)) {
            String filePath = Constants.BASE_PATH.getAbsolutePath() + "/" + Constants.APPLICATION_NAME;
            DownloadFactory.Companion.getInstance().download(downloadUrl, filePath, fileName + ".png", new OnDownloadFactoryListener() {

                @Override
                public void onDownloadSuccess(@NotNull String path) {
                    if (null != onDownloadListener) {
                        onDownloadListener.onDownloadSuccess(path);
                        onDownloadListener.onDownloadFinish();
                    }
                }

                @Override
                public void onDownloading(int progress) {
                }

                @Override
                public void onDownloadFailed(@NotNull Throwable e) {
                    if (null != onDownloadListener) {
                        onDownloadListener.onDownloadFailed(e);
                        onDownloadListener.onDownloadFinish();
                    }
                }

            });
        }
    }

    //检测url是否符合标准
    private boolean matcherUrl(String downloadUrl) {
        if (!Patterns.WEB_URL.matcher(downloadUrl).matches()) {
            showToast(mActivity.get().getString(R.string.download_err_txt));
            return false;
        }
        return true;
    }

    //统一提示方法
    private void showToast(String text) {
        ToastUtil.INSTANCE.mackToastSHORT(text, mActivity.get());
    }

    //获取安装跳转的行为
    private Intent getSetupApk(String apkFilePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            File file = new File(apkFilePath);
            Uri contentUri = FileProvider.getUriForFile(mActivity.get(), Constants.APPLICATION_ID + ".fileProvider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.parse("file://" + apkFilePath), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

}
