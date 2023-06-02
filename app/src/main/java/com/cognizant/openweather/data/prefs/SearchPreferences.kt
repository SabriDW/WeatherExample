package com.cognizant.openweather.data.prefs

import android.content.SharedPreferences
import javax.inject.Inject

class SearchPreferences @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun saveSearchQuery(query: String) {
        sharedPreferences.edit().putString("query", query).apply()
    }

    fun getSearchQuery(): String? {
        return sharedPreferences.getString("query", null)
    }

    fun clearSearchQuery() {
        sharedPreferences.edit().remove("query").apply()
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}
