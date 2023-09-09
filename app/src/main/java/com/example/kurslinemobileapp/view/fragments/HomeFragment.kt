package com.example.kurslinemobileapp.view.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SearchView.OnQueryTextListener
import androidx.annotation.RequiresApi
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
import androidx.core.view.doOnLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.amar.library.ui.StickyScrollView
import com.example.kurslinemobileapp.adapter.*
import com.example.kurslinemobileapp.api.ad.AdAPI
import com.example.kurslinemobileapp.api.announcement.higlightProduct.HiglightProductModelItem
import com.example.kurslinemobileapp.api.getUserCmpDatas.companyAnnouncement.CompanyTransactionAnnouncementItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(), MainListProductAdapter.FavoriteItemClickListener,VIPAdapter.VIPFavoriteItemClickListener,
    SearchView.OnQueryTextListener, HiglightForMainListAdapter.OnHighlightItemClickListener{
    private lateinit var view: ViewGroup
    private lateinit var higlightProducAdapter: HiglighProducAdapter

    private lateinit var viewPager2: ViewPager2
    private lateinit var handler: Handler
    private lateinit var imageList: ArrayList<Int>
    private lateinit var viewPagerImageAdapter: ViewPagerImageAdapter
    private lateinit var mainListProductAdapter: MainListProductAdapter
    private lateinit var vipAdapter: VIPAdapter
    private lateinit var mainList: ArrayList<Announcemenet>
    private lateinit var mainList2: ArrayList<Announcemenet>
    private lateinit var mainList2High: ArrayList<HiglightProductModelItem>
    private lateinit var vipList: ArrayList<Announcemenet>
    private val announcements: MutableList<Announcemenet> = mutableListOf()
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var sharedPreferences: SharedPreferences
    private var isRegistered: Boolean = false
    private var isFavorite: Boolean = false
    private var token: String = ""
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var userId: Int = 0
    private var currentOffset = 0
    lateinit var vipRv: RecyclerView
    lateinit var recycler: RecyclerView
    lateinit var highRv: RecyclerView
    private val PAGE_SIZE = 5
    private var isLoading = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false) as ViewGroup
        val createAccount = view.findViewById<ImageView>(R.id.writeus)

       // viewModel = ViewModelProvider(this).get(ViewModelPagination::class.java)
/*
        handleResponsePagination()
*/

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        val goToUploadActivity=requireActivity().findViewById<FloatingActionButton>(R.id.goToUploadActivity)
        // Hide the BottomNavigationView
        goToUploadActivity.visibility=View.VISIBLE
        bottomNavigationView.visibility = View.VISIBLE
        sharedPreferences =
            requireContext().getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("userID", 0)
        token = sharedPreferences.getString("USERTOKENNN", "")!!
        println("Clear token: " + token)
        println("Clear: " + userId)
        mainList = ArrayList<Announcemenet>()
        mainList2 = ArrayList<Announcemenet>()
        mainList2High = ArrayList<HiglightProductModelItem>()
        vipList = ArrayList<Announcemenet>()

        recycler = view.findViewById<RecyclerView>(R.id.allCoursesRV)
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recycler.visibility = View.GONE

        highRv = view.findViewById<RecyclerView>(R.id.higlightCoursesRV)
        highRv.visibility = View.GONE
        vipRv = view.findViewById<RecyclerView>(R.id.vipCoursesRV)
        vipRv.visibility = View.GONE
        val lottie = view.findViewById<LottieAnimationView>(R.id.loadingHome)
        lottie.visibility = View.VISIBLE
        lottie.playAnimation()
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)
        vipRv.layoutManager = GridLayoutManager(requireContext(), 2)
        highRv.layoutManager = GridLayoutManager(requireContext(),2)
        //getProducts()


        val imageWithTextListJson = sharedPreferences.getString("imageWithTextList", null)

