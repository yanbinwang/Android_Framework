package com.github.faucamp.simplertmp

import java.io.IOException
import java.io.OutputStream

object Util {
    private const val HEXES = "0123456789ABCDEF"

    @Throws(IOException::class)
    fun writeUnsignedInt32(out: OutputStream, value: Int) {
        out.write((value ushr 24).toByte().va)
        out.write((value ushr 16) as Byte.toInt())
        out.write((value ushr 8) as Byte.toInt())
        out.write(value as Byte.toInt())
    }
}