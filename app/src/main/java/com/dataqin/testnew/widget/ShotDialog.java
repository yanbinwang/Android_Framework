package com.dataqin.testnew.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dataqin.common.base.BaseDialog;
import com.dataqin.common.utils.file.FileUtil;
import com.dataqin.media.utils.MediaFileUtil;
import com.dataqin.testnew.databinding.ViewDialogShotBinding;

/**
 * Created by wangyanbin
 */
public class ShotDialog extends BaseDialog<ViewDialogShotBinding> {
    private String filePath;

    public ShotDialog(Context context) {
        super(context);
        initialize();
    }

    @Override
    protected void initialize() {
        super.initialize();
        binding.ivShit.setOnClickListener(v -> {
            FileUtil.deleteDir(filePath);
            hide();
        });
    }

    public void show(String filePath) {
        setCancelable(false);
        if (!isShowing()) {
            this.filePath = filePath;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            MediaFileUtil.saveBitmapToSd(bitmap, filePath, 100);
            binding.ivShit.setImageBitmap(bitmap);
            show();
        }
    }

    public void hide() {
        if (isShowing()) {
            dismiss();
        }
    }

}
