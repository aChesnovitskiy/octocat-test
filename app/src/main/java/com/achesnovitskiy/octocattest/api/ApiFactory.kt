package com.achesnovitskiy.empoyees.api

import com.achesnovitskiy.octocattest.api.TLSSocketFactory
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyManagementException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException


object ApiFactory {
    private const val BASE_URL = "https://api.github.com/"
    private var retrofit: Retrofit

    init {
        // It is necessary for working with HTTPS on pre-lollipop devices
        var client: OkHttpClient? = OkHttpClient()
        try {
            val tlsSocketFactory = TLSSocketFactory()
            if (tlsSocketFactory.trustManager != null) {
                client = OkHttpClient.Builder()
                    .sslSocketFactory(tlsSocketFactory, tlsSocketFactory.trustManager!!)
                    .build()
            }
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        }

        retrofit = Retrofit.Builder()
            .client(client!!)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    fun getApiService(): ApiService = retrofit.create(ApiService::class.java)
}