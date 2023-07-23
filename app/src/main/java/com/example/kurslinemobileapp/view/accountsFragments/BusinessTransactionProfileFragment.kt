package com.example.kurslinemobileapp.view.accountsFragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.CompanyTransactionAdapter
import com.example.kurslinemobileapp.adapter.CourseBusinessProfileAdapter
import com.example.kurslinemobileapp.adapter.MainListProductAdapter
import com.example.kurslinemobileapp.adapter.ResizeTransformation
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.api.companyTeachers.companyProfile.Announcement
import com.example.kurslinemobileapp.api.getUserCmpDatas.InfoAPI
import com.example.kurslinemobileapp.api.getUserCmpDatas.UserCmpInfoModel.UserInfoModel
import com.example.kurslinemobileapp.api.getUserCmpDatas.companyAnnouncement.CompanyTransactionAnnouncement
import com.example.kurslinemobileapp.api.getUserCmpDatas.companyAnnouncement.CompanyTransactionAnnouncementItem
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.courseFmAc.ProductDetailActivity
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_company_update.*
import kotlinx.android.synthetic.main.fragment_business_account.view.*
import kotlinx.android.synthetic.main.fragment_business_transactions_profile.view.*

class BusinessTransactionProfileFragment : Fragment() {
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var companyTransactionAdapter: CompanyTransactionAdapter
    private lateinit var view: ViewGroup
    private lateinit var mainList: ArrayList<CompanyTransactionAnnouncementItem>
    private lateinit var mainList2: ArrayList<CompanyTransactionAnnouncementItem>
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(
            R.layout.fragment_business_transactions_profile,
            container,
            false
        ) as ViewGroup

        sharedPreferences = requireContext().getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        val id = sharedPreferences.getInt("userID", 0)
        val token = sharedPreferences.getString("USERTOKENNN", "")
        val authHeader = "Bearer $token"
getDataFromServer(id,authHeader)
        val userFullName = sharedPreferences.getString("companyOwnerName1","")?:""
        val userPhoto = sharedPreferences.getString("companyPhoto1","")?:""
        view.businessTransName.setText(userFullName)
        if (userPhoto.isEmpty()){
            view.businessTransProfileImage.setImageResource(R.drawable.setpp)
        }else {
            Picasso.get().load(userPhoto).into(view.businessTransProfileImage)
        }
        button1 = view.findViewById(R.id.button1BusinessTrans)
        button2 = view.findViewById(R.id.button2BusinessTrans)
        button3 = view.findViewById(R.id.button3BusinessTrans)
        button4 = view.findViewById(R.id.button4BusinessTrans)

        updateButtonBackgrounds(button1)
        val lottie = view.findViewById<LottieAnimationView>(R.id.loadingBusinessTrans)
        lottie.visibility = View.VISIBLE
        val text = view.findViewById<TextView>(R.id.notFoundBusinessTransCourseText)
        text.visibility = View.GONE
        val recycler = view.findViewById<RecyclerView>(R.id.businessTransRv)
        recycler.visibility = View.GONE
        val image = view.findViewById<ImageView>(R.id.businessTransNotFoundImage)
        image.visibility = View.GONE
        lottie.playAnimation()
        val coursesRV = view.findViewById<RecyclerView>(R.id.businessTransRv)
        coursesRV.layoutManager = GridLayoutManager(requireContext(), 2)
        getWaitingAnnouncements(id, authHeader)

        val goButton = view.findViewById<ImageView>(R.id.goToBusinessAccountFragment)
        goButton.setOnClickListener {
            println("1")
            val fragmentManager = requireFragmentManager()
            // Start a fragment transaction
            val transaction = fragmentManager.beginTransaction()
            // Replace the first fragment with the second fragment
            transaction.replace(R.id.transactionProfileRl, BusinessAccountFragment())
            transaction.setReorderingAllowed(true)
            // Add the transaction to the back stack
            transaction.addToBackStack(null)
            // Commit the transaction
            transaction.commit()
        }

        mainList = ArrayList<CompanyTransactionAnnouncementItem>()
        mainList2 = ArrayList<CompanyTransactionAnnouncementItem>()

