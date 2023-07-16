package com.example.kurslinemobileapp.view.accountsFragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.airbnb.lottie.LottieAnimationView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.ResizeTransformation
import com.example.kurslinemobileapp.api.companyData.CompanyDatasAPI
import com.example.kurslinemobileapp.api.companyData.CompanyRegisterData
import com.example.kurslinemobileapp.api.getUserCmpDatas.InfoAPI
import com.example.kurslinemobileapp.api.getUserCmpDatas.UserCmpInfoModel.UserInfoModel
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.loginRegister.UserToCompanyActivity
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_business_account.view.*

class BusinessAccountFragment : Fragment() {
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var view : ViewGroup
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_business_account, container, false) as ViewGroup
        val scroll = view.findViewById<ScrollView>(R.id.scrollBusinessAccount)
        scroll.visibility = View.GONE
        val lottie = view.findViewById<LottieAnimationView>(R.id.loadingBusinessAccount)
        lottie.visibility = View.VISIBLE
        lottie.playAnimation()
         sharedPreferences = requireContext().getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
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

        view.companyUpdateTxt.setOnClickListener {
            val intent = Intent(requireContext(), CompanyUpdateActivity::class.java)
            startActivity(intent)
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
        val scroll = view.findViewById<ScrollView>(R.id.scrollBusinessAccount)
        scroll.visibility = View.VISIBLE
        val lottie = view.findViewById<LottieAnimationView>(R.id.loadingBusinessAccount)
        lottie.visibility = View.GONE
        lottie.pauseAnimation()
       val companyPhoto = response.photo
        if (companyPhoto == null){
            view.myBusinessImage.setImageResource(R.drawable.setpp)
        }else{
            Picasso.get().load(companyPhoto).transform(ResizeTransformation(300, 300)).into(view.myBusinessImage)
        }

        val userFullName = response.fullName
        val userPhoneNumber = response.mobileNumber
        val userEmail  = response.email
        val companyName = response.companyName.toString()
        val companyAddress = response.companyAddress.toString()
        val about = response.companyAbout.toString()
        val userstaus = response.userStatusId
        val category  = response.companyCategoryId

        val userStatusId = response.userStatusId.toString()
        val companyCategoryId = response.companyCategoryId.toString()
     sharedPreferences = requireContext().getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userStatusId", userStatusId)
        editor.putString("companyCategoryId", companyCategoryId)
        editor.putString("companyOwnerName",userFullName)
        editor.putString("companyEmail",userEmail)
        editor.putString("companyNumber",userPhoneNumber)
        editor.putString("companyName",companyName)
        editor.putString("companyAddress",companyAddress)
        editor.putString("companyAbout",about)
        editor.putString("companyPhoto",companyPhoto)
        editor.apply()

        var categoryName = ""
        getCategoryList()!!.subscribe({ categories ->
            println("333")
             categoryName = categories.categories.find { it.categoryId == category }?.categoryName.toString()
            view.businessAccountCategoryEditText.setText(categoryName)
            editor.putString("companyCategory",categoryName)
            editor.apply()
        }, { throwable ->
            // Handle error during category retrieval
            println("Category retrieval error: $throwable")
        }).let { compositeDisposable.add(it) }

        var statusName = ""
        getStatusList()!!.subscribe({ status ->
            statusName = status.statuses.find { it.statusId == category }?.statusName.toString()
            view.compantStatusEditText.setText(statusName)
            editor.putString("companyStatus",statusName)
            editor.apply()
        }, { throwable ->
            // Handle error during category retrieval
            println("Category retrieval error: $throwable")
        }).let { compositeDisposable.add(it) }

        view.businessAccountNameEditText.setText(userFullName)
        view.businessAccountPhoneEditText.setText(userPhoneNumber)
        view.businessAccountEmailEditText.setText(userEmail)
        view.businessAccountCompanyEditText.setText(companyName)
        view.companyAdressEditText.setText(companyAddress)
        view.businessAccountAboutEditText.setText(about)
        view.compantStatusEditText.setText(userstaus)
        view.businessAccountCategoryEditText.setText(category)

    }

    private fun getCategoryList(): Observable<CompanyRegisterData>? {
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(CompanyDatasAPI::class.java)
        return retrofit.getCategories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getStatusList():Observable<CompanyRegisterData>?{
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(CompanyDatasAPI::class.java)
        return retrofit.getStatus()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}