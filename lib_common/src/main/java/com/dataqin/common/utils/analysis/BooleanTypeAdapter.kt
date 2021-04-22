package com.dataqin.common.utils.analysis

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

import java.io.IOException

/**
 * 接管【String】类型的序列化和反序列化过程
 */
class BooleanTypeAdapter : TypeAdapter<Boolean>() {

    @Throws(IOException::class)
    override fun write(writer: JsonWriter, value: Boolean?) {
        writer.value(value.toString())
    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): Boolean? {
        return try {
            val value = reader.nextString()
            "Y" == value || "1" == value || "true" == value
        } catch (e: NullPointerException) {
            false
        }
    }

}
