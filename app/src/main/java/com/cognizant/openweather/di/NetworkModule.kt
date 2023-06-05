package com.cognizant.openweather.di

import com.cognizant.openweather.network.NetworkClient
import com.cognizant.openweather.network.currentweather.WeatherClient
import com.cognizant.openweather.network.currentweather.WeatherService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .client(
                OkHttpClient.Builder().build()
            )
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherService(retrofit: Retrofit): WeatherService =
        retrofit.create(WeatherService::class.java)

    @Provides
    @Singleton
    fun provideWeatherClient(
        weatherService: WeatherService,
        responseHandler: NetworkClient,
        @Named("apiKey") apiKey: String
    ): WeatherClient =
        WeatherClient(weatherService, responseHandler, apiKey)

    @Provides
    @Named("apiKey")
    fun provideApiKey(): String {
        // TODO: for demo purposes only, store private keys in a secure location
        return "0fc22c042cfb8daaf3b5c0b50f203ca3"
    }

}