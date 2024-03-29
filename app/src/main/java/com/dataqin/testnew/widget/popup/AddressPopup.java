package com.dataqin.testnew.widget.popup;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.aigestudio.wheelpicker.WheelPicker;
import com.dataqin.base.utils.DisplayUtilKt;
import com.dataqin.base.utils.ToastUtil;
import com.dataqin.common.base.BasePopupWindow;
import com.dataqin.testnew.R;
import com.dataqin.testnew.databinding.ViewPopupAddressBinding;
import com.dataqin.testnew.model.AddressModel;
import com.dataqin.testnew.model.ProperModel;
import com.dataqin.testnew.model.ProvinceModel;
import com.dataqin.testnew.widget.popup.callback.OnAddressPopupListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyanbin
 * 省市区地址选择
 */
public class AddressPopup extends BasePopupWindow<ViewPopupAddressBinding> implements View.OnClickListener {
    private List<AddressModel> addressList;//省份集合
    private List<ProvinceModel> provinceList = new ArrayList<>();//市集合
    private List<ProperModel> properList = new ArrayList<>();//区集合
    private OnAddressPopupListener onAddressPopupListener;

    public AddressPopup(@NotNull Activity activity) {
        super(activity, true);
        initialize();
    }

    // <editor-fold defaultstate="collapsed" desc="基类方法">
    @Override
    protected void initialize() {
        super.initialize();
        initPicker(binding.wpCity);
        initPicker(binding.wpProvince);
        initPicker(binding.wpProper);

        binding.wpCity.setOnWheelChangeListener(new WheelPicker.OnWheelChangeListener() {
            @Override
            public void onWheelScrolled(int offset) {
            }

            @Override
            public void onWheelSelected(int position) {
                //得到滑动的市的集合内容默认选择第一个
                getProvinceList(position);
                //得到滑动的区的集合内容默认选择第一个
                getProperList(0);
            }

            @Override
            public void onWheelScrollStateChanged(int state) {
            }
        });
        binding.wpProvince.setOnWheelChangeListener(new WheelPicker.OnWheelChangeListener() {
            @Override
            public void onWheelScrolled(int offset) {
            }

            @Override
            public void onWheelSelected(int position) {
                //得到滑动的区的集合内容默认选择第一个
                getProperList(position);
            }

            @Override
            public void onWheelScrollStateChanged(int state) {
            }
        });
        binding.tvCancel.setOnClickListener(this);
        binding.tvSure.setOnClickListener(this);
        //设置默认值
        getAddressData();
    }

    private void initPicker(WheelPicker picker) {
        picker.setAtmospheric(true);//阴影效果
        picker.setCurved(true);//开启类似IOS的滚筒效果
        picker.setItemTextSize(DisplayUtilKt.dip2px(getActivity(), 15));//每个item的字体大小
        picker.setIndicatorColor(ContextCompat.getColor(getActivity(), R.color.black));//年月日的颜色
        picker.setItemSpace(DisplayUtilKt.dip2px(getActivity(), 20));//每个item之间的间距
    }

