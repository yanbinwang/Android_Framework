package com.dataqin.testnew.widget.popup;

import android.app.Activity;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.dataqin.base.utils.DateUtil;
import com.dataqin.base.utils.DisplayUtil;
import com.dataqin.common.base.BasePopupWindow;
import com.dataqin.testnew.R;
import com.dataqin.testnew.databinding.ViewPopupDateBinding;
import com.dataqin.testnew.widget.popup.callback.OnDateListener;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wangyanbin
 * 日期选择类
 */
public class DatePopup extends BasePopupWindow<ViewPopupDateBinding> implements View.OnClickListener {
    private OnDateListener onDateListener;

    public DatePopup(@NotNull Activity activity) {
        super(activity, true);
        initialize();
    }

    @Override
    protected void initialize() {
        super.initialize();
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        binding.wdpChannel.setYearStart(1900);
        binding.wdpChannel.setYearEnd(2100);
        binding.wdpChannel.setSelectedYear(calendar.get(Calendar.YEAR));
        binding.wdpChannel.setMonth(calendar.get(Calendar.MONTH) + 1);
        binding.wdpChannel.setSelectedDay(calendar.get(Calendar.DAY_OF_MONTH));
        binding.wdpChannel.setItemSpace(DisplayUtil.dip2px(getActivity(), 30));//每个item之间的间距
        binding.wdpChannel.setAtmospheric(true);//阴影效果
        binding.wdpChannel.setCurved(true);//开启类似IOS的滚筒效果
        binding.wdpChannel.setItemTextSize(DisplayUtil.dip2px(getActivity(), 20));//每个item的字体大小
        binding.wdpChannel.setIndicatorColor(ContextCompat.getColor(getActivity(), R.color.black));//年月日的颜色

        binding.tvSure.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    //传入一组yyyy-MM-dd HH:mm:ss的日期数据
    public void setDefault(String date) {
        Calendar defaultCal = Calendar.getInstance();
        defaultCal.setTime(new Date(DateUtil.getDateTime(DateUtil.EN_YMDHMS_FORMAT, date)));
        binding.wdpChannel.setSelectedYear(defaultCal.get(Calendar.YEAR));
        binding.wdpChannel.setMonth(defaultCal.get(Calendar.MONTH) + 1);
        binding.wdpChannel.setSelectedDay(defaultCal.get(Calendar.DAY_OF_MONTH));
    }

    public void setOnDateListener(OnDateListener onDateListener) {
        this.onDateListener = onDateListener;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_sure) {
            if (null != onDateListener) {
                onDateListener.onCurrent(DateUtil.getDateTimeStr(DateUtil.EN_YMDHMS_FORMAT, binding.wdpChannel.getCurrentDate()));
            }
            dismiss();
        } else if (v.getId() == R.id.tv_cancel) {
            dismiss();
        }
    }

}
