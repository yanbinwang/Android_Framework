package com.dataqin.testnew.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.dataqin.common.base.binding.BaseAdapter
import com.dataqin.common.base.binding.BaseViewBindingHolder
import com.dataqin.media.utils.helper.GSYVideoHelper
import com.dataqin.testnew.databinding.*
import java.lang.ref.WeakReference

@SuppressLint("SetTextI18n", "NotifyDataSetChanged")
class FunctionAdapter(private var activity: WeakReference<Activity>, private var type: String) : BaseAdapter<Any>() {
    private val TYPE_HEADER = 1
    private val TYPE_BODY = 2
    private val TYPE_BOTTOM = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewBindingHolder {
        return when (viewType) {
            TYPE_HEADER -> onCreateViewBindingHolder(parent, ItemFunctionHeaderBinding::class.java)
            TYPE_BODY -> when (type) {
                "1" -> onCreateViewBindingHolder(parent, ItemFunctionShotBodyBinding::class.java)
                "2" -> onCreateViewBindingHolder(parent, ItemFunctionRecordBodyBinding::class.java)
                "3" -> onCreateViewBindingHolder(parent, ItemFunctionVideoBodyBinding::class.java)
                else -> onCreateViewBindingHolder(parent, ItemFunctionScreenBodyBinding::class.java)
            }
            else -> onCreateViewBindingHolder(parent, ItemFunctionBottomBinding::class.java)
        }
    }

    override fun convert(holder: BaseViewBindingHolder, item: Any?) {
        var binding = holder.getBinding<ViewBinding>()
        when (binding) {
            is ItemFunctionHeaderBinding -> {
                binding = holder.getBinding<ItemFunctionHeaderBinding>()
                when (type) {
                    "1" -> binding.tvIntroduce.text = "    简证APP将通知并获取手机位置权限和相机进行拍摄。拍照完成后系统将录像源文件加密存储至公有云服务器。通过SHA256算法或国密SM3算法计算数据摘要值并实时固定至区块链并生成电子数据保管单，以确保文件真实未篡改。"
                    "2" -> binding.tvIntroduce.text = "    简证APP将获取手机音频、定位权限进行录音取证操作，在手机内存条件允许的情况下，目前限制录制时长为1小时。录音完成后系统通过SHA256算法或国密SM3算法计算数据摘要值并实时固定至区块链生成电子数据保管单，以确保音频文件真实未篡改。接着，系统会将音频源文件加密存储至公有云服务器。"
                    "3" -> binding.tvIntroduce.text = "    简证APP将通知并获取手机位置权限和相机、麦克风权限，进行拍摄。在手机内存条件允许的情况下，目前限制时长为1小时。录像完成后系统通过SHA256算法或国密SM3算法计算数据摘要值并实时固定至区块链生成电子数据保管单，以确保录像文件真实未篡改。接着，系统会将录像源文件加密存储至公有云服务器。"
                    else -> binding.tvIntroduce.text = "    简证APP将通知并获取手机读写，音频，视频，定位系统权限，开启系统录屏，进行视频的录制。在手机内存条件允许情况下，规定录制时长不超过1小时。录制成功后，系统通过SHA256算法或国密SM3算法计算数据摘要值并实时固定至区块链生成电子数据保管单，以确保音频文件真实未篡改。接着，系统会将录屏源文件加密存储至公有云服务器。"
                }
            }
            is ItemFunctionBottomBinding -> {
                binding = holder.getBinding<ItemFunctionBottomBinding>()
                GSYVideoHelper.initialize(activity.get()!!, binding.pvVideo, true)
                when (type) {
                    "1" -> {
                        binding.llBottom.visibility = View.VISIBLE
                        GSYVideoHelper.setUrl("https://qtgzc.obs.cn-east-3.myhuaweicloud.com/uploads/2021/08/30/20210830172728689.mp4")
                    }
                    "2" -> {
                        binding.llBottom.visibility = View.VISIBLE
                        GSYVideoHelper.setUrl("https://qtgzc.obs.cn-east-3.myhuaweicloud.com/uploads/2021/08/30/20210830172650704.mp4")
                    }
                    "3" -> {
                        binding.llBottom.visibility = View.VISIBLE
                        GSYVideoHelper.setUrl("https://qtgzc.obs.cn-east-3.myhuaweicloud.com/uploads/2021/08/30/20210830172554518.mp4")
                    }
                    else -> binding.llBottom.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return when (type) {
            "4" -> 2
            else -> 3
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_HEADER
            1 -> TYPE_BODY
            else -> TYPE_BOTTOM
        }
    }

    fun setPageType(type: String) {
        this.type = type
        notifyDataSetChanged()
    }

    fun onPause() = GSYVideoHelper.onPause()

    fun onResume() = GSYVideoHelper.onResume()

    fun onDestroy() = GSYVideoHelper.onDestroy()

}