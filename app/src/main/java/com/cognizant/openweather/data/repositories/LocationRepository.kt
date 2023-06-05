package com.cognizant.openweather.data.repositories

import android.location.Location
import com.google.android.gms.tasks.Task

interface LocationRepository {
    fun getLastLocation(): Task<Location>?

}