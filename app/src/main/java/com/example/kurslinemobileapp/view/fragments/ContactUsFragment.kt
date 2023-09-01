package com.example.kurslinemobileapp.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import kotlinx.android.synthetic.main.fragment_contact_us.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ContactUsFragment : Fragment() {
    private lateinit var view: ViewGroup
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contact_us, container, false) as ViewGroup

        view?.sendLetterBtn?.setOnClickListener {
            sendMessage(requireContext())
        }
        return view
    }

    fun sendMessage(context: Context){
        val phoneNumber ="+994"+view.writeUsPhoneText?.text.toString()
        val message = view.writeUsLetterEdittext?.text.toString()
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