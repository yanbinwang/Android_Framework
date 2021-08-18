package com.dataqin.testnew.widget.keyboard

import android.view.View
import androidx.core.content.ContextCompat
import com.dataqin.common.base.binding.BaseQuickAdapter
import com.dataqin.common.base.binding.BaseViewBindingHolder
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ItemKeyboardBinding

class KeyBoardAdapter (list: MutableList<Map<String, String>>) : BaseQuickAdapter<Map<String, String>, ItemKeyboardBinding>(list){

    override fun convert(holder: BaseViewBindingHolder, item: Map<String, String>?) {
        if (null != item) {
            holder.getBinding<ItemKeyboardBinding>().apply {
                when (val position = holder.absoluteAdapterPosition) {
                    9 -> {
                        rlKeyboardDelete.visibility = View.INVISIBLE
                        tvKeyboard.visibility = View.VISIBLE
                        tvKeyboard.text = data[position]["name"]
                        tvKeyboard.setBackgroundColor(ContextCompat.getColor(context!!, R.color.gray_b3b3b3))
                    }
                    11 -> {
                        tvKeyboard.setBackgroundResource(R.mipmap.ic_keyboard_delete)
                        rlKeyboardDelete.visibility = View.VISIBLE
                        tvKeyboard.visibility = View.INVISIBLE
                    }
                    else -> {
                        rlKeyboardDelete.visibility = View.INVISIBLE
                        tvKeyboard.visibility = View.VISIBLE
                        tvKeyboard.text = data[position]["name"]
                    }
                }
            }
        }
    }

}