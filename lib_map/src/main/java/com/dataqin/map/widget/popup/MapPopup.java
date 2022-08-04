package com.dataqin.map.widget.popup;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.amap.api.maps.model.LatLng;
import com.dataqin.base.utils.ToastUtil;
import com.dataqin.common.base.BasePopupWindow;
import com.dataqin.common.utils.file.FileUtil;
import com.dataqin.map.R;
import com.dataqin.map.databinding.ViewPopupMapBinding;
import com.dataqin.map.utils.CoordinateTransUtil;

import org.jetbrains.annotations.NotNull;

/**
 * Created by wangyanbin
 * 地图
 */
public class MapPopup extends BasePopupWindow<ViewPopupMapBinding> implements View.OnClickListener {
    private String address;
    private LatLng latlng;

    public MapPopup(@NotNull Activity activity) {
        super(activity,true);
        initialize();
    }

    @Override
    protected void initialize() {
        super.initialize();
        binding.tvBmap.setOnClickListener(this);
        binding.tvAmap.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
        binding.rlContainer.setOnClickListener(this);
    }

    public void setData(LatLng latlng, String address) {
        this.latlng = latlng;
        this.address = address;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (v.getId() == R.id.rl_container || v.getId() == R.id.tv_cancel) {
            dismiss();
        } else if (v.getId() == R.id.tv_bmap) {
            if (FileUtil.isAvailable(getActivity(), "com.baidu.BaiduMap")) {
                try {
                    //使用百度地图之前需要对经纬度做转换
                    double[] LngLat = CoordinateTransUtil.transformGCJ02ToBD09(latlng.longitude, latlng.latitude);
                    intent = Intent.parseUri("intent://map/direction?destination=latlng:" + LngLat[1] + "," + LngLat[0] + "|name:" + address + "&mode=driving&&src=appname#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end", 0);
                    getActivity().startActivity(intent);//启动调用
                } catch (Exception ignored) {
                }
            } else {
                ToastUtil.mackToastSHORT("未安装百度地图", getActivity());
            }
            dismiss();
        } else if (v.getId() == R.id.tv_amap) {
            if (FileUtil.isAvailable(getActivity(), "com.autonavi.minimap")) {
                intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                Uri uri = Uri.parse("amapuri://route/plan/?sid=BGVIS1&slat=&slon=&sname=&did=&dlat=" + latlng.latitude + "&dlon=" + latlng.longitude + "&dname=" + address + "&dev=0&t=0");
                intent.setData(uri);
                //启动该页面即可
                getActivity().startActivity(intent);
            } else {
                ToastUtil.mackToastSHORT("未安装高德地图", getActivity());
            }
            dismiss();
        }
    }
}
