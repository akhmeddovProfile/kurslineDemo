package com.example.kurslinemobileapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Announcemenet
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.api.companyTeachers.companyProfile.Announcement
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.callback.OnPaginationResponseListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

//interface ViewModelCallBack{
//    fun <T>run(response:T)
//}

class ViewModelPagination(private val announcementService: AnnouncementAPI) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnnouncementViewModel::class.java)) {
            return AnnouncementViewModel(announcementService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
    }