// Check if the JSON string is not null
        if (imageWithTextListJson != null) {
            // Convert the JSON string back to a list
            val gson = Gson()
            val type = object : TypeToken<List<Highlight>>() {}.type
            val imageWithTextList = gson.fromJson<List<Highlight>>(imageWithTextListJson, type)

            val recylerviewForHighlight = view.findViewById<RecyclerView>(R.id.topProductsRV)
            val adapter = HiglightForMainListAdapter(imageWithTextList,this@HomeFragment)
            recylerviewForHighlight.adapter = adapter
            recylerviewForHighlight.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            // Now, you have the imageWithTextList in this activity
        }

        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
        compositeDisposable.add(
            retrofit.getHighlight().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ highlightModel ->
                    // The data has been successfully fetched from the server
                    // Now, you can update your imageWithTextList
                    val imageWithTextList = listOf(
                        Highlight(R.drawable.mainpage2,"Ən çox baxılanlar ${highlightModel[0].mostView}" ),
                        Highlight(R.drawable.yenielan2,"${highlightModel[0].newCourse} yeni kurs"),
                        Highlight(R.drawable.vip, "${highlightModel[0].vip} VIP kurs")
                    )
                    val recylerviewForHighlight = view.findViewById<RecyclerView>(R.id.topProductsRV)
                    val adapter = HiglightForMainListAdapter(imageWithTextList,this@HomeFragment)
                    recylerviewForHighlight.adapter = adapter
                    recylerviewForHighlight.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

// Save the JSON string in SharedPreferences

                    // Use the updated imageWithTextList for your UI
                }, { error ->
                    // Handle the error if the request fails
                })
        )




     //   viewModel.loadMoreData()
        val nestedScrollView = view.findViewById<NestedScrollView>(R.id.nestedScrollHome)
/*        viewModel = ViewModelProvider(this).get(ViewModelPagination::class.java)
        nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) { // Scrolling downwards
                val contentHeight = nestedScrollView.getChildAt(0).height
                val scrollViewHeight = nestedScrollView.height
                val scrolledDistance = scrollY + scrollViewHeight
                if (scrolledDistance >= contentHeight) {
                    // Load more data when scrolled to the bottom
                    viewModel.loadMoreData()
                }
            }
        }*/

        createAccount.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_contactUsFragment)
        }

        val goToFilter = view.findViewById<TextInputEditText>(R.id.mainFilterEditText)
        goToFilter.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_filterFragment)
        }


        val userType = sharedPreferences.getString("userType", null)
        if (userType == "İstifadəçi" || userType == "Kurs" || userType == "Repititor") {
            view.writeus.visibility = View.VISIBLE
        } else {
            view.writeus.visibility = View.VISIBLE
        }
        //setscroollListenerGuest()

        val search = arguments?.getString("search", "")
        val statusId = arguments?.getString("statusId", "")
        val isOnlineId = arguments?.getString("isOnlineId", "")
        val limit = arguments?.getInt("limit", 10) ?: 10
        val offset = arguments?.getInt("offset", 0) ?: 0
        val regionId = arguments?.getString("regionId", "")
        val categoryId = arguments?.getString("categoryId", "")
        val minPrice = arguments?.getString("minPrice", "")
        val maxPrice = arguments?.getString("maxPrice", "")
        val course = arguments?.getString("courseId", "")

        // Call the getFilterProducts method with the retrieved filter parameters
        if (regionId != null || categoryId != null || search != null || minPrice != null || maxPrice != null || statusId != null || isOnlineId != null || course != null) {
            recycler.layoutManager = GridLayoutManager(requireContext(), 2)
            getFilterProducts(
                limit,
                offset,
                regionId!!,
                categoryId!!,
                search!!,
                minPrice!!,
                maxPrice!!,
                statusId!!,
                isOnlineId!!,
                course!!,
                0
            )
        } else {
            recycler.layoutManager = GridLayoutManager(requireContext(), 2)
            if (userId == 0) {
                isRegistered = false
                /*setupScrollListener(nestedScrollView)*/
                /* viewModel.loadMoreData()
                 handleResponsePagination()*/
                getProductWhichIncludeFavorite(userId)
                // handleResponsePagination()
                //getProductsAndSetupScrollListener(0)
            } else {
                isRegistered = true
                getProductWhichIncludeFavorite(userId)
            }
        }
