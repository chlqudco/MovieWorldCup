package com.chlqudco.movieworldcup.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object TmdbServiceFactory {
    fun create(credential: String): TmdbApi {
        val authentication = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Accept", "application/json")

            if (credential.length == 32 && !credential.contains('.')) {
                requestBuilder.url(
                    original.url.newBuilder()
                        .addQueryParameter("api_key", credential)
                        .build()
                )
            } else if (credential.isNotBlank()) {
                requestBuilder.header("Authorization", "Bearer $credential")
            }

            chain.proceed(requestBuilder.build())
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authentication)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApi::class.java)
    }
}
