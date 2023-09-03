package com.example.kurslinemobileapp.view.callback

import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement

interface OnPaginationResponseListener {

    fun <T>run(response: T?)

}