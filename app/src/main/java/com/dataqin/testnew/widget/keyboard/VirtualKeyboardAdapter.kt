package com.dataqin.testnew.widget.keyboard

import android.view.View
import androidx.core.content.ContextCompat
import com.dataqin.common.base.binding.BaseQuickAdapter
import com.dataqin.common.base.binding.BaseViewBindingHolder
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ItemVirtualKeyboardBinding

class VirtualKeyboardAdapter(var list: MutableList<Map<String, String>>) : BaseQuickAdapter<Map<String, String>, ItemVirtualKeyboardBinding>(list) {

    override fun convert(holder: BaseViewBindingHolder, item: Map<String, String>?) {
        if (null != item) {
            holder.getBinding<ItemVirtualKeyboardBinding>().apply {
                when (val position = holder.absoluteAdapterPosition) {
                    9 -> {
                        relKeyboardDelete.visibility = View.INVISIBLE
                        tvKeyboard.visibility = View.VISIBLE
                        tvKeyboard.text = list[position]["name"]
                        tvKeyboard.setBackgroundColor(ContextCompat.getColor(context!!, com.dataqin.common.R.color.grey_b3b3b3))
                    }
                    11 -> {
                        tvKeyboard.setBackgroundResource(R.mipmap.img_keyboard_delete)
                        relKeyboardDelete.visibility = View.VISIBLE
                        tvKeyboard.visibility = View.INVISIBLE
                    }
                    else -> {
                        relKeyboardDelete.visibility = View.INVISIBLE
                        tvKeyboard.visibility = View.VISIBLE
                        tvKeyboard.text = list[position]["name"]
                    }
                }
            }
        }
    }

}