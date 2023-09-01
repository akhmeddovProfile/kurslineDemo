package com.example.kurslinemobileapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Announcemenet
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.api.companyTeachers.companyProfile.Announcement
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ViewModelPagination:ViewModel() {
    private val _newAnnouncements = MutableLiveData<List<Announcemenet>>()
    val newAnnouncements: LiveData<List<Announcemenet>> = _newAnnouncements
    lateinit var compositeDisposable:CompositeDisposable
    private var currentOffset = 100
    private val PAGE_SIZE = 100
    private var isLoading = false

    fun loadMoreData() {
        if (isLoading) {
            return
        }
        isLoading = true
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
        compositeDisposable.add(
            retrofit.getAnnouncement(limit = PAGE_SIZE, offset = currentOffset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    isLoading = false
                    handleResponse(response)
                    currentOffset += PAGE_SIZE
                }, { throwable ->
                    isLoading = false
                    println("Error: $throwable")
                })
        )

    }

    private fun handleResponse(response: GetAllAnnouncement) {
        val newAnnouncements = response.announcemenets
        _newAnnouncements.value = newAnnouncements
    }


}