package com.non.sunnyweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.non.sunnyweather.SunnyWeatherApplication
import com.non.sunnyweather.logic.model.Place

object PlaceDao {

    fun savePlace(place: Place) {
        sharePreferences().edit {
            putString("place", Gson().toJson(place))
        }
    }

    fun getSavePlace(): Place {
        val placeJson = sharePreferences().getString("place", "")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    fun savePlaceRecord(place: Place) {
        sharePreferences().edit {
            putString("place_record", Gson().toJson(place))
        }
    }

    fun getSavePlaceRecord(): Place {
        val placeJson = sharePreferences().getString("place_record", "")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    fun cleanPlaceRecord() {
        sharePreferences().edit {
            remove("place_record")
        }
    }

    fun isPlaceSaved() = sharePreferences().contains("place")

    fun isPlaceRecord() = sharePreferences().contains("place_record")


    private fun sharePreferences() = SunnyWeatherApplication.context.
        getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)
}