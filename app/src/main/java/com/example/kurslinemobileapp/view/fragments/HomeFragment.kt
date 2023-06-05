package com.example.kurslinemobileapp.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.HiglightForMainListAdapter
import com.example.kurslinemobileapp.adapter.MainListProductAdapter
import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.model.mainpage.Highlight
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.accountsFragments.BusinessTransactionProfileFragment
import com.example.kurslinemobileapp.view.courseFmAc.ProductDetailActivity
import com.example.kurslinemobileapp.view.loginRegister.LoginActivity
import com.example.kurslinemobileapp.view.loginRegister.MainRegisterActivity
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register_company.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    private lateinit var mainListProductAdapter: MainListProductAdapter
    private lateinit var mainList : ArrayList<GetAllAnnouncement>
    private lateinit var mainList2 : ArrayList<GetAllAnnouncement>
    private lateinit var compositeDisposable: CompositeDisposable
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)
            val createAccount = view.findViewById<TextView>(R.id.createAccountTextMain)

        mainList = ArrayList<GetAllAnnouncement>()
        mainList2 = ArrayList<GetAllAnnouncement>()
        val coursesRV = view.findViewById<RecyclerView>(R.id.allCoursesRV)
        coursesRV.layoutManager = GridLayoutManager(requireContext(),2)
        getProducts()
      //  imageforHighlight()


        createAccount.setOnClickListener {
            val intent = Intent(activity, MainRegisterActivity::class.java)
            activity?.startActivity(intent)
        }

        val goToFilter = view.findViewById<TextInputEditText>(R.id.mainFilterEditText)
        goToFilter.setOnClickListener {
            val fragmentManager = requireFragmentManager()

            // Start a fragment transaction
            val transaction = fragmentManager.beginTransaction()

            // Replace the first fragment with the second fragment
            transaction.replace(R.id.relativeLayoutMain, FilterFragment())
            transaction.setReorderingAllowed(true)

            // Add the transaction to the back stack
            transaction.addToBackStack(null)

            // Commit the transaction
            transaction.commit()
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
        mainListProductAdapter = MainListProductAdapter(mainList2)
        val recyclerviewForProducts = requireView().findViewById<RecyclerView>(R.id.allCoursesRV)
        recyclerviewForProducts.adapter = mainListProductAdapter
        mainListProductAdapter.notifyDataSetChanged()
        mainListProductAdapter.setOnItemClickListener {
            val intent = Intent(activity, ProductDetailActivity::class.java)
            intent.putExtra("id",response.announcemenets.get(0).id)
            activity?.startActivity(intent)
            println("responseId: " +  intent.putExtra("id",response.announcemenets.get(0).id))
        }
    }

}