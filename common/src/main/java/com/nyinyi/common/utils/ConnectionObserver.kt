package com.nyinyi.common.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface ConnectionObserver {
    var onConnected: (hasInternetAccess: Boolean) -> Unit
    var onDisconnected: () -> Unit

    fun startObserving()

    fun stopObserving()

    fun isCurrentlyConnected(): Boolean
}

class ConnectionObserverImpl
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : ConnectionObserver {
        private val connectivityManager = context.getSystemService<ConnectivityManager>()
        private var lastKnownNetwork: Network? = null
        private var lastKnownInternetStatus: Boolean = false

        private val networkCallback =
            object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities,
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)

                    if (lastKnownNetwork == null || lastKnownNetwork == network) {
                        val hasInternet =
                            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                        if (hasInternet != lastKnownInternetStatus) {
                            lastKnownInternetStatus = hasInternet
                            onConnected(hasInternet)
                        }
                    }
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)

                    lastKnownNetwork = network
                }

                override fun onLost(network: Network) {
                    super.onLost(network)

                    if (lastKnownNetwork == network) {
                        lastKnownNetwork = null
                        lastKnownInternetStatus = false
                        onDisconnected()
                    }
                }
            }

        override var onConnected: (hasInternetAccess: Boolean) -> Unit = {}
        override var onDisconnected: () -> Unit = {}

        override fun startObserving() {
            val networkRequest =
                NetworkRequest
                    .Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                    .build()

            connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)

            val activeNetwork = connectivityManager?.activeNetwork
            val currentCaps = connectivityManager?.getNetworkCapabilities(activeNetwork)
            val hasInternet =
                currentCaps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                    currentCaps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
            lastKnownInternetStatus = hasInternet
            lastKnownNetwork = activeNetwork
            onConnected(hasInternet)
        }

        override fun stopObserving() {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
            lastKnownNetwork = null
            lastKnownInternetStatus = false
        }

        override fun isCurrentlyConnected(): Boolean {
            if (connectivityManager == null) return false

            val activeNetwork = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

            return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }
    }
