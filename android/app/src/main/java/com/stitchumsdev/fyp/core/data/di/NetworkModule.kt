package com.stitchumsdev.fyp.core.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.stitchumsdev.fyp.core.data.remote.ApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit

// For Emulator
//private const val BASE_URL = "http://10.0.2.2:8000/" // ToDo change to real api on deployment

// For tethered device
private const val BASE_URL = "http://192.168.178.165:8000/"
val networkModule = module {
    // For dealing with JSON
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
        }
    }

    // For logging
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel( HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    // Retrofit instance
    single {
        val contentType = "application/json".toMediaType()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory(contentType))
            .build()
    }

    // Api Service instance
    single<ApiService> {
        get<Retrofit>().create(ApiService::class.java)
    }
}