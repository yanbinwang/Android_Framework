package com.example.framework.net

import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * author: wyb
 * date: 2019/7/30.
 * okhttp单例
 */
class OkHttpFactory {
    val okHttpClient: OkHttpClient = OkHttpClient.Builder().connectTimeout(6, TimeUnit.SECONDS) //设置连接超时
            .readTimeout(6, TimeUnit.SECONDS) //设置读超时
            .writeTimeout(6, TimeUnit.SECONDS) //设置写超时
            .retryOnConnectionFailure(true).addInterceptor(LoggingInterceptor()) //日志监听
            .connectionPool(ConnectionPool(8, 15, TimeUnit.SECONDS)) //这里你可以根据自己的机型设置同时连接的个数和时间，我这里8个，和每个保持时间为10s
            .build()

    companion object {
        private var instance: OkHttpFactory? = null

        @Synchronized
        fun getInstance(): OkHttpFactory {
            if (instance == null) {
                instance = OkHttpFactory()
            }
            return instance!!
        }
    }

}
