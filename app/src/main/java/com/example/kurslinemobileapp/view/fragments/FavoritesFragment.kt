package com.example.kurslinemobileapp.view.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.CommentAdapter
import com.example.kurslinemobileapp.adapter.FavoriteAdapter
import com.example.kurslinemobileapp.adapter.MainListProductAdapter
import com.example.kurslinemobileapp.adapter.ProductDetailImageAdapter
import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.AnnouncementDetailModel
import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.Comment
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.api.favorite.DeleteFavModel
import com.example.kurslinemobileapp.api.favorite.FavoriteApi
import com.example.kurslinemobileapp.api.favorite.favoriteGet.FavoriteGetModel
import com.example.kurslinemobileapp.api.favorite.favoriteGet.FavoriteGetModelItem
import com.example.kurslinemobileapp.api.getUserCmpDatas.companyAnnouncement.CompanyTransactionAnnouncementItem
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.courseFmAc.CourseBusinessProfile
import com.example.kurslinemobileapp.view.loginRegister.LoginActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.fragment_favorites.view.*
import java.util.Collections

class FavoritesFragment : Fragment(),FavoriteAdapter.DeleteItemFromFavorite {
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var mainList : ArrayList<FavoriteGetModelItem>
    private lateinit var mainList2 : ArrayList<FavoriteGetModelItem>
    lateinit var deleteFavModel:DeleteFavModel
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

         sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        mainList=  ArrayList<FavoriteGetModelItem>()
        mainList2=  ArrayList<FavoriteGetModelItem>()

        val isRegistered = sharedPreferences.getBoolean("token", false)
        val id = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        val authHeader = "Bearer $token"
        println("userID "+id)
        println("userToken "+authHeader)

        if (!isRegistered) {
// User is not registered, navigate to the registration fragment
            val intent = Intent(activity, LoginActivity::class.java)
            activity?.startActivity(intent)
            activity?.finish()
// User is already registered, stay on the current fragment/activity
        } else {
            // Required data is present, display it
            view.favoritesRl.visibility = View.VISIBLE
            val recycler = view.findViewById<RecyclerView>(R.id.favorites_item_recycler)
            recycler.visibility = View.GONE
            recycler.layoutManager = GridLayoutManager(requireContext(),2)
        }
        getFavItems(authHeader,id)

        return view
    }


    private fun getFavItems(token:String,id:Int){
        compositeDisposable= CompositeDisposable()
        val retrofit= RetrofitService(Constant.BASE_URL).retrofit.create(FavoriteApi::class.java)
        compositeDisposable.add(
            retrofit.getFavoritesItem(token,id).
            subscribeOn(Schedulers.io()).
            observeOn(AndroidSchedulers.mainThread()).
            subscribe(
                this::handleResponse,
                { throwable->
                    println("Error: "+ throwable)
                })
        )
    }

    private fun handleResponse(response: FavoriteGetModel) {
        if (response.isNotEmpty()) {
            val companyDetailItem = response
            val recycler = requireView().findViewById<RecyclerView>(R.id.favorites_item_recycler)
            recycler.visibility = View.VISIBLE
            val textNotfound = requireView().findViewById<TextView>(R.id.notFoundFavoritesCourseText)
            textNotfound.visibility = View.GONE
            val imageNotfound = requireView().findViewById<ImageView>(R.id.notFoundImageFav)
            imageNotfound.visibility = View.GONE

            val heartButton=requireView().findViewById<ImageButton>(R.id.favorite_button2)
            heartButton?.setBackgroundResource(R.drawable.favorite_for_product)

            mainList.addAll(companyDetailItem)
            mainList2.addAll(companyDetailItem)
            favoriteAdapter = FavoriteAdapter(mainList2,this@FavoritesFragment)
            recycler.adapter = favoriteAdapter
            favoriteAdapter.notifyDataSetChanged()
            println("responseElan: " + response)
        }else{
            val recycler = requireView().findViewById<RecyclerView>(R.id.favorites_item_recycler)
            recycler.visibility = View.GONE
            val textNotfound = requireView().findViewById<TextView>(R.id.notFoundFavoritesCourseText)
            textNotfound.visibility = View.VISIBLE
            val imageNotfound = requireView().findViewById<ImageView>(R.id.notFoundImageFav)
            imageNotfound.visibility = View.VISIBLE
        }
    }

    override fun deletefavoriteOnItemClick(id: Int,unliked:Boolean,position:Int) {

        if(id!=null){
            deleteFavModel= DeleteFavModel(id,unliked)
            val token = sharedPreferences.getString("USERTOKENNN","")
            val authHeader = "Bearer $token"
            val userId = sharedPreferences.getInt("userID",0)
            val retrofit=RetrofitService(Constant.BASE_URL).retrofit.create(FavoriteApi::class.java)
            compositeDisposable.add(
                retrofit.postFavorite(token!!,userId,deleteFavModel.productId).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe({
                    mainList2.removeAt(position)
                    favoriteAdapter.notifyItemRemoved(position)
                    favoriteAdapter.notifyItemChanged(position,mainList2.size)
                    if (mainList2.isEmpty()){
                        val recycler = requireView().findViewById<RecyclerView>(R.id.favorites_item_recycler)
                        recycler.visibility = View.GONE
                        val textNotfound = requireView().findViewById<TextView>(R.id.notFoundFavoritesCourseText)
                        textNotfound.visibility = View.VISIBLE
                        val imageNotfound = requireView().findViewById<ImageView>(R.id.notFoundImageFav)
                        imageNotfound.visibility = View.VISIBLE
                    }
                },{throwable->
                    println("My msg: ${throwable}")
                })
            )


        }else{

        }
    }


}