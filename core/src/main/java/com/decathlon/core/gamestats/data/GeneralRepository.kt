package com.decathlon.core.gamestats.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

class GeneralRepository(val context: Context) {

    var listener: IInternetAvailability? = null

    private var connectivityManager: ConnectivityManager? = null

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            listener?.onInternetAvailabilityChange(true)
        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            listener?.onInternetAvailabilityChange(false)
        }
    }

    fun registerConnectivityListener() {
        if (connectivityManager == null) {
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build()

            connectivityManager =
                context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
            connectivityManager?.requestNetwork(networkRequest, networkCallback)
        }
    }

    fun isConnected(): Boolean {
        val manager =
            context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        return manager.activeNetwork != null
    }

    fun unRegisterConnectivityListener() {
        connectivityManager?.unregisterNetworkCallback(networkCallback)
        listener = null
    }
}

interface IInternetAvailability {
    fun onInternetAvailabilityChange(isAvailable: Boolean)
}