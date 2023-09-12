package com.example.kurslinemobileapp.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.api.companyTeachers.companyProfile.Announcement
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AnnouncementViewModel(private val announcementService: AnnouncementAPI) : ViewModel() {

    private val announcements = mutableListOf<LiveData<Announcement>>()
    private var currentItemIndex = 0
    private val batchSize = 20

    // LiveData for the list of announcements to be displayed
    private val _announcementList = MutableLiveData<List<LiveData<Announcement>>>()
    val announcementList: LiveData<List<LiveData<Announcement>>>
        get() = _announcementList

    // Function to fetch all items initially
    @SuppressLint("CheckResult")
    fun fetchAllAnnouncements() {
        val limit = batchSize
        val offset = 0 // Initial offset

        announcementService.getAnnouncement(limit, offset)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                // Handle the response here and add LiveData items to 'announcements' list
                // Example:
                // val announcementLiveData = createAnnouncementLiveData(response)
                // announcements.addAll(announcementLiveData)

                // Update the LiveData with the initial 20 items
                _announcementList.postValue(getNextBatchOfItems())
            }, { error ->
                // Handle the error
            })
    }

    // Function to load the next batch of items (20 at a time)
    fun loadNextBatch() {
        _announcementList.postValue(getNextBatchOfItems())
    }

    private fun getNextBatchOfItems(): List<LiveData<Announcement>> {
        val endIndex = minOf(currentItemIndex + batchSize, announcements.size)
        val nextBatch = announcements.subList(currentItemIndex, endIndex)
        currentItemIndex += batchSize

        return nextBatch
    }

    // ...
}