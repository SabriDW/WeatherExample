package com.cognizant.openweather

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // Hilt will generate a base class for our application
class WeatherApplication : Application()