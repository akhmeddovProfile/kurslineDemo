package com.example.kurslinemobileapp.view.fragments

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.FavoriteAdapter
import com.example.kurslinemobileapp.api.favorite.FavoriteApi
import com.example.kurslinemobileapp.api.favorite.favoriteGet.FavoriteGetModel
import com.example.kurslinemobileapp.api.favorite.favoriteGet.FavoriteGetModelItem
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.courseFmAc.ProductDetailActivity
import com.example.kurslinemobileapp.view.loginRegister.LoginActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_favorites.view.*

class FavoritesFragment : Fragment(),FavoriteAdapter.DeleteItemFromFavorite {
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var mainList : ArrayList<FavoriteGetModelItem>
    private lateinit var mainList2 : ArrayList<FavoriteGetModelItem>
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var sharedPreferences: SharedPreferences
    private var isFavorite: Boolean = false

    @SuppressLint("MissingInflatedId")
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
            val lottie = view.findViewById<LottieAnimationView>(R.id.favoriteLoading)
            lottie.visibility = View.VISIBLE
            val text = view.findViewById<TextView>(R.id.notFoundFavoritesCourseText)
            text.visibility = View.GONE
            val image = view.findViewById<ImageView>(R.id.notFoundImageFav)
            image.visibility = View.GONE
            lottie.playAnimation()
            val recycler = view.findViewById<RecyclerView>(R.id.favorites_item_recycler)
            recycler.visibility = View.GONE
            val coursesRV = view.findViewById<RecyclerView>(R.id.favorites_item_recycler)
            coursesRV.layoutManager = GridLayoutManager(requireContext(), 2)
            getFavItems(authHeader,id)
        }


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
            val checkFavoritesForDetail:Boolean=true
            val companyDetailItem = response
            val recycler = requireView().findViewById<RecyclerView>(R.id.favorites_item_recycler)
            recycler.isNestedScrollingEnabled=false
            recycler.visibility = View.VISIBLE
            val textNotfound = requireView().findViewById<TextView>(R.id.notFoundFavoritesCourseText)
            textNotfound.visibility = View.GONE
            val imageNotfound = requireView().findViewById<ImageView>(R.id.notFoundImageFav)
            imageNotfound.visibility = View.GONE

            val lottie = requireView().findViewById<LottieAnimationView>(R.id.favoriteLoading)
            lottie.visibility = View.GONE
            lottie.pauseAnimation()
            val heartButton=requireView().findViewById<ImageButton>(R.id.favorite_button2)
            heartButton?.setBackgroundResource(R.drawable.favorite_for_product)
            mainList.addAll(companyDetailItem)
            mainList2.addAll(companyDetailItem)
            favoriteAdapter = FavoriteAdapter(mainList2,this@FavoritesFragment)
            recycler.adapter = favoriteAdapter
            favoriteAdapter.notifyDataSetChanged()
            println("responseElan: " + response)
            favoriteAdapter.setOnItemClickListener {
                isFavorite=it.isFavorite
                val intent = Intent(activity, ProductDetailActivity::class.java)
                intent.putExtra("isFavoriteFromFavoriteFragment",isFavorite)
                activity?.startActivity(intent)
                sharedPreferences = requireContext().getSharedPreferences("MyPrefs",Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                sharedPreferences.edit().putInt("announcementId", it.id).apply()
                sharedPreferences.edit().putBoolean("isFavoriteDetailProduct",checkFavoritesForDetail).apply()
                println("gedenId-----"+it.id)
                editor.apply()
            }

        }else{

            val recycler = requireView().findViewById<RecyclerView>(R.id.favorites_item_recycler)
            recycler.visibility = View.GONE
            val textNotfound = requireView().findViewById<TextView>(R.id.notFoundFavoritesCourseText)
            textNotfound.visibility = View.VISIBLE
            val imageNotfound = requireView().findViewById<ImageView>(R.id.notFoundImageFav)
            imageNotfound.visibility = View.VISIBLE
            val lottie = requireView().findViewById<LottieAnimationView>(R.id.favoriteLoading)
            lottie.visibility = View.GONE
            lottie.pauseAnimation()
        }
    }

    override fun deletefavoriteOnItemClick(id: Int,position:Int) {

        if(id!=null){
            val token = sharedPreferences.getString("USERTOKENNN","")
            val authHeader = "Bearer $token"
            val userId = sharedPreferences.getInt("userID",0)
            val retrofit=RetrofitService(Constant.BASE_URL).retrofit.create(FavoriteApi::class.java)
            compositeDisposable.add(
                retrofit.postFavorite(authHeader,userId,id).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe({
                    mainList2[0].isFavorite=it.isSuccess
                    favoriteAdapter.deleteItems(mainList2,position)
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
                    println("Deleted From Server: "+it.isSuccess)

                },{throwable->
                    println("My msg: ${throwable}")
                })
            )


        }else{

        }
    }


}