    //获取本地省市区文件
    private void getAddressData() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getActivity().getAssets().open("pcas-code.json")));
            String str;
            while (null != (str = bufferedReader.readLine())) {
                stringBuilder.append(str);
            }
        } catch (IOException e) {
            stringBuilder.delete(0, stringBuilder.length());
        } finally {
            String result = stringBuilder.toString();
            if (!TextUtils.isEmpty(result)) {
                addressList = new Gson().fromJson(result, new TypeToken<List<AddressModel>>() {}.getType());
                //设置默认值
                getCityList();
                getProvinceList(0);
                getProperList(0);
            }
        }
    }

    //得到所有省份的字符串集合
    public void getCityList() {
        if (!addressList.isEmpty()) {
            List<String> cityStrList = new ArrayList<>();
            for (AddressModel model : addressList) {
                cityStrList.add(model.getName());
            }
            binding.wpCity.setData(cityStrList);
            binding.wpCity.setSelectedItemPosition(0);
        }
    }

    //查询要选择的城市下标（如果查询不到默认返回0，既第一条）
    private int getCurrentCityIndex(String code) {
        int index = 0;
        for (int i = 0; i < addressList.size(); i++) {
            if (code.equals(addressList.get(i).getCode())) {
                index = i;
                break;
            }
        }
        return index;
    }

    //得到选择的市集合
    private void getProvinceList(int position) {
        List<String> provinceStrList = new ArrayList<>();
        if (!addressList.isEmpty() && addressList.size() > position) {
            provinceList = addressList.get(position).getChilds();
            for (ProvinceModel model : provinceList) {
                provinceStrList.add(model.getName());
            }
        }
        binding.wpProvince.setData(provinceStrList);
        binding.wpProvince.setSelectedItemPosition(0);
    }

    //查询要选择的市下标（如果查询不到默认返回0，既第一条）
    private int getCurrentProvinceIndex(String code) {
        int index = 0;
        for (int i = 0; i < provinceList.size(); i++) {
            if (code.equals(provinceList.get(i).getCode())) {
                index = i;
                break;
            }
        }
        return index;
    }

    //得到选择的区集合
    private void getProperList(int position) {
        List<String> properStrList = new ArrayList<>();
        if (!provinceList.isEmpty() && provinceList.size() > position) {
            properList = provinceList.get(position).getChilds();
            for (ProperModel model : properList) {
                properStrList.add(model.getName());
            }
        }
        binding.wpProper.setData(properStrList);
        binding.wpProper.setSelectedItemPosition(0);
    }

    //查询要选择的区下标（如果查询不到默认返回0，既第一条）
    private int getCurrentProperIndex(String code) {
        int index = 0;
        for (int i = 0; i < properList.size(); i++) {
            if (code.equals(properList.get(i).getCode())) {
                index = i;
                break;
            }
        }
        return index;
    }
    // </editor-fold>

    public boolean showPopup(View view) {
        return showPopup(view, null);
    }

    //传入全部的区编码回显
    public boolean showPopup(View view, String fullCodes) {
        if (addressList.isEmpty()) {
            ToastUtil.mackToastSHORT("正在加载,请稍后...", getActivity());
            getAddressData();
            return false;
        }

        if (TextUtils.isEmpty(fullCodes)) {
            binding.wpCity.setSelectedItemPosition(0);
            binding.wpProvince.setSelectedItemPosition(0);
            binding.wpProper.setSelectedItemPosition(0);
        } else {
            String[] fullCode = fullCodes.split(",");
            binding.wpCity.setSelectedItemPosition(getCurrentCityIndex(fullCode[0]));
            //得到滑动的市的集合内容默认选择第一个
            getProvinceList(getCurrentCityIndex(fullCode[0]));
            //得到滑动的区的集合内容默认选择第一个
            getProperList(0);
            binding.wpProvince.setSelectedItemPosition(getCurrentProvinceIndex(fullCode[1]));
            binding.wpProper.setSelectedItemPosition(getCurrentProperIndex(fullCode[2]));
        }

        showAtLocation(view, Gravity.BOTTOM, 0, 0);
        return true;
    }

    public void setOnAddressPopupListener(OnAddressPopupListener onAddressPopupListener) {
        this.onAddressPopupListener = onAddressPopupListener;
    }

    @Override
    public void onClick(View v) {
        //获取省市区字段做返回显示
        if (v.getId() == R.id.tv_sure) {
            //省份编码，市编码，区编码
            String fullCode = addressList.get(binding.wpCity.getCurrentItemPosition()).getCode() + "," + provinceList.get(binding.wpProvince.getCurrentItemPosition()).getCode() + "," + properList.get(binding.wpProper.getCurrentItemPosition()).getCode();
            //310003
            String areaCode = properList.get(binding.wpProper.getCurrentItemPosition()).getCode();
            //浙江省，杭州市，下城区
            String fullName = addressList.get(binding.wpCity.getCurrentItemPosition()).getName() + "," + provinceList.get(binding.wpProvince.getCurrentItemPosition()).getName() + "," + properList.get(binding.wpProper.getCurrentItemPosition()).getName();
            if (null != onAddressPopupListener) {
                onAddressPopupListener.onCurrent(fullCode, areaCode, fullName);
            }
            dismiss();
        } else if (v.getId() == R.id.tv_cancel) {
            dismiss();
        }
    }
}
