package com.example.kurslinemobileapp.view.accountsFragments

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.getInfo.InfoAPI
import com.example.kurslinemobileapp.api.getInfo.UserInfoModel
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.android.synthetic.main.fragment_business_account.*
import kotlinx.android.synthetic.main.fragment_business_account.view.*

class BusinessAccountFragment : Fragment() {
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var view : ViewGroup
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_business_account, container, false) as ViewGroup

        val sharedPreferences = requireContext().getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        val id = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        val authHeader = "Bearer $token"
        println("userID"+id)
        println("userToken"+token)
        getDataFromServer(id,authHeader)
            view.backtoMainPageFromBusinessAcc.setOnClickListener {
                val fragmentManager = requireFragmentManager()
                // Start a fragment transaction
                val transaction = fragmentManager.beginTransaction()
                // Replace the first fragment with the second fragment
                transaction.replace(R.id.businessAccountRL, BusinessTransactionProfileFragment())
                transaction.setReorderingAllowed(true)
                // Add the transaction to the back stack
                transaction.addToBackStack(null)
                // Commit the transaction
                transaction.commit()
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
       // val companyPhoto = response.companyPhoto
        //Picasso.get().load(companyPhoto).into(myBusinessImage)
        val userFullName = response.fullName
        val userPhoneNumber = response.mobileNumber
        val userEmail  = response.email
        val companyName = response.companyName.toString()
        val companyAddress = response.companyAddress.toString()
        val about = response.companyAbout.toString()
        val userstaus = response.userStatusId
        val category = response.companyCategoryId.toString()


        view.businessAccountNameEditText.setText(userFullName)
        view.businessAccountPhoneEditText.setText(userPhoneNumber)
        view.businessAccountEmailEditText.setText(userEmail)
        view.businessAccountCompanyEditText.setText(companyName)
        view.companyAdressEditText.setText(companyAddress)
        view.businessAccountAboutEditText.setText(about)
        view.compantStatusEditText.setText(userstaus)
        view.businessAccountCategoryEditText.setText(category)

    }
}