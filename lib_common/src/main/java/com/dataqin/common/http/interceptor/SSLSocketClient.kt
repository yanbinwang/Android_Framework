package com.dataqin.common.http.interceptor

import android.annotation.SuppressLint
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.*

/**
 * 部分正式域名证书会针对手机时间戳的修改返回错误的提示，添加忽略让网络框架允许该行为
 */
@SuppressLint("CustomX509TrustManager")
object SSLSocketClient {

    fun getSSLSocketFactory(): SSLSocketFactory? {
        try {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }), SecureRandom())
            sslContext.socketFactory
        } catch (ignored: Exception) {
        }
        return null
    }

    fun getHostnameVerifier() = HostnameVerifier { _: String?, _: SSLSession? -> true }

    fun getX509TrustManager(): X509TrustManager? {
        var trustManager: X509TrustManager? = null
        try {
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers = trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) { "Unexpected default trust managers:" + Arrays.toString(trustManagers) }
            trustManager = trustManagers[0] as X509TrustManager
        } catch (ignored: Exception) {
        }
        return trustManager
    }

}