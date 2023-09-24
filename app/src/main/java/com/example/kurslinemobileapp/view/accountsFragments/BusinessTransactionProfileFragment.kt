package com.example.kurslinemobileapp.view.accountsFragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.kurslinemobileapp.R
import com.app.kurslinemobileapp.databinding.FragmentBusinessTransactionsProfileBinding
import com.example.kurslinemobileapp.adapter.CompanyTransactionAdapter
import com.example.kurslinemobileapp.api.getUserCmpDatas.InfoAPI
import com.example.kurslinemobileapp.api.getUserCmpDatas.UserCmpInfoModel.UserInfoModel
import com.example.kurslinemobileapp.api.getUserCmpDatas.companyAnnouncement.CompanyTransactionAnnouncement
import com.example.kurslinemobileapp.api.getUserCmpDatas.companyAnnouncement.CompanyTransactionAnnouncementItem
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.courseFmAc.ProductDetailActivity
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class BusinessTransactionProfileFragment : Fragment() {
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var button5: Button
    private lateinit var button6: Button
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var companyTransactionAdapter: CompanyTransactionAdapter
    private lateinit var view: ViewGroup
    private lateinit var mainList: ArrayList<CompanyTransactionAnnouncementItem>
    private lateinit var mainList2: ArrayList<CompanyTransactionAnnouncementItem>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var bindingBusinesTransaction:FragmentBusinessTransactionsProfileBinding
    @SuppressLint("ResourceAsColor", "SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingBusinesTransaction=FragmentBusinessTransactionsProfileBinding.inflate(inflater,container,false)
    /*    view = inflater.inflate(
            R.layout.fragment_business_transactions_profile,
            container,
            false
        ) as ViewGroup*/

        sharedPreferences = requireContext().getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        val id = sharedPreferences.getInt("userID", 0)
        val token = sharedPreferences.getString("USERTOKENNN", "")
        val authHeader = "Bearer $token"
            getDataFromServer(id,authHeader)
        val userFullName = sharedPreferences.getString("companyOwnerName1","")?:""
        val userPhoto = sharedPreferences.getString("companyPhoto1","")?:""
        bindingBusinesTransaction.businessTransName.setText(userFullName)
        if (userPhoto.isEmpty()){
            bindingBusinesTransaction.businessTransProfileImage.setImageResource(R.drawable.setpp)
        }else {
            Picasso.get().load(userPhoto).into(bindingBusinesTransaction.businessTransProfileImage)
        }
        button1 = bindingBusinesTransaction.button1BusinessTrans
        button2 = bindingBusinesTransaction.button2BusinessTrans
        button3 = bindingBusinesTransaction.button3BusinessTrans
        button4 = bindingBusinesTransaction.button4BusinessTrans
        button5 = bindingBusinesTransaction.buttonVipAnn
        button6 = bindingBusinesTransaction.buttonMoveAnn

        updateButtonBackgrounds(button1)
        val lottie = bindingBusinesTransaction.loadingBusinessTrans
        lottie.visibility = View.VISIBLE
        val text = bindingBusinesTransaction.notFoundBusinessTransCourseText
        text.visibility = View.GONE
        val recycler = bindingBusinesTransaction.businessTransRv
        recycler.visibility = View.GONE
        val image = bindingBusinesTransaction.businessTransNotFoundImage
        image.visibility = View.GONE
        lottie.playAnimation()
        val coursesRV = bindingBusinesTransaction.businessTransRv
        coursesRV.layoutManager = GridLayoutManager(requireContext(), 2)
        getWaitingAnnouncements(id, authHeader)

        val goButton = bindingBusinesTransaction.goToBusinessAccountFragment
        goButton.setOnClickListener {
            println("1")
        findNavController().navigate(R.id.action_businessTransactionProfileFragment_to_businessAccountFragment)
        }

        mainList = ArrayList<CompanyTransactionAnnouncementItem>()
        mainList2 = ArrayList<CompanyTransactionAnnouncementItem>()

        button1.setOnClickListener {
            updateButtonBackgrounds(button1)
            val lottie = bindingBusinesTransaction.loadingBusinessTrans
            lottie.visibility = View.VISIBLE
            val text = bindingBusinesTransaction.notFoundBusinessTransCourseText
            text.visibility = View.GONE
            val image = bindingBusinesTransaction.businessTransNotFoundImage
            image.visibility = View.GONE
            lottie.playAnimation()
            val recycler = bindingBusinesTransaction.businessTransRv
            recycler.visibility = View.GONE
            val coursesRV = bindingBusinesTransaction.businessTransRv
            coursesRV.layoutManager = GridLayoutManager(requireContext(), 2)
            getWaitingAnnouncements(id, authHeader)
        }
        button2.setOnClickListener {
            updateButtonBackgrounds(button2)
            val lottie = bindingBusinesTransaction.loadingBusinessTrans
            lottie.visibility = View.VISIBLE
            val text = bindingBusinesTransaction.notFoundBusinessTransCourseText
            text.visibility = View.GONE
            val image = bindingBusinesTransaction.businessTransNotFoundImage
            image.visibility = View.GONE
            lottie.playAnimation()
            val recycler = bindingBusinesTransaction.businessTransRv
            recycler.visibility = View.GONE
            val coursesRV = bindingBusinesTransaction.businessTransRv
            coursesRV.layoutManager = GridLayoutManager(requireContext(), 2)
            getActiveAnnouncements(id, authHeader)
        }
        button3.setOnClickListener {
            updateButtonBackgrounds(button3)
            val lottie = bindingBusinesTransaction.loadingBusinessTrans
            lottie.visibility = View.VISIBLE
            val text = bindingBusinesTransaction.notFoundBusinessTransCourseText
            text.visibility = View.GONE
            val image = bindingBusinesTransaction.businessTransNotFoundImage
            image.visibility = View.GONE
            lottie.playAnimation()
            val recycler = bindingBusinesTransaction.businessTransRv
            recycler.visibility = View.GONE
            val coursesRV = bindingBusinesTransaction.businessTransRv
            coursesRV.layoutManager = GridLayoutManager(requireContext(), 2)
            getDeactiveAnnouncements(id, authHeader)
        }
        button4.setOnClickListener {
            updateButtonBackgrounds(button4)
            val lottie = bindingBusinesTransaction.loadingBusinessTrans
            lottie.visibility = View.VISIBLE
            val text = bindingBusinesTransaction.notFoundBusinessTransCourseText
            text.visibility = View.GONE
            val image = bindingBusinesTransaction.businessTransNotFoundImage
            image.visibility = View.GONE
            lottie.playAnimation()
            val recycler = bindingBusinesTransaction.businessTransRv
            recycler.visibility = View.GONE
            val coursesRV = bindingBusinesTransaction.businessTransRv
            coursesRV.layoutManager = GridLayoutManager(requireContext(), 2)
            getWaitingAnnouncements(id, authHeader)
        }

        button5.setOnClickListener {
            updateButtonBackgrounds(button5)
            val lottie = bindingBusinesTransaction.loadingBusinessTrans
            lottie.visibility = View.VISIBLE
            val text = bindingBusinesTransaction.notFoundBusinessTransCourseText
            text.visibility = View.GONE
            val image = bindingBusinesTransaction.businessTransNotFoundImage
            image.visibility = View.GONE
            lottie.playAnimation()
            val recycler = bindingBusinesTransaction.businessTransRv
            recycler.visibility = View.GONE
            val coursesRV = bindingBusinesTransaction.businessTransRv
            coursesRV.layoutManager = GridLayoutManager(requireContext(), 2)
            getVipAnnouncements(id, authHeader)
        }

        button6.setOnClickListener {
            updateButtonBackgrounds(button6)
            val lottie = bindingBusinesTransaction.loadingBusinessTrans
            lottie.visibility = View.VISIBLE
            val text = bindingBusinesTransaction.notFoundBusinessTransCourseText
            text.visibility = View.GONE
            val image = bindingBusinesTransaction.businessTransNotFoundImage
            image.visibility = View.GONE
            lottie.playAnimation()
            val recycler = bindingBusinesTransaction.businessTransRv
            recycler.visibility = View.GONE
            val coursesRV = bindingBusinesTransaction.businessTransRv
            coursesRV.layoutManager = GridLayoutManager(requireContext(), 2)
            getMovingAnnouncements(id, authHeader)
        }

        return bindingBusinesTransaction.root
    }

    @SuppressLint("ResourceAsColor")
    private fun updateButtonBackgrounds(selectedButton: Button) {
        val buttons = arrayOf(button1, button2, button3, button4,button5,button6)

        for (button in buttons) {
            if (button == selectedButton) {
                // Set selected button background
                button.setBackgroundResource(R.drawable.business_button_bg)
                button.setTextColor(this.getResources().getColor(R.color.white))
            } else {
                // Set unselected button background
                button.setBackgroundResource(R.drawable.business_button_bg_2)
                button.setTextColor(this.getResources().getColor(R.color.black))
            }
        }

    }


    private fun getActiveAnnouncements(id: Int, token: String) {
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(InfoAPI::class.java)
        compositeDisposable.add(retrofit.getActiveAnnouncements(token, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponseActive,
                { throwable -> println("MyTests1: $throwable") }
            ))
    }

    private fun handleResponseActive(response: CompanyTransactionAnnouncement) {
        mainList.clear()
        mainList2.clear()
        if (response.isNotEmpty()) {
            val companyDetailItem = response
            mainList.addAll(companyDetailItem)
            mainList2.addAll(companyDetailItem)
            val recycler = bindingBusinesTransaction.businessTransRv
            recycler.visibility = View.VISIBLE
            val text = bindingBusinesTransaction.notFoundBusinessTransCourseText
            text.visibility = View.GONE
            val image = bindingBusinesTransaction.businessTransNotFoundImage
            image.visibility = View.GONE
            val lottie = bindingBusinesTransaction.loadingBusinessTrans
            lottie.visibility = View.GONE
            lottie.pauseAnimation()
            println("responseElan: " + response)
            companyTransactionAdapter = CompanyTransactionAdapter(mainList2,requireContext())
            recycler.adapter = companyTransactionAdapter
            recycler.isNestedScrollingEnabled = false
            companyTransactionAdapter.notifyDataSetChanged()
            companyTransactionAdapter.setOnItemClickListener {

                val intent = Intent(activity, ProductDetailActivity::class.java)
                activity?.startActivity(intent)
                sharedPreferences =
                    requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                sharedPreferences.edit().putInt("announcementId", it.id).apply()
                println("gedenId-----" + it.id)
                editor.apply()
            }
        }else{
            val recycler = bindingBusinesTransaction.businessTransRv
            recycler.visibility = View.GONE
            val text = bindingBusinesTransaction.notFoundBusinessTransCourseText
            text.visibility = View.VISIBLE
            val image = bindingBusinesTransaction.businessTransNotFoundImage
            image.visibility = View.VISIBLE
            val lottie = bindingBusinesTransaction.loadingBusinessTrans
            lottie.visibility = View.GONE
            lottie.pauseAnimation()
        }
    }

    private fun getDeactiveAnnouncements(id: Int, token: String) {
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(InfoAPI::class.java)
        compositeDisposable.add(retrofit.getDeactiveAnnouncements(token, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponseDeactive,
                { throwable -> println("MyTests2: $throwable") }
            ))
    }

    private fun handleResponseDeactive(response: CompanyTransactionAnnouncement) {
        mainList.clear()
        mainList2.clear()
        if (response.isNotEmpty()) {
            val companyDetailItem = response
            mainList.addAll(companyDetailItem)
            mainList2.addAll(companyDetailItem)
            val recycler = bindingBusinesTransaction.businessTransRv
            recycler.visibility = View.VISIBLE
            val text = bindingBusinesTransaction.notFoundBusinessTransCourseText
            text.visibility = View.GONE
            val image = bindingBusinesTransaction.businessTransNotFoundImage
            image.visibility = View.GONE
            val lottie = bindingBusinesTransaction.loadingBusinessTrans
            lottie.visibility = View.GONE
            lottie.pauseAnimation()
            println("responseElan: " + response)
            companyTransactionAdapter = CompanyTransactionAdapter(mainList2,requireContext())
            recycler.adapter = companyTransactionAdapter
            recycler.isNestedScrollingEnabled = false
            companyTransactionAdapter.notifyDataSetChanged()
            companyTransactionAdapter.setOnItemClickListener {
                val intent = Intent(activity, ProductDetailActivity::class.java)
                activity?.startActivity(intent)
                sharedPreferences =
                    requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                sharedPreferences.edit().putInt("announcementId", it.id).apply()
                println("gedenId-----" + it.id)
                editor.apply()
            }
        }else{
            val recycler = bindingBusinesTransaction.businessTransRv
            recycler.visibility = View.GONE
            val text = bindingBusinesTransaction.notFoundBusinessTransCourseText
            text.visibility = View.VISIBLE
            val image = bindingBusinesTransaction.businessTransNotFoundImage
            image.visibility = View.VISIBLE
            val lottie = bindingBusinesTransaction.loadingBusinessTrans
            lottie.visibility = View.GONE
            lottie.pauseAnimation()
        }
    }

    private fun getWaitingAnnouncements(id: Int, token: String) {
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(InfoAPI::class.java)
        compositeDisposable.add(retrofit.getWaitAnnouncements(token, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponseWait,
                { throwable -> println("MyTests3: $throwable") }
            ))
    }

    private fun handleResponseWait(response: CompanyTransactionAnnouncement) {
        mainList.clear()
        mainList2.clear()
        if (response.isNotEmpty()) {
            val companyDetailItem = response
            mainList.addAll(companyDetailItem)
            mainList2.addAll(companyDetailItem)
            val recycler = bindingBusinesTransaction.businessTransRv
            recycler.visibility = View.VISIBLE
            val text = bindingBusinesTransaction.notFoundBusinessTransCourseText
            text.visibility = View.GONE
            val image = bindingBusinesTransaction.businessTransNotFoundImage
            image.visibility = View.GONE
            val lottie = bindingBusinesTransaction.loadingBusinessTrans
            lottie.visibility = View.GONE
            lottie.pauseAnimation()
            println("responseElan: " + response)
            companyTransactionAdapter = CompanyTransactionAdapter(mainList2,requireContext())
            recycler.adapter = companyTransactionAdapter
            recycler.isNestedScrollingEnabled = false
            companyTransactionAdapter.notifyDataSetChanged()
            companyTransactionAdapter.setOnItemClickListener {
                val intent = Intent(activity, ProductDetailActivity::class.java)
                activity?.startActivity(intent)
                sharedPreferences =
                    requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                sharedPreferences.edit().putInt("announcementId", it.id).apply()
                println("gedenId-----" + it.id)
                editor.apply()
            }
        }else{
            val recycler = bindingBusinesTransaction.businessTransRv
            recycler.visibility = View.GONE
            val text = bindingBusinesTransaction.notFoundBusinessTransCourseText
            text.visibility = View.VISIBLE
            val image = bindingBusinesTransaction.businessTransNotFoundImage
            image.visibility = View.VISIBLE
            val lottie = bindingBusinesTransaction.loadingBusinessTrans
            lottie.visibility = View.GONE
            lottie.pauseAnimation()
        }
    }


    private fun getVipAnnouncements(id: Int, token: String) {
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(InfoAPI::class.java)
        compositeDisposable.add(retrofit.getVipAnnoucnementsTransaction(token, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponseVip,
                { throwable -> println("MyTests3: $throwable") }
            ))
    }

    private fun handleResponseVip(response: CompanyTransactionAnnouncement) {
        mainList.clear()
        mainList2.clear()
        if (response.isNotEmpty()) {
            val companyDetailItem = response
            mainList.addAll(companyDetailItem)
            mainList2.addAll(companyDetailItem)
            val recycler = bindingBusinesTransaction.businessTransRv
            recycler.visibility = View.VISIBLE
            val text = bindingBusinesTransaction.notFoundBusinessTransCourseText
            text.visibility = View.GONE
            val image = bindingBusinesTransaction.businessTransNotFoundImage
            image.visibility = View.GONE
            val lottie = bindingBusinesTransaction.loadingBusinessTrans
            lottie.visibility = View.GONE
            lottie.pauseAnimation()
            println("responseElan: " + response)
            companyTransactionAdapter = CompanyTransactionAdapter(mainList2,requireContext())
            recycler.adapter = companyTransactionAdapter
            recycler.isNestedScrollingEnabled = false
            companyTransactionAdapter.notifyDataSetChanged()
            companyTransactionAdapter.setOnItemClickListener {
                val intent = Intent(activity, ProductDetailActivity::class.java)
                activity?.startActivity(intent)
                sharedPreferences =
                    requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                sharedPreferences.edit().putInt("announcementId", it.id).apply()
                println("gedenId-----" + it.id)
                editor.apply()
            }
        }else{
            val recycler = bindingBusinesTransaction.businessTransRv
            recycler.visibility = View.GONE
            val text = bindingBusinesTransaction.notFoundBusinessTransCourseText
            text.visibility = View.VISIBLE
            val image = bindingBusinesTransaction.businessTransNotFoundImage
            image.visibility = View.VISIBLE
            val lottie = bindingBusinesTransaction.loadingBusinessTrans
            lottie.visibility = View.GONE
            lottie.pauseAnimation()
        }
    }

    private fun getMovingAnnouncements(id: Int, token: String) {
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(InfoAPI::class.java)
        compositeDisposable.add(retrofit.getMovingAnnoucnementsTransaction(token, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponseMove,
                { throwable -> println("MyTests3: $throwable") }
            ))
    }

    private fun handleResponseMove(response: CompanyTransactionAnnouncement) {
        mainList.clear()
        mainList2.clear()
        if (response.isNotEmpty()) {
            val companyDetailItem = response
            mainList.addAll(companyDetailItem)
            mainList2.addAll(companyDetailItem)
            val recycler = bindingBusinesTransaction.businessTransRv
            recycler.visibility = View.VISIBLE
            val text = bindingBusinesTransaction.notFoundBusinessTransCourseText
            text.visibility = View.GONE
            val image = bindingBusinesTransaction.businessTransNotFoundImage
            image.visibility = View.GONE
            val lottie = bindingBusinesTransaction.loadingBusinessTrans
            lottie.visibility = View.GONE
            lottie.pauseAnimation()
            println("responseElan: " + response)
            companyTransactionAdapter = CompanyTransactionAdapter(mainList2,requireContext())
            recycler.adapter = companyTransactionAdapter
            recycler.isNestedScrollingEnabled = false
            companyTransactionAdapter.notifyDataSetChanged()
            companyTransactionAdapter.setOnItemClickListener {
                val intent = Intent(activity, ProductDetailActivity::class.java)
                activity?.startActivity(intent)
                sharedPreferences =
                    requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                sharedPreferences.edit().putInt("announcementId", it.id).apply()
                println("gedenId-----" + it.id)
                editor.apply()
            }
        }else{
            val recycler = bindingBusinesTransaction.businessTransRv
            recycler.visibility = View.GONE
            val text = bindingBusinesTransaction.notFoundBusinessTransCourseText
            text.visibility = View.VISIBLE
            val image = bindingBusinesTransaction.businessTransNotFoundImage
            image.visibility = View.VISIBLE
            val lottie = bindingBusinesTransaction.loadingBusinessTrans
            lottie.visibility = View.GONE
            lottie.pauseAnimation()
        }
    }


    private fun getDataFromServer(id: Int,token:String) {

        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(InfoAPI::class.java)
        compositeDisposable.add(retrofit.getUserInfo(token,id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse,
                { throwable -> println("MyTests4: $throwable") }
            ))
    }

    private fun handleResponse(response: UserInfoModel) {
        println("Response: "+response)
        val companyPhoto = response.photo
        if (companyPhoto == null){
            bindingBusinesTransaction.businessTransProfileImage.setImageResource(R.drawable.setpp)
        }else{
            Picasso.get().load(companyPhoto).into(bindingBusinesTransaction.businessTransProfileImage)
        }

        val userFullName = response.fullName
        sharedPreferences = requireContext().getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("companyOwnerName1",userFullName)
        editor.putString("companyPhoto1",companyPhoto)
        editor.apply()
        val businessAccountNameEditText = view.findViewById<TextInputEditText>(R.id.businessAccountNameEditText)
        if (businessAccountNameEditText != null) {
            businessAccountNameEditText.setText(userFullName)
            // Other operations with businessAccountNameEditText
        } else {
            // Handle the case where businessAccountNameEditText is null
        }
        //view.businessAccountNameEditText.setText(userFullName)

    }

}