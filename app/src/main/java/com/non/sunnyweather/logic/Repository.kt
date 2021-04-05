package com.non.sunnyweather.logic

import androidx.lifecycle.liveData
import com.non.sunnyweather.logic.dao.PlaceDao
import com.non.sunnyweather.logic.model.Place
import com.non.sunnyweather.logic.model.Weather
import com.non.sunnyweather.logic.network.SunnyWeatherNetWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

object Repository {

    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetWork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        //创建一个协程域，让两个网络请求同时进行，增加效率
        coroutineScope {
            val deferredRealtime = async {
                SunnyWeatherNetWork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetWork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" +
                                "daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavePlace()

    fun savePlaceRecord(place: Place) = PlaceDao.savePlaceRecord(place)

    fun getSavedPlaceRecord() = PlaceDao.getSavePlaceRecord()

    fun cleanPlaceRecord() = PlaceDao.cleanPlaceRecord()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

    fun isPlaceRecord() = PlaceDao.isPlaceRecord()

}