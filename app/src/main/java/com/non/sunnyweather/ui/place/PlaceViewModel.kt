package com.non.sunnyweather.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.non.sunnyweather.logic.Repository
import com.non.sunnyweather.logic.dao.PlaceDao
import com.non.sunnyweather.logic.model.Place

class PlaceViewModel : ViewModel(){

    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Place>()

    val placeLiveData = Transformations.switchMap(searchLiveData) { query ->
        Repository.searchPlaces(query)
    }

    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }

    fun savePlace(place: Place) = Repository.savePlace(place)

    fun getSavedPlace() = Repository.getSavedPlace()

    fun savePlaceRecord(place: Place) = Repository.savePlaceRecord(place)

    fun getSavedPlaceRecord() = Repository.getSavedPlaceRecord()

    fun cleanPlaceRecord() = Repository.cleanPlaceRecord()

    fun isPlaceSaved() = Repository.isPlaceSaved()

    fun isPlaceRecord() = Repository.isPlaceRecord()

}