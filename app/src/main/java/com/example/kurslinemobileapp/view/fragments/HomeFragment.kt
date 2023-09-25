package com.example.kurslinemobileapp.view.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.app.kurslinemobileapp.R
import com.app.kurslinemobileapp.databinding.FragmentHomeBinding
import com.example.kurslinemobileapp.adapter.*
import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Announcemenet
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.api.announcement.higlightProduct.HiglightProductModelItem
import com.example.kurslinemobileapp.api.favorite.FavoriteApi
import com.example.kurslinemobileapp.model.mainpage.Highlight
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.service.Room.AppDatabase
import com.example.kurslinemobileapp.view.courseFmAc.ProductDetailActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


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
    private var job: Job? = null

    private lateinit var binding: FragmentHomeBinding
    //@RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentHomeBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        //view = inflater.inflate(R.layout.fragment_home, container, false) as ViewGroup
        val createAccount = binding.writeus

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

        recycler = binding.allCoursesRV
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recycler.visibility = View.GONE

        highRv = binding.higlightCoursesRV
        highRv.visibility = View.GONE
        vipRv = binding.vipCoursesRV
        vipRv.visibility = View.GONE
        val lottie = binding.loadingHome
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

            val recylerviewForHighlight = binding.topProductsRV
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
                        Highlight(R.drawable.mainpage2,"${highlightModel[0].mostView} ${getString(R.string.mostViewHgText)}" ),
                        Highlight(R.drawable.yenielan2,"${highlightModel[0].newCourse} ${getString(R.string.newHgText)}"),
                        Highlight(R.drawable.vip, "${highlightModel[0].vip} ${getString(R.string.vipHgText)}")
                    )
                    val recylerviewForHighlight = binding.topProductsRV
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


        createAccount.setOnClickListener {
           findNavController().navigate(R.id.action_homeFragment1_to_contactUsFragment1)
        }

        val goToFilter = binding.mainFilterEditText
        goToFilter.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment1_to_filterFragment1)
        }

        val userType = sharedPreferences.getString("userType", null)
        if (userType == "İstifadəçi" || userType == "Kurs" || userType == "Repititor") {
            binding.writeus.visibility = View.VISIBLE
        } else {
            binding.writeus.visibility = View.VISIBLE
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

        val searchView = binding.searchViewAnnEditText
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
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 4000)
            }
        })


        return binding.root
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
        val recycler = binding.allCoursesRV
        recycler.visibility = View.VISIBLE
        val vipRv = binding.vipCoursesRV
        vipRv.visibility = View.VISIBLE
        val lottie = binding.loadingHome
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
        if (response.announcemenets.isNotEmpty()){
            val recycler = binding.allCoursesRV
            recycler.visibility = View.VISIBLE
            val vipRv = binding.vipCoursesRV
            vipRv.visibility = View.VISIBLE
            val lottie = binding.loadingHome
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
        }else{
            binding.notFoundHomeCourseText.visibility =View.VISIBLE
            binding.notFoundImageHome.visibility =View.VISIBLE
            binding.line1Main.visibility = View.GONE
            binding.vipAnnouncementTextMain.visibility = View.GONE
            binding.line2Main.visibility = View.GONE
            binding.vipCoursesRV.visibility =View.GONE
            binding.line3Main.visibility = View.GONE
            binding.AnnouncementTextMain.visibility = View.GONE
            binding.line4Main.visibility = View.GONE
            binding.allCoursesRV.visibility = View.GONE
            val lottie = binding.loadingHome
            lottie.visibility =View.GONE
            lottie.pauseAnimation()

        }


    }    override fun onFavoriteItemClick(
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
        binding.viewPager2.currentItem = binding.viewPager2.currentItem + 1
    }

    private fun setUpTransformer() {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + r * 0.14f
        }

        binding.viewPager2.setPageTransformer(transformer)
    }

    private fun init() {
        val appdatabase = AppDatabase.getDatabase(requireContext())
        viewPager2 = binding.viewPager2
        handler = Handler(Looper.myLooper()!!)

/*
        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(AdAPI::class.java)
*/

        job=appdatabase.advDao().getAllAdv().onEach { adModel->
            val advArrayList=ArrayList(adModel)
            val adapter = ViewPagerImageAdapter(advArrayList, viewPager2,requireContext())
            binding.viewPager2.adapter = adapter
            binding.viewPager2.offscreenPageLimit = 3
            binding.viewPager2.clipToPadding = false
            binding.viewPager2.clipChildren = false
            binding.viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }.catch {throwable->
            println("MyTests: $throwable")

        }.launchIn(lifecycleScope)

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
                val recycler1 = binding.vipCoursesRV
                recycler1.visibility = View.GONE
                binding.vipAnnouncementTextMain.text = getString(R.string.mostViewHg)
                binding.line3Main.visibility = View.GONE
                binding.AnnouncementTextMain.visibility = View.GONE
                binding.line4Main.visibility = View.GONE
                binding.allCoursesRV.visibility = View.GONE
                binding.filteredCoursesRV.visibility = View.GONE
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
                val recycler1 = binding.vipCoursesRV
                recycler1.visibility = View.GONE
                binding.vipAnnouncementTextMain.text = getString(R.string.newHg)
                binding.line3Main.visibility = View.GONE
                binding.AnnouncementTextMain.visibility = View.GONE
                binding.line4Main.visibility = View.GONE
                binding.allCoursesRV.visibility = View.GONE
                binding.filteredCoursesRV.visibility = View.GONE
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
                val recycler1 = binding.vipCoursesRV
                recycler1.visibility = View.GONE
                binding.vipAnnouncementTextMain.text = getString(R.string.vipHg)
                binding.line3Main.visibility = View.GONE
                binding.AnnouncementTextMain.visibility = View.GONE
                binding.line4Main.visibility = View.GONE
                binding.allCoursesRV.visibility = View.GONE
                binding.filteredCoursesRV.visibility = View.GONE
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