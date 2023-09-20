package com.example.kurslinemobileapp.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.kurslinemobileapp.R
import com.app.kurslinemobileapp.databinding.FragmentContactUsBinding
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ContactUsFragment : Fragment() {
    private lateinit var view: ViewGroup
    private lateinit var bindingContactUsBinding: FragmentContactUsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingContactUsBinding=FragmentContactUsBinding.inflate(inflater,container,false)
        view = inflater.inflate(R.layout.fragment_contact_us, container, false) as ViewGroup

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        val goToUploadActivity=requireActivity().findViewById<FloatingActionButton>(R.id.goToUploadActivity)
        // Hide the BottomNavigationView
        goToUploadActivity.visibility=View.GONE
        bottomNavigationView.visibility = View.GONE
        bindingContactUsBinding.sendLetterBtn?.setOnClickListener {
            sendMessage(requireContext())
        }
        bindingContactUsBinding.backtoHomePage.setOnClickListener {
            findNavController().navigate(R.id.action_contactUsFragment_to_homeFragment)
        }
        return bindingContactUsBinding.root
    }

    fun sendMessage(context: Context){
        val phoneNumber ="+994"+bindingContactUsBinding.writeUsPhoneText?.text.toString()
        val message = bindingContactUsBinding.writeUsLetterEdittext?.text.toString()
        CoroutineScope(Dispatchers.Main).launch {
            val apiService = RetrofitService(Constant.BASE_URL).apiServicewriteUs.writeUs(phoneNumber,message).await()
            try {
                if (apiService.isSuccess){
                    Toast.makeText(context,"Your message had been sent successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_contactUsFragment_to_homeFragment)
                }else{
                    Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show()
                }
            }catch (e: HttpException) {
                // Handle HTTP error
                println("HTTP Error: ${e.code()}")
            }
            catch (e:java.lang.Exception){
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}