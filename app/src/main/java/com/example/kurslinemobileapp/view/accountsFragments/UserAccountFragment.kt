package com.example.kurslinemobileapp.view.accountsFragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.app.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.getUserCmpDatas.InfoAPI
import com.example.kurslinemobileapp.api.getUserCmpDatas.UserCmpInfoModel.UserInfoModel
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.Constant.sharedkeyname
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.loginRegister.UserToCompanyActivity
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_account.view.*

class UserAccountFragment : Fragment() {
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var view: ViewGroup
    private lateinit var sharedPreferences: SharedPreferences
    private val REQUEST_IMAGE_CAPTURE = 1 // Request code for image capture
    val MAX_IMAGE_WIDTH = 800 // Maximum width for the compressed image
    val MAX_IMAGE_HEIGHT = 600 // Maximum height for the compressed image

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_account, container, false) as ViewGroup

        val scroll = view.findViewById<ScrollView>(R.id.scrollUserAccount)
        scroll.visibility = View.GONE
        val lottie = view.findViewById<LottieAnimationView>(R.id.loadingUserAccount)
        lottie.visibility = View.VISIBLE
        lottie.playAnimation()
        // Get the SharedPreferences object
         sharedPreferences =
            requireContext().getSharedPreferences(sharedkeyname, Context.MODE_PRIVATE)
        val id = sharedPreferences.getInt("userID", 0)
        val token = sharedPreferences.getString("USERTOKENNN", "")
        val authHeader = "Bearer $token"
        println("userID" + id)
        println("userToken" + authHeader)

        getDataFromServer(id, authHeader)

        view.goToBusinessCreate.setOnClickListener {
            val intent = Intent(requireContext(), UserToCompanyActivity::class.java)
            startActivity(intent)
        }
        // Display the account information in the UI

        view.backtoMainPage.setOnClickListener {
        }

        view.userUpdateTxt.setOnClickListener {
            val intent = Intent(requireContext(), UpdateUserActivity::class.java)
            startActivity(intent)

        }

        return view
    }

    private fun getDataFromServer(id: Int, token: String) {
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(InfoAPI::class.java)
        compositeDisposable.add(retrofit.getUserInfo(token, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse,
                { throwable -> println("MyTests: $throwable") }
            ))
    }

    private fun handleResponse(response: UserInfoModel) {

        //  Picasso.get().load(response.photo.toString()).into(myProfileImage)
        val scroll = view.findViewById<ScrollView>(R.id.scrollUserAccount)
        scroll.visibility = View.VISIBLE
        val lottie = view.findViewById<LottieAnimationView>(R.id.loadingUserAccount)
        lottie.visibility = View.GONE
        lottie.pauseAnimation()
        val userFullName = response.fullName
        val userPhoneNumber = response.mobileNumber
        val userEmail = response.email

        println("userfullname:" + response.fullName + response.mobileNumber + response.email)

        view.accountNameEditText.setText(userFullName)
        view.accountPhoneEditText.setText(userPhoneNumber)
        view.accountMailEditText.setText(userEmail)

        if (response.photo == null) {
            view.myProfileImage.setImageResource(R.drawable.setpp)
        } else {
            Picasso.get().load(response.photo).into(view.myProfileImage)
        }
        val editor = sharedPreferences.edit()
        editor.putString("profilePhotoUrl", response.photo)
        editor.putString("accountName", response.fullName)
        editor.putString("accountPhone", response.mobileNumber)
        editor.putString("accountMail",response.email)
        editor.apply()
    }

    override fun onResume() {
        val scroll = view.findViewById<ScrollView>(R.id.scrollUserAccount)
        scroll.visibility = View.GONE
        val lottie = view.findViewById<LottieAnimationView>(R.id.loadingUserAccount)
        lottie.visibility = View.VISIBLE
        lottie.playAnimation()
        val id = sharedPreferences.getInt("userID", 0)
        val token = sharedPreferences.getString("USERTOKENNN", "")
        val authHeader = "Bearer $token"
        getDataFromServer(id,authHeader)
        super.onResume()
    }
}