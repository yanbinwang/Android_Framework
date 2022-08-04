package com.dataqin.testnew.model

import java.util.*

/**
 *  Created by wangyanbin
 *  城市选择
 */
class AddressModel {
    var code: String? = null
    var name: String? = null
    var childs: MutableList<ProvinceModel>? = ArrayList()
}