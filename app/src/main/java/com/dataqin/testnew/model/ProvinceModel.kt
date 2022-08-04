package com.dataqin.testnew.model

/**
 *  Created by wangyanbin
 *  省份类
 */
class ProvinceModel(
    var code: String? = null,
    var name: String? = null,
    var childs: MutableList<ProperModel>? = ArrayList()
)