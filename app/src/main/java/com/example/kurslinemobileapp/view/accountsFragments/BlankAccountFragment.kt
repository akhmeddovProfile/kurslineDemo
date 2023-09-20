package com.example.kurslinemobileapp.view.accountsFragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.kurslinemobileapp.R
import com.app.kurslinemobileapp.databinding.FragmentBlankAccountBinding
import com.example.kurslinemobileapp.service.Constant.sharedkeyname
import com.example.kurslinemobileapp.view.loginRegister.LoginActivity

class BlankAccountFragment : Fragment() {
    private lateinit var binding:FragmentBlankAccountBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentBlankAccountBinding.inflate(inflater,container,false)
        //val view = inflater.inflate(R.layout.fragment_blank_account, container, false)
        val sharedPreferences = requireActivity().getSharedPreferences(sharedkeyname, Context.MODE_PRIVATE)
        val isRegistered = sharedPreferences.getBoolean("token", false)

        if (!isRegistered) {
            // User is not registered, navigate to the login activity
            val intent = Intent(activity, LoginActivity::class.java)
            activity?.startActivity(intent)
            activity?.finish()
        } else {
            val userType = sharedPreferences.getString("userType", null)

            when (userType) {
                "İstifadəçi" -> findNavController().navigate(R.id.action_blankAccountFragment_to_accountFragment)
                "Kurs", "Repititor" -> findNavController().navigate(R.id.action_blankAccountFragment_to_businessTransactionProfileFragment)
                else -> {
                    // Handle the case when userType is unknown
                    val intent = Intent(activity, LoginActivity::class.java)
                    activity?.startActivity(intent)
                }
            }
        }

        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = requireFragmentManager()
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayoutforChange, fragment)
        transaction.setReorderingAllowed(true)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}