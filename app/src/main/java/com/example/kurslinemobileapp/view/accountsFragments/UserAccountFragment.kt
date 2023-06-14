package com.example.kurslinemobileapp.view.accountsFragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.AnnouncementDetailModel
import com.example.kurslinemobileapp.api.getInfo.InfoAPI
import com.example.kurslinemobileapp.api.getInfo.UserInfoModel
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.Constant.sharedkeyname
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.courseFmAc.CourseBusinessProfile
import com.example.kurslinemobileapp.view.loginRegister.LoginActivity
import com.example.kurslinemobileapp.view.loginRegister.RegisterCompanyActivity
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*

class UserAccountFragment : Fragment() {
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var view : ViewGroup
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_account, container, false) as ViewGroup

        // Get the SharedPreferences object
        val sharedPreferences = requireContext().getSharedPreferences(sharedkeyname, Context.MODE_PRIVATE)
        val id = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        val authHeader = "Bearer $token"
        println("userID"+id)
        println("userToken"+authHeader)
    getDataFromServer(id,authHeader)

        view.goToBusinessCreate.setOnClickListener {
            val intent = Intent(requireContext(), RegisterCompanyActivity::class.java)
            startActivity(intent)
        }
        // Display the account information in the UI

        view.backtoMainPage.setOnClickListener {
            findNavController().navigate(R.id.action_blankAccountFragment_to_homeFragment)
        }

        return view
    }

    private fun getDataFromServer(id: Int,token:String) {
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(InfoAPI::class.java)
        compositeDisposable.add(retrofit.getUserInfo(token,id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse,
                { throwable -> println("MyTests: $throwable") }
            ))
    }

    private fun handleResponse(response: UserInfoModel) {
      //  Picasso.get().load(response.photo.toString()).into(myProfileImage)
        val userFullName = response.fullName
        val userPhoneNumber = response.mobileNumber
        val userEmail  = response.email

        println("userfullname:"+response.fullName + response.mobileNumber + response.email)

        view.accountNameEditText.setText(userFullName)
        view.accountPhoneEditText.setText(userPhoneNumber)
        view.accountMailEditText.setText(userEmail)
    }
}