package com.cognizant.openweather.di

import com.cognizant.openweather.data.repositories.WeatherRepositoryImpl
import com.cognizant.openweather.network.NetworkClient
import com.cognizant.openweather.network.currentweather.WeatherClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(
        weatherClient: WeatherClient,
    ): WeatherRepositoryImpl {
        return WeatherRepositoryImpl(weatherClient)
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideResponseHandler(moshi: Moshi): NetworkClient = NetworkClient(moshi)

}