        button1.setOnClickListener {
            updateButtonBackgrounds(button1)
            val lottie = view.findViewById<LottieAnimationView>(R.id.loadingBusinessTrans)
            lottie.visibility = View.VISIBLE
            val text = view.findViewById<TextView>(R.id.notFoundBusinessTransCourseText)
            text.visibility = View.GONE
            val image = view.findViewById<ImageView>(R.id.businessTransNotFoundImage)
            image.visibility = View.GONE
            lottie.playAnimation()
            val recycler = view.findViewById<RecyclerView>(R.id.businessTransRv)
            recycler.visibility = View.GONE
            val coursesRV = view.findViewById<RecyclerView>(R.id.businessTransRv)
            coursesRV.layoutManager = GridLayoutManager(requireContext(), 2)
            getWaitingAnnouncements(id, authHeader)
        }
        button2.setOnClickListener {
            updateButtonBackgrounds(button2)
            val lottie = view.findViewById<LottieAnimationView>(R.id.loadingBusinessTrans)
            lottie.visibility = View.VISIBLE
            val text = view.findViewById<TextView>(R.id.notFoundBusinessTransCourseText)
            text.visibility = View.GONE
            val image = view.findViewById<ImageView>(R.id.businessTransNotFoundImage)
            image.visibility = View.GONE
            lottie.playAnimation()
            val recycler = view.findViewById<RecyclerView>(R.id.businessTransRv)
            recycler.visibility = View.GONE
            val coursesRV = view.findViewById<RecyclerView>(R.id.businessTransRv)
            coursesRV.layoutManager = GridLayoutManager(requireContext(), 2)
            getActiveAnnouncements(id, authHeader)
        }
        button3.setOnClickListener {
            updateButtonBackgrounds(button3)
            val lottie = view.findViewById<LottieAnimationView>(R.id.loadingBusinessTrans)
            lottie.visibility = View.VISIBLE
            val text = view.findViewById<TextView>(R.id.notFoundBusinessTransCourseText)
            text.visibility = View.GONE
            val image = view.findViewById<ImageView>(R.id.businessTransNotFoundImage)
            image.visibility = View.GONE
            lottie.playAnimation()
            val recycler = view.findViewById<RecyclerView>(R.id.businessTransRv)
            recycler.visibility = View.GONE
            val coursesRV = view.findViewById<RecyclerView>(R.id.businessTransRv)
            coursesRV.layoutManager = GridLayoutManager(requireContext(), 2)
            getDeactiveAnnouncements(id, authHeader)
        }
        button4.setOnClickListener {
            updateButtonBackgrounds(button4)
            val lottie = view.findViewById<LottieAnimationView>(R.id.loadingBusinessTrans)
            lottie.visibility = View.VISIBLE
            val text = view.findViewById<TextView>(R.id.notFoundBusinessTransCourseText)
            text.visibility = View.GONE
            val image = view.findViewById<ImageView>(R.id.businessTransNotFoundImage)
            image.visibility = View.GONE
            lottie.playAnimation()
            val recycler = view.findViewById<RecyclerView>(R.id.businessTransRv)
            recycler.visibility = View.GONE
            val coursesRV = view.findViewById<RecyclerView>(R.id.businessTransRv)
            coursesRV.layoutManager = GridLayoutManager(requireContext(), 2)
            getWaitingAnnouncements(id, authHeader)
        }
        return view
    }

    @SuppressLint("ResourceAsColor")
    private fun updateButtonBackgrounds(selectedButton: Button) {
        val buttons = arrayOf(button1, button2, button3, button4)

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
                { throwable -> println("MyTests: $throwable") }
            ))
    }

    private fun handleResponseActive(response: CompanyTransactionAnnouncement) {
        mainList.clear()
        mainList2.clear()
        if (response.isNotEmpty()) {
            val companyDetailItem = response
            mainList.addAll(companyDetailItem)
            mainList2.addAll(companyDetailItem)
            val recycler = view.findViewById<RecyclerView>(R.id.businessTransRv)
            recycler.visibility = View.VISIBLE
            val text = view.findViewById<TextView>(R.id.notFoundBusinessTransCourseText)
            text.visibility = View.GONE
            val image = view.findViewById<ImageView>(R.id.businessTransNotFoundImage)
            image.visibility = View.GONE
            val lottie = view.findViewById<LottieAnimationView>(R.id.loadingBusinessTrans)
            lottie.visibility = View.GONE
            lottie.pauseAnimation()
            println("responseElan: " + response)
            companyTransactionAdapter = CompanyTransactionAdapter(mainList2)
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
            val recycler = view.findViewById<RecyclerView>(R.id.businessTransRv)
            recycler.visibility = View.GONE
            val text = view.findViewById<TextView>(R.id.notFoundBusinessTransCourseText)
            text.visibility = View.VISIBLE
            val image = view.findViewById<ImageView>(R.id.businessTransNotFoundImage)
            image.visibility = View.VISIBLE
            val lottie = view.findViewById<LottieAnimationView>(R.id.loadingBusinessTrans)
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
                { throwable -> println("MyTests: $throwable") }
            ))
    }

    private fun handleResponseDeactive(response: CompanyTransactionAnnouncement) {
        mainList.clear()
        mainList2.clear()
        if (response.isNotEmpty()) {
            val companyDetailItem = response
            mainList.addAll(companyDetailItem)
            mainList2.addAll(companyDetailItem)
            val recycler = view.findViewById<RecyclerView>(R.id.businessTransRv)
            recycler.visibility = View.VISIBLE
            val text = view.findViewById<TextView>(R.id.notFoundBusinessTransCourseText)
            text.visibility = View.GONE
            val image = view.findViewById<ImageView>(R.id.businessTransNotFoundImage)
            image.visibility = View.GONE
            val lottie = view.findViewById<LottieAnimationView>(R.id.loadingBusinessTrans)
            lottie.visibility = View.GONE
            lottie.pauseAnimation()
            println("responseElan: " + response)
            companyTransactionAdapter = CompanyTransactionAdapter(mainList2)
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
            val recycler = view.findViewById<RecyclerView>(R.id.businessTransRv)
            recycler.visibility = View.GONE
            val text = view.findViewById<TextView>(R.id.notFoundBusinessTransCourseText)
            text.visibility = View.VISIBLE
            val image = view.findViewById<ImageView>(R.id.businessTransNotFoundImage)
            image.visibility = View.VISIBLE
            val lottie = view.findViewById<LottieAnimationView>(R.id.loadingBusinessTrans)
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
                { throwable -> println("MyTests: $throwable") }
            ))
    }

    private fun handleResponseWait(response: CompanyTransactionAnnouncement) {
        mainList.clear()
        mainList2.clear()
        if (response.isNotEmpty()) {
            val companyDetailItem = response
            mainList.addAll(companyDetailItem)
            mainList2.addAll(companyDetailItem)
            val recycler = view.findViewById<RecyclerView>(R.id.businessTransRv)
            recycler.visibility = View.VISIBLE
            val text = view.findViewById<TextView>(R.id.notFoundBusinessTransCourseText)
            text.visibility = View.GONE
            val image = view.findViewById<ImageView>(R.id.businessTransNotFoundImage)
            image.visibility = View.GONE
            val lottie = view.findViewById<LottieAnimationView>(R.id.loadingBusinessTrans)
            lottie.visibility = View.GONE
            lottie.pauseAnimation()
            println("responseElan: " + response)
            companyTransactionAdapter = CompanyTransactionAdapter(mainList2)
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
            val recycler = view.findViewById<RecyclerView>(R.id.businessTransRv)
            recycler.visibility = View.GONE
            val text = view.findViewById<TextView>(R.id.notFoundBusinessTransCourseText)
            text.visibility = View.VISIBLE
            val image = view.findViewById<ImageView>(R.id.businessTransNotFoundImage)
            image.visibility = View.VISIBLE
            val lottie = view.findViewById<LottieAnimationView>(R.id.loadingBusinessTrans)
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
                { throwable -> println("MyTests: $throwable") }
            ))
    }

    private fun handleResponse(response: UserInfoModel) {

        val companyPhoto = response.photo
        if (companyPhoto == null){
            view.myBusinessImage.setImageResource(R.drawable.setpp)
        }else{
            Picasso.get().load(companyPhoto).into(view.businessTransProfileImage)
        }

        val userFullName = response.fullName
        sharedPreferences = requireContext().getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("companyOwnerName1",userFullName)
        editor.putString("companyPhoto1",companyPhoto)
        editor.apply()
        view.businessAccountNameEditText.setText(userFullName)

    }

}