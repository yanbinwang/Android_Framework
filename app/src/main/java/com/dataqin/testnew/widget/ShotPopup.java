package com.dataqin.testnew.widget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dataqin.common.base.BasePopupWindow;
import com.dataqin.common.utils.file.FileUtil;
import com.dataqin.media.utils.MediaFileUtil;
import com.dataqin.testnew.databinding.ViewPopupShotBinding;

import org.jetbrains.annotations.NotNull;

/**
 * Created by wangyanbin
 */
public class ShotPopup extends BasePopupWindow<ViewPopupShotBinding> {
    private String filePath;

    public ShotPopup(@NotNull Activity activity) {
        super(activity, true);
        initialize();
    }

    @Override
    protected void initialize() {
        super.initialize();
        binding.ivShit.setOnClickListener(v -> {
            FileUtil.deleteDir(filePath);
            dismiss();
        });
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        MediaFileUtil.saveBitmapToSd(bitmap, filePath, 100);
        binding.ivShit.setImageBitmap(bitmap);
    }

}
