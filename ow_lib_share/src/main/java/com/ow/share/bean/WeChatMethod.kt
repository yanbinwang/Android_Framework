package com.ow.share.bean

enum class WeChatMethod(private val value: String) {
    TEXT("TEXT"),

    IMAGE("IMAGE"),

    MUSIC("MUSIC"),

    VIDEO("VIDEO"),

    WEBPAGE("WEBPAGE"),

    MINIPROGRAM("MINIPROGRAM");

    override fun toString(): String {
        return this.value
    }

}
