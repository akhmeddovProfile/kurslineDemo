package com.example.kurslinemobileapp.view.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var products: Announcemenet
    private lateinit var favList : kotlin.collections.ArrayList<Int>
    private val selectedItems: MutableList<SendFavModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)
            val createAccount = view.findViewById<TextView>(R.id.createAccountTextMain)

         sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        favList = ArrayList<Int>()
        mainList = ArrayList<GetAllAnnouncement>()
        mainList2 = ArrayList<GetAllAnnouncement>()

        val coursesRV = view.findViewById<RecyclerView>(R.id.allCoursesRV)
        coursesRV.layoutManager = GridLayoutManager(requireContext(),2)
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
        mainList.addAll(listOf(response))
        mainList2.addAll(listOf(response))
        println("responseElan: " + response)

        mainListProductAdapter = MainListProductAdapter(mainList2,this@HomeFragment,requireActivity(),selectedItems)
        val recyclerviewForProducts = requireView().findViewById<RecyclerView>(R.id.allCoursesRV)
        recyclerviewForProducts.adapter = mainListProductAdapter
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

/*
    override fun onHeartItemCLick(heart: GetAllAnnouncement, liked: Boolean,position:Int,) {

        compositeDisposable= CompositeDisposable()

        val annId = sharedPreferences.getInt("announcementId",0)
        val userId = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        val authHeader = "Bearer $token"
        println("gelenid" + annId)
        println("userid" + userId)
        println("token:"+authHeader)

        println("Clicked")
        val item=heart.announcemenets.get(position).id
        favModel= SendFavModel(item,userId,authHeader,true)

        var likeState = liked
        println("likeState: $liked")
        if (likeState){
            val retrofit=RetrofitService(Constant.BASE_URL).retrofit.create(FavoriteApi::class.java)
            compositeDisposable.add(
                retrofit.postFavorite(favModel.token,favModel.userId,favModel.productid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                            println("Handler Response: "+it.isSuccess)
                               likeState=true
                               },{ throwable ->
                        println("MyTests: $throwable")
                    })
            )
        }
        else{

        }
    }
*/


    override fun onFavoriteItemClick(item: SendFavModel) {
        val annId = sharedPreferences.getInt("announcementId",0)
        val userId = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        val authHeader = "Bearer $token"
        println("gelenid: " + annId)
        println("userid: " + userId)
        println("token: "+authHeader)
        // Update the heart drawable in the adapter
        val adapter = mainListProductAdapter as? MainListProductAdapter
        adapter?.notifyDataSetChanged()

        // Add or remove the item from the selectedItems list
        if (item.isSelected) {
            selectedItems.add(item)
        } else {
            selectedItems.remove(item)
        }
        compositeDisposable= CompositeDisposable()

        println("SelectedItems:"+ selectedItems)

        var sendfavitem=SendFavModel(annId,userId)
        val retrofit=RetrofitService(Constant.BASE_URL).retrofit.create(FavoriteApi::class.java)
        compositeDisposable.add(
            retrofit.postFavorite(token!!,sendfavitem.userid,sendfavitem.productid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({response->
                    if(response.isSuccess){
                        Log.d(TAG, "Favorite item sent successfully: ${response.isSuccess}")
                    }else{
                        Log.e(TAG, "Failed to send favorite item: ${response.isSuccess}")
                    }
                },{ throwable ->
                    println("MyTests: $throwable")
                })
        )

    }
}