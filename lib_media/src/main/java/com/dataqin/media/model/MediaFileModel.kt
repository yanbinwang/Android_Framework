package com.dataqin.media.model

import java.util.*

/**
 *  Created by wangyanbin
 *  文件信息类
 */
class MediaFileModel (
    var name: String? = null,
    var path: String? = null,
    var lastModified: Long = 0
) {
    override fun toString(): String {
        return "MediaFileInfoModel{" +
                "name=" + name +
                ", path=" + path +
                ", lastModified=" + lastModified +
                ", lastModified=" + Date(lastModified).toGMTString() +
                '}'
    }
}