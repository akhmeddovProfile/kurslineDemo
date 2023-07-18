package com.example.kurslinemobileapp.view.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.recreate
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
import com.example.kurslinemobileapp.model.mainpage.Highlight
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.courseFmAc.ProductDetailActivity
import com.example.kurslinemobileapp.view.loginRegister.MainRegisterActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.product_item_row.*

class HomeFragment : Fragment(),MainListProductAdapter.FavoriteItemClickListener {
    private lateinit var mainListProductAdapter: MainListProductAdapter
    private lateinit var mainList : ArrayList<GetAllAnnouncement>
    private lateinit var mainList2 : ArrayList<GetAllAnnouncement>
    private val announcements: MutableList<Announcemenet> = mutableListOf()
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var items: List<GetAllAnnouncement>
    private var isRegistered:Boolean=false
    private var isFavorite: Boolean = false
    private var userId:Int=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)
            val createAccount = view.findViewById<TextView>(R.id.createAccountTextMain)

         sharedPreferences = requireContext().getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
         userId = sharedPreferences.getInt("userID",0)
        items= listOf()
        mainList = ArrayList<GetAllAnnouncement>()
        mainList2 = ArrayList<GetAllAnnouncement>()
        val recycler = view.findViewById<RecyclerView>(R.id.allCoursesRV)
        recycler.visibility = View.GONE
        val lottie = view.findViewById<LottieAnimationView>(R.id.loadingHome)
        lottie.visibility = View.VISIBLE
        lottie.playAnimation()
        recycler.layoutManager = GridLayoutManager(requireContext(),2)
        //getProducts()

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

        val search = arguments?.getString("search", "")
        val statusId = arguments?.getString("statusId", "")
        val isOnlineId = arguments?.getString("isOnlineId", "")
        val limit = arguments?.getInt("limit", 10) ?: 10
        val offset = arguments?.getInt("offset", 0) ?: 0
        val regionId = arguments?.getString("regionId", "")
        val categoryId = arguments?.getString("categoryId", "")
        val minPrice = arguments?.getString("minPrice", "")
        val maxPrice = arguments?.getString("maxPrice", "")

        // Call the getFilterProducts method with the retrieved filter parameters
        if (regionId != null || categoryId != null || search != null || minPrice != null || maxPrice != null || statusId != null || isOnlineId != null) {
            recycler.layoutManager = GridLayoutManager(requireContext(),2)
            getFilterProducts(limit, offset, regionId!!, categoryId!!, search!!, minPrice!!, maxPrice!!, statusId!!, isOnlineId!!, 0)
        }
        else{
            recycler.layoutManager = GridLayoutManager(requireContext(),2)
            if (userId==0){
                isRegistered=false
                getProducts()
            }else{
                isRegistered=true
                getProductWhichIncludeFavorite(userId)
            }
        }

        view.filterfromAtoZ.setOnClickListener {
            showBottomSheetDialog()
        }

        view.filterforPrice.setOnClickListener {
            showBottomSheetDialogPrice()
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
        recycler.isNestedScrollingEnabled=false
        mainListProductAdapter.notifyDataSetChanged()
        mainListProductAdapter.setOnItemClickListener {
            val intent = Intent(activity, ProductDetailActivity::class.java)
            activity?.startActivity(intent)
            sharedPreferences = requireContext().getSharedPreferences(Constant.sharedkeyname,Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            sharedPreferences.edit().putInt("announcementId", it.id).apply()
            sharedPreferences.edit().putBoolean("checkIsRegistered",isRegistered).apply()
            println("Fav Item Clicked without UserID: "+isRegistered)

            println("gedenId-----"+it.id)
            editor.apply()
        }
    }

    private fun getProductWhichIncludeFavorite(id: Int) {
        compositeDisposable=CompositeDisposable()
        val retrofit=RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
        compositeDisposable.add(
            retrofit.getAnnouncementFavoriteForUserId(id).
            subscribeOn(Schedulers.io()).
            observeOn(AndroidSchedulers.mainThread()).
            subscribe(this::handleResponseforAllItemsAndFavItems,{
            })
        )
    }

    private fun handleResponseforAllItemsAndFavItems(response : GetAllAnnouncement){
        val recycler = requireView().findViewById<RecyclerView>(R.id.allCoursesRV)
        recycler.visibility = View.VISIBLE
        val lottie = requireView().findViewById<LottieAnimationView>(R.id.loadingHome)
        lottie.visibility = View.GONE
        lottie.pauseAnimation()
        announcements.addAll(response.announcemenets)
        mainList.addAll(listOf(response))
        mainList2.addAll(listOf(response))

        println("responseElanFavoriteTrue: " + response.announcemenets)
        mainListProductAdapter = MainListProductAdapter(mainList2,this@HomeFragment,requireActivity())
        recycler.adapter = mainListProductAdapter
        recycler.isNestedScrollingEnabled=false
        mainListProductAdapter.notifyDataSetChanged()
        mainListProductAdapter.setOnItemClickListener {
                        //isFavorite=!isFavorite
            isFavorite=it.isFavorite
            println("SubCategory New: "+it.subCategory)
            val intent = Intent(activity, ProductDetailActivity::class.java)
            intent.putExtra("isFavorite",isFavorite)
            activity?.startActivity(intent)
            sharedPreferences = requireContext().getSharedPreferences(Constant.sharedkeyname,Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            sharedPreferences.edit().putInt("announcementId", it.id).apply()
            sharedPreferences.edit().putBoolean("checkIsRegistered",isRegistered).apply()
            println("Item Clicked with UserID: "+isRegistered)
            println("gedenId-----"+it.id)
            editor.apply()
        }

    }

    private fun getFilterProducts(
        limit: Int,
        offset: Int,
        regionId: String,
        categoryId: String,
        search: String,
        minPrice: String,
        maxPrice: String,
        statusId: String,
        isOnlineId: String,
        userId: Int
    ) {
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
        compositeDisposable.add(retrofit.getFilterProducts(limit,offset,regionId,categoryId,search,minPrice,maxPrice,statusId,isOnlineId,userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponseFilter, { throwable-> println("MyTests: $throwable") }))
    }

    private fun handleResponseFilter(response : GetAllAnnouncement){
        mainList.clear()
        mainList2.clear()
        val recycler = requireView().findViewById<RecyclerView>(R.id.allCoursesRV)
        recycler.visibility = View.VISIBLE
        val lottie = requireView().findViewById<LottieAnimationView>(R.id.loadingHome)
        lottie.visibility = View.GONE
        lottie.pauseAnimation()
        mainList.addAll(listOf(response))
        mainList2.addAll(listOf(response))

        mainListProductAdapter = MainListProductAdapter(mainList2,this@HomeFragment,requireActivity())
        recycler.adapter = mainListProductAdapter
        mainListProductAdapter.notifyDataSetChanged()
        mainListProductAdapter.setOnItemClickListener {
            val intent = Intent(activity, ProductDetailActivity::class.java)
            activity?.startActivity(intent)
            sharedPreferences = requireContext().getSharedPreferences(Constant.sharedkeyname,Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            sharedPreferences.edit().putInt("announcementId", it.id).apply()
            sharedPreferences.edit().putBoolean("checkIsRegistered",isRegistered).apply()
           // println("Item Clicked with UserID: "+isRegistered)

            println("gedenId-----"+it.id)
            editor.apply()
        }
    }


    override fun onFavoriteItemClick(id: Int, position: Int) {
        val adapter = mainListProductAdapter as? MainListProductAdapter
        adapter!!.notifyItemChanged(position)
        adapter?.notifyDataSetChanged()
        val userId = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        val authHeader = "Bearer $token"
        println("userid" + userId)
        println("token:"+authHeader)
        if (isRegistered==false){
            Toast.makeText(requireActivity(),"Please to be Log in",Toast.LENGTH_SHORT).show()
        }
        else{
            postFav(id,position)
        }

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

    fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.sorted_layout)
        val btnAtoZ = dialog.findViewById<RelativeLayout>(R.id.rl_atoz)
        val btnZtoA = dialog.findViewById<RelativeLayout>(R.id.rl_ztoa)

        btnAtoZ?.setOnClickListener {
            mainList2.clear()
            mainList2.addAll(mainList.sortedBy { it.announcemenets.firstOrNull()?.announcementName })
            println("a to z: "+mainList2)
            mainListProductAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        btnZtoA?.setOnClickListener {
            mainList2.clear()
            mainList2.addAll(mainList.sortedByDescending {  it.announcemenets.firstOrNull()?.announcementName })
            println(mainList2)
            println(mainList)
            mainListProductAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showBottomSheetDialogPrice() {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.sorted_layout_price)
        val btnMinMax = dialog.findViewById<RelativeLayout>(R.id.rl_minmax)
        val btnmaxmin = dialog.findViewById<RelativeLayout>(R.id.rl_maxmin)

        btnMinMax?.setOnClickListener {
            mainList2.clear()
            mainList2.addAll(mainList.sortedByDescending { it.announcemenets.firstOrNull()?.price })
            println(mainList2)
            println(mainList2)
            mainListProductAdapter.notifySetChanged(mainList2)
            dialog.dismiss()
        }
        btnmaxmin?.setOnClickListener {
            mainList2.clear()
            mainList2.addAll(mainList.sortedBy {  it.announcemenets.firstOrNull()?.price })
            println(mainList2)
            println(mainList)
            mainListProductAdapter.notifySetChanged(mainList2)
            dialog.dismiss()
        }
        dialog.show()
    }


}