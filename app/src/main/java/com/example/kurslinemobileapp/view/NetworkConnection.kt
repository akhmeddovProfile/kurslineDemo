package com.example.kurslinemobileapp.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Build
import androidx.lifecycle.LiveData
import com.app.kurslinemobileapp.databinding.NetworkErrorLayoutBinding

class NetworkConnection(private val context:Context):LiveData<Boolean>() {

    private lateinit var bindingNetworkConnection: NetworkErrorLayoutBinding

    private val connectivityManager:ConnectivityManager=context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private lateinit var networkConnectionCallBack:ConnectivityManager.NetworkCallback

    override fun onActive() {
        super.onActive()
        updateNetworkConnection()
        when{
            Build.VERSION.SDK_INT>=Build.VERSION_CODES.N->{
                connectivityManager.registerDefaultNetworkCallback(connectionCallBack())
            }else->{
                context.registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        //connectivityManager.unregisterNetworkCallback(connectionCallBack())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                connectivityManager.unregisterNetworkCallback(connectionCallBack())
            } catch (e: Exception) {
                // Handle exceptions if needed
            }
        } else {
            try {
                context.unregisterReceiver(networkReceiver)
            } catch (e: Exception) {
                // Handle exceptions if needed
            }
        }
    }

    private fun updateNetworkConnection() {
        val networkConnection:NetworkInfo?=connectivityManager.activeNetworkInfo
        postValue(networkConnection?.isConnected==true)
    }


    private fun connectionCallBack():ConnectivityManager.NetworkCallback{
        networkConnectionCallBack=object :ConnectivityManager.NetworkCallback(){

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                postValue(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                postValue(false)
            }

        }
        return networkConnectionCallBack
    }

    private val networkReceiver=object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {

        }

    }
}