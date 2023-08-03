package com.example.kurslinemobileapp.service.Room.region

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RegionViewModel(private val regionDao:RegionDao):ViewModel() {
    val _regionName = MutableLiveData<String>()
    val regionName: LiveData<String>
        get() = _regionName

    fun loadRegionById(regionId: Int) {
        viewModelScope.launch {
            val region = regionDao.getRegionsForEditAnnouncement(regionId)
            _regionName.value = region?.regionName ?: "Default Name"
        }
    }

}