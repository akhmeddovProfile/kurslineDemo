package com.example.kurslinemobileapp.view.fragments

import android.annotation.SuppressLint
import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.airbnb.lottie.LottieAnimationView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.CategoryAdapter
import com.example.kurslinemobileapp.adapter.CourseFilterAdapter
import com.example.kurslinemobileapp.adapter.RegionAdapter
import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.api.announcement.filterAnnouncements.FilterModel
import com.example.kurslinemobileapp.api.companyData.CompanyDatasAPI
import com.example.kurslinemobileapp.api.companyTeachers.CompanyTeacherAPI
import com.example.kurslinemobileapp.api.companyTeachers.companyTeacherRow.CompanyTeacherModel
import com.example.kurslinemobileapp.api.companyTeachers.companyTeacherRow.CompanyTeacherModelItem
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.service.Room.AppDatabase
import com.example.kurslinemobileapp.service.Room.category.MyRepositoryForCategory
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register_company.*
import kotlinx.android.synthetic.main.activity_user_register.*
import kotlinx.android.synthetic.main.activity_user_to_company.*
import kotlinx.android.synthetic.main.fragment_filter.*
import kotlinx.android.synthetic.main.fragment_filter.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FilterFragment : Fragment() {
    private lateinit var mainList: ArrayList<CompanyTeacherModelItem>
    private lateinit var view: ViewGroup
    var compositeDisposable = CompositeDisposable()
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var button5: Button
    private lateinit var button6: Button
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var courseFilterAdapter: CourseFilterAdapter
    private lateinit var regionAdapter: RegionAdapter
    lateinit var categoryId: String
    lateinit var regionId:String
    lateinit var courseId: String
    private var job: Job? = null
    private lateinit var repository: MyRepositoryForCategory

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         view =  inflater.inflate(R.layout.fragment_filter, container, false) as ViewGroup
        view.backtoMainPage.setOnClickListener {
            findNavController().navigate(R.id.action_filterFragment_to_homeFragment)
        }

        repository = MyRepositoryForCategory(
            AppDatabase.getDatabase(requireContext()).categoryDao(),
            AppDatabase.getDatabase(requireContext()).subCategoryDao()
        )
        categoryId = ""
        regionId = ""
        courseId = ""
        button1 = view.findViewById(R.id.coursefilter)
        button2 = view.findViewById(R.id.repititorfilter)
        button3 = view.findViewById(R.id.allcourseFilter)

        button4 = view.findViewById(R.id.onlinemodeBtn)
        button5 = view.findViewById(R.id.offlineModebtn)
        button6 = view.findViewById(R.id.allModesbtn)

        view.allCategoriesFilterTxt.setOnClickListener {
            showBottomSheetDialog()
        }
        view.filterRegionsTxt.setOnClickListener {
            showBottomSheetDialogRegions()
        }
        view.filterRepititorid.setOnClickListener {
            showBottomSheetDialogCourses()
        }

        val search = ""
        var statusId = ""
        var isOnlineId = ""
        val limit = 10
        val offset = 0

        button1.setOnClickListener {
            updateButtonBackgroundsStatus(button1)
            statusId = "1"
        }
        button2.setOnClickListener {
            updateButtonBackgroundsStatus(button2)
            statusId = "2"
        }
        button3.setOnClickListener {
            updateButtonBackgroundsStatus(button3)
            statusId = "3"
        }

        button4.setOnClickListener {
            updateButtonBackgroundsMode(button4)
            isOnlineId = "1"
        }
        button5.setOnClickListener {
            updateButtonBackgroundsMode(button5)
            isOnlineId = "2"
        }
        button6.setOnClickListener {
            updateButtonBackgroundsMode(button6)
            isOnlineId = "3"
        }

        view.resetFilter.setOnClickListener {
            categoryId = ""
            regionId = ""
            courseId = ""
            view.filterRegionsTxt.text = "Regionlar"
            view.allCategoriesFilterTxt.text = "Kateqoriyalar"
            resetBtnsBackground(button1)
            resetBtnsBackground(button2)
            resetBtnsBackground(button3)
            resetBtnsBackground(button4)
            resetBtnsBackground(button5)
            resetBtnsBackground(button6)
            statusId = ""
            isOnlineId = ""
            view.maxEditText.text?.clear()
         view.minEditText.text?.clear()
        }

        view.showCoursesFilterBtn.setOnClickListener {
            val region = regionId
            println("regionId: "+region)
            val category = categoryId
            val maxPrice = view.maxEditText.text.toString().trim()
            val minPrice = view.minEditText.text.toString().trim()

            val bundle = Bundle()
            bundle.putString("search", search)
            bundle.putString("statusId", statusId)
            bundle.putString("isOnlineId", isOnlineId)
            bundle.putInt("limit", limit)
            bundle.putInt("offset", offset)
            bundle.putString("regionId", region)
            bundle.putString("categoryId", category)
            bundle.putString("minPrice", minPrice)
            bundle.putString("maxPrice", maxPrice)

            // Create an instance of the HomeFragment
            val homeFragment = HomeFragment()
            homeFragment.arguments = bundle

            // Navigate to the HomeFragment and pass the filter parameters
            findNavController().navigate(R.id.action_filterFragment_to_homeFragment, bundle)
        }
        return view
    }


    @SuppressLint("ResourceAsColor")
    private fun updateButtonBackgroundsStatus(selectedButton: Button) {
        val buttons = arrayOf(button1, button2, button3)

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


    private fun resetBtnsBackground(selectedButton: Button){
        val buttons = arrayOf(button1, button2, button3,button4, button5, button6)
        for (button in buttons){
            button.setBackgroundResource(R.drawable.business_button_bg_2)
            button.setTextColor(this.getResources().getColor(R.color.black))
        }
    }

    private fun updateButtonBackgroundsMode(selectedButton: Button) {
        val buttons = arrayOf(button4, button5, button6)

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

    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    private fun showBottomSheetDialog() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bottomSheetView)
        val recyclerViewCategories: RecyclerView =
            bottomSheetView.findViewById(R.id.recyclerViewCategories)
        recyclerViewCategories.setHasFixedSize(true)
        recyclerViewCategories.setLayoutManager(LinearLayoutManager(requireContext()))

        job=repository.getAllCategories().onEach { categories ->
            println("1")
            categoryAdapter = CategoryAdapter(categories)
            recyclerViewCategories.adapter = categoryAdapter
            categoryAdapter.setChanged(categories)
            categoryAdapter.setOnItemClickListener { category ->
                categoryId = category.category.categoryId.toString()
                allCategoriesFilterTxt.setText(category.category.categoryName)
                dialog.dismiss()
            }
        }.catch { throwable ->
            println("MyTests: $throwable")
        }.launchIn(lifecycleScope)
        dialog.show()

    }
    @SuppressLint("MissingInflatedId")
    private fun showBottomSheetDialogRegions() {
        val appdatabase = AppDatabase.getDatabase(requireContext())
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_region, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bottomSheetView)
        val recyclerviewRegions: RecyclerView =
            bottomSheetView.findViewById(R.id.recyclerViewRegions)
        recyclerviewRegions.setHasFixedSize(true)
        recyclerviewRegions.setLayoutManager(LinearLayoutManager(requireContext()))

        job=appdatabase.regionDao().getAllRegions()
            .onEach { reg->
                println("2")
                regionAdapter = RegionAdapter(reg)
                recyclerviewRegions.adapter = regionAdapter
                regionAdapter.setChanged(reg)
                regionAdapter.setOnItemClickListener { region ->
                    view.filterRegionsTxt.setText(region.regionName)
                    regionId = region.regionId.toString()
                    dialog.dismiss()
                }
            }.catch { throwable ->
                println("MyTestsRegions: $throwable")

            }.launchIn(lifecycleScope)

        dialog.show()
    }

    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    private fun showBottomSheetDialogCourses() {
        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(CompanyTeacherAPI::class.java)
        compositeDisposable.add(
            retrofit.getCompanies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse, { throwable -> println("MyTests: $throwable") })
        )


    }

    private fun handleResponse(response: CompanyTeacherModel) {
        mainList = ArrayList()
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_courses1, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bottomSheetView)
        val recyclerViewCategories: RecyclerView =
            bottomSheetView.findViewById(R.id.recyclerViewCourses)
        recyclerViewCategories.setHasFixedSize(true)
        recyclerViewCategories.setLayoutManager(LinearLayoutManager(requireContext()))
        val filteredList = response.filter { it.companyStatusId == 2 }
        mainList.addAll(filteredList)
        courseFilterAdapter = CourseFilterAdapter(filteredList)
        recyclerViewCategories.adapter = courseFilterAdapter
        courseFilterAdapter.setChanged(filteredList)
        courseFilterAdapter.setOnItemClickListener { course ->
            categoryId = course.companyId.toString()
            view.filterRepititorid.setText(course.companyName)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun cancelJob() {
        job?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelJob()
    }
}