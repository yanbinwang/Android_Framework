package com.dataqin.common.utils

import android.annotation.SuppressLint
import android.util.Base64
import com.dataqin.base.utils.LogUtil
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@SuppressLint("GetInstance")
object AESUtil {
    private val CHARSET_UTF8 by lazy { StandardCharsets.UTF_8 }//字符编码(默认使用UTF-8编码 getBytes()默认使用ISO8859-1编码
    private const val CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding"//加解密算法/工作模式/填充方式

    /**
     * 加密
     * @param secretKey 加密密码，长度：16 或 32 个字符
     * @param data      待加密内容
     * @return 返回Base64转码后的加密数据
     */
    @JvmStatic
    fun encrypt(secretKey: String, data: String): String {
        try {
            //创建AES秘钥
            val secretKeySpec = SecretKeySpec(secretKey.toByteArray(CHARSET_UTF8), "AES")
            //创建密码器
            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            //初始化加密器
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
            //将加密以后的数据进行 Base64 编码
            return base64Encode(cipher.doFinal(data.toByteArray(CHARSET_UTF8)))
        } catch (e: Exception) {
            handleException("encrypt", e)
        }
        return ""
    }

    /**
     * 解密
     * @param secretKey  解密的密钥，长度：16 或 32 个字符
     * @param base64Data 加密的密文 Base64 字符串
     * @return 返回Base64转码后的加密数据
     */
    @JvmStatic
    fun decrypt(secretKey: String, base64Data: String): String {
        try {
            val data = base64Decode(base64Data)
            //创建AES秘钥
            val secretKeySpec = SecretKeySpec(secretKey.toByteArray(CHARSET_UTF8), "AES")
            //创建密码器
            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            //初始化解密器
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
            //执行解密操作
            return String(cipher.doFinal(data), CHARSET_UTF8)
        } catch (e: Exception) {
            handleException("decrypt", e)
        }
        return ""
    }

    /**
     * 将 字节数组 转换成 Base64 编码
     * 用Base64.DEFAULT模式会导致加密的text下面多一行（在应用中显示是这样）
     */
    private fun base64Encode(data: ByteArray) = Base64.encodeToString(data, Base64.NO_WRAP)

    /**
     * 将 Base64 字符串 解码成 字节数组
     */
    private fun base64Decode(data: String) = Base64.decode(data, Base64.NO_WRAP)

    /**
     * 处理异常
     */
    private fun handleException(methodName: String, e: Exception) = LogUtil.e("AESUtil", "${methodName}---->${e}")

}