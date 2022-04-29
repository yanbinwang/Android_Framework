package com.dataqin.slice.model

class SliceInfo {
    var tmpPath: String ?=null//分片路径
    var sliceCount: Int = 0//分片总数
    var index: Int? = 0//起始下标
    var endPointer: Long? = 0//起始点->每次的结尾点即为起始点
    var over: Boolean = false//是否结束
}