//elanlar gorurem istesen emulatorda run ver ram yaxsidir komo ses exo verir google meet baglayaq istesen wpdan zeng vurum

        val searchView = view.findViewById<SearchView>(R.id.searchViewAnnEditText)
        val searchEditText =
            searchView?.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

        // Check if the searchEditText is not null before setting the text color
        searchEditText?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        searchEditText?.setHintTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.grayColor2
            )
        )
        searchView.setOnQueryTextListener(this)


        init()
        setUpTransformer()
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 4000)
            }
        })


        return view
    }

/*    private fun getProductsAndSetupScrollListener(offset: Int) {
        getProducts(offset) // Call getProducts() with the provided offset
    }
    private fun getProducts(offset: Int){
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
        compositeDisposable.add(retrofit.getAnnouncement(limit = PAGE_SIZE, offset)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({response->
                handleResponse(response)
                isLoading=false
                }, { throwable-> println("MyTests: $throwable")
                isLoading=false
                }))
    }*/



/*
    private fun handleResponsePagination() {
        viewModel.newAnnouncements.observe(viewLifecycleOwner) { announcementspagination ->
            val recycler = requireView().findViewById<RecyclerView>(R.id.allCoursesRV)
            recycler.visibility = View.VISIBLE
            val vipRv = view.findViewById<RecyclerView>(R.id.vipCoursesRV)
            vipRv.visibility = View.VISIBLE
            val lottie = requireView().findViewById<LottieAnimationView>(R.id.loadingHome)
            lottie.visibility = View.GONE
            lottie.pauseAnimation()
            announcements.addAll(announcementspagination)

            Log.d("MyTag","${announcements.size}")
            //mainList.addAll(listOf(response))
            //mainList2.addAll(listOf(response))
            for (newList in announcementspagination) {
                if (newList.isVIP) {
                    vipList.add(newList)
                } else {
                    mainList2.add(newList)
                }
            }
            mainListProductAdapter =
                MainListProductAdapter(mainList2, this@HomeFragment, requireActivity())
            recycler.adapter = mainListProductAdapter
            recycler.isNestedScrollingEnabled = false
            println("Item Count: "+mainListProductAdapter.itemCount)
            vipAdapter =
                VIPAdapter(vipList, this@HomeFragment, requireActivity())
            vipRv.adapter = vipAdapter
            vipRv.isNestedScrollingEnabled = true

            Handler(Looper.getMainLooper()).postDelayed({
                view.doOnLayout {
                    it.measuredHeight
                    Toast.makeText(requireContext(),"${it.measuredHeight}",Toast.LENGTH_SHORT).show()
                }
            },4000)

            println("ResponseElan: " + announcementspagination)
            mainListProductAdapter =
                MainListProductAdapter(mainList2, this@HomeFragment, requireActivity())
            recycler.adapter = mainListProductAdapter
            mainListProductAdapter.notifyDataSetChanged()

            mainListProductAdapter.setOnItemClickListener {
                val intent = Intent(activity, ProductDetailActivity::class.java)
                println("SubCategory New2: " + it.subCategory)

                intent.putExtra("SubCategory", it.subCategory)
                activity?.startActivity(intent)
                sharedPreferences = requireContext().getSharedPreferences(
                    Constant.sharedkeyname,
                    Context.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                sharedPreferences.edit().putInt("announcementId", it.id).apply()
                sharedPreferences.edit().putBoolean("checkIsRegistered", isRegistered).apply()
                println("Fav Item Clicked without UserID: " + isRegistered)
                println("gedenId-----" + it.id)
                editor.apply()
            }

            vipAdapter =
                VIPAdapter(vipList, this@HomeFragment, requireActivity())
            vipRv.adapter = vipAdapter
            vipRv.isNestedScrollingEnabled = false
            vipAdapter.notifyDataSetChanged()

            println("Vip ItemCount: "+ vipAdapter.itemCount)
            vipAdapter.setOnItemClickListener {
                val intent = Intent(activity, ProductDetailActivity::class.java)
                println("SubCategory New2: " + it.subCategory)

                intent.putExtra("SubCategory", it.subCategory)
                activity?.startActivity(intent)
                sharedPreferences = requireContext().getSharedPreferences(
                    Constant.sharedkeyname,
                    Context.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                sharedPreferences.edit().putInt("announcementId", it.id).apply()
                sharedPreferences.edit().putBoolean("checkIsRegistered", isRegistered).apply()
                println("Fav Item Clicked without UserID: " + isRegistered)
                println("gedenId-----" + it.id)
                editor.apply()
            }
        }


    }
*/

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setscroollListenerGuest() {
// cox guman ram  zen edirem
        recycler.layoutManager = linearLayoutManager

        val nestedScrollView = view.findViewById<NestedScrollView>(R.id.nestedScrollHome)
        nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
           /* if (scrollY == v.(0).measuredHeight - v.measuredHeight)*/
/*
                viewModel.loadMoreData()
*/
        }

      /*  recycler.addOnScrollListener( object: PaginationScrollListener(linearLayoutManager){
            lateinit var compositeDisposable:CompositeDisposable
            private var currentOffset = 0
            private val PAGE_SIZE = 10
            private var isLoading = false
            override fun loadMoreItems() {
                if (isLoading) {
                    return
                }
                isLoading = true
                compositeDisposable = CompositeDisposable()
                val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
                compositeDisposable.add(
                    retrofit.getAnnouncement(limit = PAGE_SIZE, offset = currentOffset)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response ->
                            isLoading = false
//                            onPaginationResponseListener.run(response)
                            if(response != null){
                                println("Emin:" + response.announcemenets)
                                handleResponseforAllItemsAndFavItems(response)
                            }
                            currentOffset += PAGE_SIZE
                        }, { throwable ->
                            isLoading = false
                            println("Error: $throwable")
                        })
                )
            }

            override fun isLastPage(): Boolean {
                return false
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

        }
        )*/
        /*recycler.addOnScrollListener(
            NormalAnnouncementPagination(linearLayoutManager,
                object : OnPaginationResponseListener {

                    override fun <T> run(response: T?) {
                        if ((response as GetAllAnnouncement) != null) {
                            handleResponseforAllItemsAndFavItems(response)
                        }
                    }

                })
        )*/
    }


    private fun getProductWhichIncludeFavorite(id: Int) {
        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
        compositeDisposable.add(
            retrofit.getAnnouncementFavoriteForUserId(id, 10000, 0).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseforAllItemsAndFavItems, {

                })
        )
    }

    private fun handleResponseforAllItemsAndFavItems(response: GetAllAnnouncement) {
        val recycler = requireView().findViewById<RecyclerView>(R.id.allCoursesRV)
        recycler.visibility = View.VISIBLE
        val vipRv = view.findViewById<RecyclerView>(R.id.vipCoursesRV)
        vipRv.visibility = View.VISIBLE
        val lottie = requireView().findViewById<LottieAnimationView>(R.id.loadingHome)
        lottie.visibility = View.GONE
        lottie.pauseAnimation()
        announcements.addAll(response.announcemenets)

        for (newList in response.announcemenets) {
            if (newList.isVIP) {
                vipList.add(newList)
            } else {
                mainList2.add(newList)
            }
        }

        println("responseElanFavoriteTrue: " + response.announcemenets)

        /* for (favItems in response.announcemenets){
             val heartButton=requireView().findViewById<ImageButton>(R.id.favorite_button)
             if (favItems.isFavorite){
                 heartButton.setImageResource(R.drawable.favorite_for_product)
             }
             else{
                 heartButton.setImageResource(R.drawable.favorite_border_for_product)
             }

         }*/
        mainListProductAdapter =
            MainListProductAdapter(mainList2, this@HomeFragment, requireActivity())
        recycler.adapter = mainListProductAdapter
        recycler.isNestedScrollingEnabled = false
        mainListProductAdapter.notifyDataSetChanged()
        mainListProductAdapter.setOnItemClickListener {
            //isFavorite=!isFavorite
            isFavorite = it.isFavorite
            println("SubCategory New: " + it.subCategory)
            val intent = Intent(activity, ProductDetailActivity::class.java)
            intent.putExtra("isFavorite", isFavorite)
            intent.putExtra("subCategory", it.subCategory)
            activity?.startActivity(intent)
            sharedPreferences =
                requireContext().getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            sharedPreferences.edit().putInt("announcementId", it.id).apply()
            sharedPreferences.edit().putBoolean("checkIsRegistered", isRegistered).apply()
            println("Item Clicked with UserID: " + isRegistered)
            println("gedenId-----" + it.id)
            editor.apply()
        }

        vipAdapter =
            VIPAdapter(vipList, this@HomeFragment, requireActivity())
        vipRv.adapter = vipAdapter
        vipRv.isNestedScrollingEnabled = false
        vipAdapter.notifyDataSetChanged()

        vipAdapter.setOnItemClickListener {
            isFavorite = it.isFavorite
            val intent = Intent(activity, ProductDetailActivity::class.java)
            println("SubCategory New2: " + it.subCategory)
            intent.putExtra("SubCategory", it.subCategory)
            activity?.startActivity(intent)
            sharedPreferences =
                requireContext().getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            sharedPreferences.edit().putInt("announcementId", it.id).apply()
            sharedPreferences.edit().putBoolean("checkIsRegistered", isRegistered).apply()
            println("Fav Item Clicked without UserID: " + isRegistered)
            println("gedenId-----" + it.id)
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
        companyId: String,
        userId: Int
    ) {
        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
        compositeDisposable.add(
            retrofit.getFilterProducts(
                limit,
                offset,
                regionId,
                categoryId,
                search,
                minPrice,
                maxPrice,
                statusId,
                isOnlineId,
                companyId,
                userId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::handleResponseFilter,
                    { throwable -> println("MyTests: $throwable") })
        )
    }

    private fun handleResponseFilter(response: GetAllAnnouncement) {
        mainList2.clear()
        vipList.clear()
        val recycler = requireView().findViewById<RecyclerView>(R.id.allCoursesRV)
        recycler.visibility = View.VISIBLE
        val vipRv = view.findViewById<RecyclerView>(R.id.vipCoursesRV)
        vipRv.visibility = View.VISIBLE
        val lottie = requireView().findViewById<LottieAnimationView>(R.id.loadingHome)
        lottie.visibility = View.GONE
        lottie.pauseAnimation()
        for (newList in response.announcemenets) {
            if (newList.isVIP) {
                vipList.add(newList)
            } else {
                mainList2.add(newList)
            }
        }
        mainListProductAdapter =
            MainListProductAdapter(mainList2, this@HomeFragment, requireActivity())
        recycler.adapter = mainListProductAdapter
        mainListProductAdapter.notifyDataSetChanged()
        mainListProductAdapter.setOnItemClickListener {
            val intent = Intent(activity, ProductDetailActivity::class.java)
            activity?.startActivity(intent)
            sharedPreferences =
                requireContext().getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            sharedPreferences.edit().putInt("announcementId", it.id).apply()
            sharedPreferences.edit().putBoolean("checkIsRegistered", isRegistered).apply()
            // println("Item Clicked with UserID: "+isRegistered)

            println("gedenId-----" + it.id)
            editor.apply()
        }

        vipAdapter =
            VIPAdapter(vipList, this@HomeFragment, requireActivity())
        vipRv.adapter = vipAdapter
        vipRv.isNestedScrollingEnabled = false
        vipAdapter.notifyDataSetChanged()

        vipAdapter.setOnItemClickListener {
            val intent = Intent(activity, ProductDetailActivity::class.java)
            println("SubCategory New2: " + it.subCategory)

            intent.putExtra("SubCategory", it.subCategory)
            activity?.startActivity(intent)
            sharedPreferences =
                requireContext().getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            sharedPreferences.edit().putInt("announcementId", it.id).apply()
            sharedPreferences.edit().putBoolean("checkIsRegistered", isRegistered).apply()
            println("Fav Item Clicked without UserID: " + isRegistered)
            println("gedenId-----" + it.id)
            editor.apply()
        }
    }
    override fun onFavoriteItemClick(
        id: Int,
        position: Int,

    ) {
        if (!isRegistered) {
            // User is not registered, show a toast message
            Toast.makeText(requireActivity(), "Please log in in Home", Toast.LENGTH_SHORT).show()
            return
        } else {
            val normalAnnouncement:Boolean=true
            postFav(id,position,true)
        }
    }
    fun postFav(id: Int, position: Int,isVipOrNormal:Boolean) {

        val token = sharedPreferences.getString("USERTOKENNN", "")
        val authHeader = "Bearer $token"
        val userId = sharedPreferences.getInt("userID", 0)
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(FavoriteApi::class.java)

        if (position >= 0 && position < announcements.size) {
            val item = announcements[position]
            compositeDisposable.add(
                retrofit.postFavorite(authHeader, userId, id).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    println(it.isSuccess)
                        item.isFavorite = it.isSuccess
                        if (isVipOrNormal!=true){
                            vipAdapter.LikedItemsforVip(position,it.isSuccess)
                            vipAdapter.notifyDataSetChanged()
                        }
                            else{
                            mainListProductAdapter.LikedItems(position,it.isSuccess)
                            mainListProductAdapter.notifyItemChanged(position)
                            }

                }, { throwable ->
                    println("My msg: ${throwable}")
                })
            )
        }

    }


    override fun onPause() {
        super.onPause()

        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()

        handler.postDelayed(runnable, 4000)
    }

    private val runnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem + 1
    }

    private fun setUpTransformer() {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + r * 0.14f
        }

        viewPager2.setPageTransformer(transformer)
    }

    private fun init() {
        viewPager2 = view.findViewById(R.id.viewPager2)
        handler = Handler(Looper.myLooper()!!)

        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(AdAPI::class.java)

        compositeDisposable.add(
            retrofit.getAds()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ adModel ->
                    // Handle the response from the API
                        println("1")
                    // Create the adapter and set it to the ViewPager2
                    val adapter = ViewPagerImageAdapter(adModel, viewPager2,requireContext())
                    viewPager2.adapter = adapter
                    viewPager2.offscreenPageLimit = 3
                    viewPager2.clipToPadding = false
                    viewPager2.clipChildren = false
                    viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                }, { error ->
                    // Handle the error
                })
        )


    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(msg: String): Boolean {
        if (::mainListProductAdapter.isInitialized) {
            mainListProductAdapter.getFilter().filter(msg)
        }
        if (::vipAdapter.isInitialized){
            vipAdapter.getFilter().filter(msg)
        }
        if (::higlightProducAdapter.isInitialized){
            higlightProducAdapter.getFilter().filter(msg)
        }

        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        val sharedPreferences =
            requireContext().getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("offset", currentOffset)
        editor.apply()
    }

    override fun VIPonFavoriteItemClick(id: Int, position: Int) {
        if (!isRegistered) {
            // User is not registered, show a toast message
            Toast.makeText(requireActivity(), "Please log in in Home", Toast.LENGTH_SHORT).show()
            return
        } else {
            postFav(id,position,false)
        }
    }

    override fun onItemClick(item: Highlight) {
        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)

        when (item.highlightImage) {
            R.drawable.mainpage2 -> {
                mainList2High.clear()

                highRv.visibility = View.GONE
                val recycler1 = view.findViewById<RecyclerView>(R.id.vipCoursesRV)
                recycler1.visibility = View.GONE
                view.vipAnnouncementTextMain.text = "Most Viewed"
                view.line3Main.visibility = View.GONE
                view.AnnouncementTextMain.visibility = View.GONE
                view.line4Main.visibility = View.GONE
                view.allCoursesRV.visibility = View.GONE
                view.filteredCoursesRV.visibility = View.GONE
                val lottie = requireView().findViewById<LottieAnimationView>(R.id.loadingHome)
                lottie.visibility = View.VISIBLE
                lottie.playAnimation()
                // User clicked on "Ən çox baxılanlar" item
                compositeDisposable.add(
                    retrofit.getMostViewHiglight()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ announcementModel ->
                            // Handle the response from the API
                            println("most view")
                            highRv.visibility = View.VISIBLE
                            lottie.visibility = View.GONE
                            lottie.pauseAnimation()
                            val companyDetailItem = announcementModel
                            mainList2High.addAll(companyDetailItem)
                            println("responseElan: " + announcementModel)
                            higlightProducAdapter = HiglighProducAdapter(mainList2High,requireContext())
                            highRv.adapter = higlightProducAdapter
                            highRv.isNestedScrollingEnabled = false
                            higlightProducAdapter.notifySetChanged(mainList2High)
                            higlightProducAdapter.setOnItemClickListener {

                                val intent = Intent(activity, ProductDetailActivity::class.java)
                                activity?.startActivity(intent)
                                sharedPreferences =
                                    requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                sharedPreferences.edit().putInt("announcementId", it.id).apply()
                                println("gedenId-----" + it.id)
                                editor.apply()
                            }

                        }, { error ->
                            // Handle the error
                        })
                )
            }
            R.drawable.yenielan2 -> {
                mainList2High.clear()

                highRv.visibility = View.GONE
                val recycler1 = view.findViewById<RecyclerView>(R.id.vipCoursesRV)
                recycler1.visibility = View.GONE
                view.vipAnnouncementTextMain.text = "New Course"
                view.line3Main.visibility = View.GONE
                view.AnnouncementTextMain.visibility = View.GONE
                view.line4Main.visibility = View.GONE
                view.allCoursesRV.visibility = View.GONE
                view.filteredCoursesRV.visibility = View.GONE
                val lottie = requireView().findViewById<LottieAnimationView>(R.id.loadingHome)
                lottie.visibility = View.VISIBLE
                lottie.playAnimation()
                // User clicked on "${highlightModel[0].newCourse} yeni kurs" item
                compositeDisposable.add(
                    retrofit.getNewHiglight()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ announcementModel ->
                            // Handle the response from the API
                            println("new course")
                            highRv.visibility = View.VISIBLE
                            lottie.visibility = View.GONE
                            lottie.pauseAnimation()
                            val companyDetailItem = announcementModel
                            mainList2High.addAll(companyDetailItem)
                            println("responseElan: " + announcementModel)
                            higlightProducAdapter = HiglighProducAdapter(mainList2High,requireContext())
                            highRv.adapter = higlightProducAdapter
                            highRv.isNestedScrollingEnabled = false
                            higlightProducAdapter.notifySetChanged(mainList2High)
                            higlightProducAdapter.setOnItemClickListener {

                                val intent = Intent(activity, ProductDetailActivity::class.java)
                                activity?.startActivity(intent)
                                sharedPreferences =
                                    requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                sharedPreferences.edit().putInt("announcementId", it.id).apply()
                                println("gedenId-----" + it.id)
                                editor.apply()
                            }
                        }, { error ->
                            // Handle the error
                        })
                )
            }
            R.drawable.vip -> {
                mainList2High.clear()

                highRv.visibility = View.GONE
                val recycler1 = view.findViewById<RecyclerView>(R.id.vipCoursesRV)
                recycler1.visibility = View.GONE
                view.vipAnnouncementTextMain.text = "VIP Courses"
                view.line3Main.visibility = View.GONE
                view.AnnouncementTextMain.visibility = View.GONE
                view.line4Main.visibility = View.GONE
                view.allCoursesRV.visibility = View.GONE
                view.filteredCoursesRV.visibility = View.GONE
                val lottie = requireView().findViewById<LottieAnimationView>(R.id.loadingHome)
                lottie.visibility = View.VISIBLE
                lottie.playAnimation()
                // User clicked on "${highlightModel[0].vip} VIP kurs" item
                compositeDisposable.add(
                    retrofit.getVipHiglight()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ announcementModel ->
                            highRv.visibility = View.VISIBLE
                            lottie.visibility = View.GONE
                            lottie.pauseAnimation()
                            // Handle the response from the API
                            println("vip course")
                            val companyDetailItem = announcementModel
                            mainList2High.addAll(companyDetailItem)

                            println("responseElan: " + announcementModel)
                            higlightProducAdapter = HiglighProducAdapter(mainList2High,requireContext())
                            highRv.adapter = higlightProducAdapter
                            highRv.isNestedScrollingEnabled = false
                            higlightProducAdapter.notifySetChanged(mainList2High)
                            higlightProducAdapter.setOnItemClickListener {

                                val intent = Intent(activity, ProductDetailActivity::class.java)
                                activity?.startActivity(intent)
                                sharedPreferences =
                                    requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                sharedPreferences.edit().putInt("announcementId", it.id).apply()
                                println("gedenId-----" + it.id)
                                editor.apply()
                            }
                        }, { error ->
                            // Handle the error
                        })
                )
            }
            else -> {
                // Handle any other items or conditions
            }
        }
    }

}