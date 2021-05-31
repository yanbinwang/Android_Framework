package com.dataqin.common.http.factory

import com.dataqin.common.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * author: wyb
 * date: 2019/7/30.
 * retrofit单例
 */
class RetrofitFactory private constructor() {
    private val retrofit by lazy {
        Retrofit.Builder()
            .client(OkHttpFactory.instance.okHttpClient)
            .baseUrl(BuildConfig.LOCALHOST)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    //纯粹的网络请求，不加任何拦截
    private val retrofit2 by lazy {
        Retrofit.Builder()
            .client(OkHttpClient.Builder()
                    .connectTimeout(6, TimeUnit.SECONDS)//设置连接超时
                    .writeTimeout(2, TimeUnit.HOURS)//设置写超时
                    .readTimeout(2, TimeUnit.HOURS)//设置读超时
                    .retryOnConnectionFailure(true)
                    .build())
            .baseUrl(BuildConfig.LOCALHOST)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    companion object {
        @JvmStatic
        val instance: RetrofitFactory by lazy {
            RetrofitFactory()
        }
    }

    //获取一个请求API
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }

    //获取一个不加头不加拦截器的API
    fun <T> create2(service: Class<T>): T {
        return retrofit2.create(service)
    }

}
