package fr.quentinklein.slt

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.annotation.RequiresPermission
import android.util.Log

class LocationTracker constructor(private val trackerSettings: TrackerSettings = TrackerSettings()) {
    // Android LocationManager
    private lateinit var locationManager: LocationManager

    // TAG for Logs
    private val TAG = "LocationTracker"

    // Last known location
    private var lastKnownLocation: Location? = null

    // Custom Listener for the LocationManager
    private val listener = object : LocationListener {
        override fun onLocationChanged(p0: Location) {
            Location(p0).let { currentLocation ->
                lastKnownLocation = currentLocation
                hasLocationFound = true
                listeners.forEach { l -> l.onLocationFound(currentLocation) }
            }
        }

        override fun onProviderDisabled(provider: String) {
            Log.i(TAG, "Provider `$provider` has been disabled")
        }

        override fun onProviderEnabled(provider: String) {
            // By default do nothing but log
            Log.i(TAG, "Provider `$provider` has been enabled")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            // By default do nothing but log
            Log.i(TAG, "Provider `$provider` status has changed, new status is `$status`")
        }

    }

    // List used to register the listeners to notify
    private val listeners: MutableList<Listener> = mutableListOf()

    /**
     * Indicates if Tracker is listening for updates or not
     */
    var isListening = false
        private set

    /**
     * Indicates if Tracker has found the location or not
     */
    var hasLocationFound = false
        private set

    /**
     * Make the tracker listening for location updates
     * @param context a valid android.content.Context
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun startListening(context: Context) {
        initManagerAndUpdateLastKnownLocation(context)
        if (!isListening) {
            // Listen for GPS Updates
            if (trackerSettings.shouldUseGPS) {
                registerForLocationUpdates(LocationManager.GPS_PROVIDER)
            }
            // Listen for Network Updates
            if (trackerSettings.shouldUseNetwork) {
                registerForLocationUpdates(LocationManager.NETWORK_PROVIDER)
            }
            // Listen for Passive Updates
            if (trackerSettings.shouldUseNetwork) {
                registerForLocationUpdates(LocationManager.PASSIVE_PROVIDER)
            }
            isListening = true
            Log.i(TAG, "Now listening for location updates")
        } else {
            Log.i(TAG, "Relax, already listening for location updates")
        }
    }

    /**
     * Make the tracker stops listening for location updates
     */
    fun stopListening(cleanListeners: Boolean = false) {
        if (isListening) {
            locationManager.removeUpdates(listener)
            isListening = false
            if (cleanListeners) {
                listeners.clear()
            }
            Log.i(TAG, "Stop listening for location updates")
        } else {
            Log.i(TAG, "Not listening at this time")
        }
    }

    /**
     * Best effort, it calls [.onLocationChanged] with static field named [.lastKnownLocation] if it is not null
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun quickFix(@NonNull context: Context) {
        initManagerAndUpdateLastKnownLocation(context)
        lastKnownLocation?.let { lastLocation ->
            listeners.forEach { l -> l.onLocationFound(lastLocation) }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initManagerAndUpdateLastKnownLocation(context: Context) {
        // Init the manager
        locationManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getSystemService(LocationManager::class.java)
        } else {
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        // Update the lastKnownLocation
        if (lastKnownLocation == null && trackerSettings.shouldUseGPS) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
        if (lastKnownLocation == null && trackerSettings.shouldUseNetwork) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        if (lastKnownLocation == null && trackerSettings.shouldUsePassive) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        }
    }

    @SuppressLint("MissingPermission")
    private fun registerForLocationUpdates(provider: String) {
        if (locationManager.isProviderEnabled(provider)) {
            locationManager.requestLocationUpdates(provider, trackerSettings.timeBetweenUpdates, trackerSettings.metersBetweenUpdates, listener)
        } else {
            listeners.forEach { l -> l.onProviderError(ProviderError("Provider `$provider` is not enabled")) }
        }
    }

    interface Listener {
        /**
         * Called when the tracker had found a location
         *
         * @param location the found location
         */
        fun onLocationFound(location: Location)

        /**
         * Called when there is an error on a specific provider
         * @param providerError the error sent
         */
        fun onProviderError(providerError: ProviderError)
    }

}
