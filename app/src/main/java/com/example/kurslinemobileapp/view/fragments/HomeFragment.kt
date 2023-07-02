package com.example.kurslinemobileapp.view.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.HiglightForMainListAdapter
import com.example.kurslinemobileapp.adapter.MainListProductAdapter
import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Announcemenet
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.api.favorite.FavoriteApi
import com.example.kurslinemobileapp.api.favorite.SendFavModel
import com.example.kurslinemobileapp.model.mainpage.Highlight
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.courseFmAc.ProductDetailActivity
import com.example.kurslinemobileapp.view.loginRegister.MainRegisterActivity
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.activity_register_company.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment(),MainListProductAdapter.FavoriteItemClickListener {
    private lateinit var mainListProductAdapter: MainListProductAdapter
    private lateinit var mainList : ArrayList<GetAllAnnouncement>
    private lateinit var mainList2 : ArrayList<GetAllAnnouncement>
    private val announcements: MutableList<Announcemenet> = mutableListOf()
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var favListId:MutableList<Int>

    private lateinit var favList : kotlin.collections.MutableList<SendFavModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)
            val createAccount = view.findViewById<TextView>(R.id.createAccountTextMain)

         sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        favListId= mutableListOf()
        favList = mutableListOf()
        mainList = ArrayList<GetAllAnnouncement>()
        mainList2 = ArrayList<GetAllAnnouncement>()
        val recycler = view.findViewById<RecyclerView>(R.id.allCoursesRV)
        recycler.visibility = View.GONE
        val lottie = view.findViewById<LottieAnimationView>(R.id.loadingHome)
        lottie.visibility = View.VISIBLE
        lottie.playAnimation()
        recycler.layoutManager = GridLayoutManager(requireContext(),2)
        getProducts()

        val imageWithTextList = listOf(
            Highlight(R.drawable.mainpagehiglight, "Ən çox baxılanlar"),
            Highlight(R.drawable.yenielan, "1345 yeni kurs"),
            Highlight(R.drawable.vip, "234 VIP kurs")
        )
        val recylerviewForHighlight =
            view.findViewById<RecyclerView>(R.id.topProductsRV)
        val adapter = HiglightForMainListAdapter(imageWithTextList)
        recylerviewForHighlight.adapter = adapter
        recylerviewForHighlight.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        createAccount.setOnClickListener {
            val intent = Intent(activity, MainRegisterActivity::class.java)
            activity?.startActivity(intent)
        }

        val goToFilter = view.findViewById<TextInputEditText>(R.id.mainFilterEditText)
        goToFilter.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_filterFragment)
        }


        val userType = sharedPreferences.getString("userType",null)
        if (userType == "İstifadəçi" || userType == "Kurs" || userType == "Repititor") {
            view.createAccountTextMain.visibility = View.GONE
        }
        else{
            view.createAccountTextMain.visibility = View.VISIBLE
        }

        return view
    }

    private fun getProducts(){
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
        compositeDisposable.add(retrofit.getAnnouncement()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse, { throwable-> println("MyTests: $throwable") }))
    }


    private fun handleResponse(response : GetAllAnnouncement){
        val recycler = requireView().findViewById<RecyclerView>(R.id.allCoursesRV)
        recycler.visibility = View.VISIBLE
        val lottie = requireView().findViewById<LottieAnimationView>(R.id.loadingHome)
        lottie.visibility = View.GONE
        lottie.pauseAnimation()
        announcements.addAll(response.announcemenets)
        mainList.addAll(listOf(response))
        mainList2.addAll(listOf(response))
        println("responseElan: " + response.announcemenets)

        mainListProductAdapter = MainListProductAdapter(mainList2,this@HomeFragment,requireActivity())
        recycler.adapter = mainListProductAdapter
        mainListProductAdapter.notifyDataSetChanged()
        mainListProductAdapter.setOnItemClickListener {
            val intent = Intent(activity, ProductDetailActivity::class.java)
            activity?.startActivity(intent)
            sharedPreferences = requireContext().getSharedPreferences("MyPrefs",Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            sharedPreferences.edit().putInt("announcementId", it.id).apply()
            println("gedenId-----"+it.id)
            editor.apply()
        }
    }


    override fun onFavoriteItemClick(id: Int, position: Int) {
        compositeDisposable= CompositeDisposable()
        val adapter = mainListProductAdapter as? MainListProductAdapter
        adapter!!.notifyItemChanged(position)
        adapter?.notifyDataSetChanged()
        val userId = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        val authHeader = "Bearer $token"
        println("userid" + userId)
        println("token:"+authHeader)
         postFav(id,position)

        /*else deleteFav(id)*/

    }

    fun postFav(id:Int,position: Int){
        val token = sharedPreferences.getString("USERTOKENNN","")
        val userId = sharedPreferences.getInt("userID",0)
        val retrofit=RetrofitService(Constant.BASE_URL).retrofit.create(FavoriteApi::class.java)
        compositeDisposable.add(
            retrofit.postFavorite(token!!,userId,id).
            subscribeOn(Schedulers.io()).
            observeOn(AndroidSchedulers.mainThread()).
            subscribe({
                      println(it.isSuccess)
                mainList2[0].announcemenets[position].isFavorite=it.isSuccess
                mainListProductAdapter.LikedItems(mainList2,position)

            },{throwable->
                println("My msg: ${throwable}")
            })
        )
    }


}