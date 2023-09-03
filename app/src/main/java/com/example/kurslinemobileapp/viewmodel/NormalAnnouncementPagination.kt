package com.example.kurslinemobileapp.viewmodel

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.callback.OnPaginationResponseListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class NormalAnnouncementPagination(val linearLayoutManager: LinearLayoutManager, val onPaginationResponseListener: OnPaginationResponseListener):PaginationScrollListener(
    linearLayoutManager
) {

    lateinit var compositeDisposable:CompositeDisposable
    private var currentOffset = 0
    private val PAGE_SIZE = 10
    private var isLoading = false
    override fun loadMoreItems() {
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
                    onPaginationResponseListener.run(response)
                    currentOffset += PAGE_SIZE
                }, { throwable ->
                    isLoading = false
                    println("Error: $throwable")
                })
        )

    }

    override fun isLastPage(): Boolean {
//        TODO("Not yet implemented")
        return false
    }

    override fun isLoading(): Boolean {
        return isLoading
    }
}