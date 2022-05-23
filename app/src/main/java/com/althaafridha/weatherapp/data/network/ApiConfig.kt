package com.althaafridha.weatherapp.data.network

import com.althaafridha.weatherapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {

	fun getApiService(): ApiService {
		val loggingInterceptor = if (BuildConfig.DEBUG) {
			HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
		} else {
			HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
		}

		val okHttpClient = OkHttpClient.Builder()
			.retryOnConnectionFailure(true)
			.addInterceptor(loggingInterceptor)
			.addInterceptor(defaultHttpClient())
			.pingInterval(10, TimeUnit.SECONDS)
			.readTimeout(30, TimeUnit.SECONDS)
			.connectTimeout(30, TimeUnit.SECONDS)
			.build()

		return Retrofit.Builder()
			.baseUrl(BuildConfig.BASE_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.client(okHttpClient)
			.build()
			.create(ApiService::class.java)
	}

	private fun defaultHttpClient() : Interceptor {
		return Interceptor { chain ->
			val request = chain.request()
				.newBuilder()
				.addHeader("Content-Type", "application/json")
				.build()
			return@Interceptor chain.proceed(request)
		}
	}
}