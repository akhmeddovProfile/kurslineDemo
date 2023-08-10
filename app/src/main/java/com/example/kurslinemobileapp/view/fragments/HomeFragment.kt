package com.example.kurslinemobileapp.view.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.HiglightForMainListAdapter
import com.example.kurslinemobileapp.adapter.MainListProductAdapter
import com.example.kurslinemobileapp.adapter.ViewPagerImageAdapter
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
import androidx.appcompat.widget.SearchView


class HomeFragment : Fragment(),MainListProductAdapter.FavoriteItemClickListener, SearchView.OnQueryTextListener{
    private lateinit var view: ViewGroup
    private lateinit var viewPager2: ViewPager2
    private lateinit var handler : Handler
    private lateinit var imageList:ArrayList<Int>
    private lateinit var viewPagerImageAdapter: ViewPagerImageAdapter
    private lateinit var mainListProductAdapter: MainListProductAdapter
    private lateinit var mainList : ArrayList<GetAllAnnouncement>
    private lateinit var mainList2 : ArrayList<GetAllAnnouncement>
    private val announcements: MutableList<Announcemenet> = mutableListOf()
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var items: List<GetAllAnnouncement>
    private var isRegistered:Boolean=false
    private var isFavorite: Boolean = false
    private var token:String=""
    private var userId:Int=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         view =  inflater.inflate(R.layout.fragment_home, container, false) as ViewGroup
            val createAccount = view.findViewById<ImageView>(R.id.createAccountTextMain)

         sharedPreferences = requireContext().getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
         userId = sharedPreferences.getInt("userID",0)
        token= sharedPreferences.getString("USERTOKENNN","")!!
        println("Clear token: "+token)
        println("Clear: "+userId)
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
            Highlight(R.drawable.mainpage2, "Ən çox baxılanlar"),
            Highlight(R.drawable.yenielan2, "1345 yeni kurs"),
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
            view.createAccountTextMain.visibility = View.VISIBLE
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


        val searchView = view.findViewById<SearchView>(R.id.searchViewAnnEditText)
        val searchEditText = searchView?.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

        // Check if the searchEditText is not null before setting the text color
        searchEditText?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        searchEditText?.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.grayColor2))
        searchView.setOnQueryTextListener(this)


        init()
        setUpTransformer()
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable , 4000)
            }
        })


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
            intent.putExtra("subCategory",it.subCategory)
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



    override fun onPause() {
        super.onPause()

        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()

        handler.postDelayed(runnable , 4000)
    }

    private val runnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem + 1
    }

    private fun setUpTransformer(){
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + r * 0.14f
        }

        viewPager2.setPageTransformer(transformer)
    }

    private fun init(){
        viewPager2 = view.findViewById(R.id.viewPager2)
        handler = Handler(Looper.myLooper()!!)
        imageList = ArrayList()

        imageList.add(R.drawable.mainpage2)
        imageList.add(R.drawable.yenielan2)
        imageList.add(R.drawable.vip)
        viewPagerImageAdapter = ViewPagerImageAdapter(imageList, viewPager2)

        viewPager2.adapter = viewPagerImageAdapter
        viewPager2.offscreenPageLimit = 3
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(msg: String): Boolean {
        if (::mainListProductAdapter.isInitialized) {
            mainListProductAdapter.getFilter().filter(msg)
        }
        return false
    }

}