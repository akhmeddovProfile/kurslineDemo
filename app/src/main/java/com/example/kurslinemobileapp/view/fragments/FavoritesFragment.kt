package com.example.kurslinemobileapp.view.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.favorite.FavoriteApi
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.loginRegister.LoginActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_favorites.view.*

class FavoritesFragment : Fragment() {
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

         sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
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
            subscribe({
                println("Success: "+ it.id)
            },
                { throwable->
                    println("Error: "+ throwable)
                })
        )
    }
}