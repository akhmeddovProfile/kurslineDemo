package com.example.kurslinemobileapp.view.fragments

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.kurslinemobileapp.R
import com.app.kurslinemobileapp.databinding.FragmentContactUsBinding
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ContactUsFragment : Fragment() {
    private lateinit var view: ViewGroup
    private lateinit var bindingContactUsBinding: FragmentContactUsBinding
    private var job: Job = Job()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingContactUsBinding=FragmentContactUsBinding.inflate(inflater,container,false)
        view = inflater.inflate(R.layout.fragment_contact_us, container, false) as ViewGroup

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        val goToUploadActivity=requireActivity().findViewById<FloatingActionButton>(R.id.goToUploadActivity)
        val writeusLetterContainer=bindingContactUsBinding.writeUsLetterContainer
        val writeusEditText=bindingContactUsBinding.writeUsLetterEdittext
        writeusLetterContainer.setOnClickListener {
            writeusEditText.requestFocus()
            writeusEditText.viewTreeObserver.addOnGlobalLayoutListener {
                val rect=Rect()
                writeusEditText.getGlobalVisibleRect(rect)
                val screenHeight = resources.displayMetrics.heightPixels
                if (rect.bottom > screenHeight) {
                    writeusLetterContainer.scrollBy(0, rect.bottom - screenHeight)
                }
            }
        }
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

    fun sendMessage(context: Context) {
        val phoneNumber = "+994" + bindingContactUsBinding.writeUsPhoneText?.text.toString()
        val message = bindingContactUsBinding.writeUsLetterEdittext?.text.toString()
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                val apiService = RetrofitService(Constant.BASE_URL).apiServicewriteUs.writeUs(phoneNumber, message).await()

                if (apiService.isSuccess) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "Your message had been sent successfully", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_contactUsFragment_to_homeFragment)
                    }
                } else {
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: HttpException) {
                if (isActive) {
                    if (e.code() == 404) {
                        launch(Dispatchers.Main) {
                            Toast.makeText(context, "HTTP 404 - ${context.getString(R.string.http404)}", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_contactUsFragment_to_homeFragment)
                        }
                    } else {
                        launch(Dispatchers.Main) {
                            Toast.makeText(context, "HTTP Error: ${e.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    // Handle other HTTP errors if needed
                    println("HTTP Error: ${e.code()}")
                }
            } catch (e: java.lang.Exception) {
                if (isActive) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}