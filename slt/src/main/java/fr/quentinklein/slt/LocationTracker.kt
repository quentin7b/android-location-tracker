package fr.quentinklein.slt

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.annotation.RequiresPermission
import android.util.Log

/**
 * Build a LocationTracker
 * @param minTimeBetweenUpdates The minimum time interval between location updates, in milliseconds by default its value is 5 minutes
 * @param minDistanceBetweenUpdates The minimum distance between location updates in meters, by default its value is 100m
 * @param shouldUseGPS Specifies if tracker should use the GPS (default is true)
 * @param shouldUseNetwork Specifies if tracker should use the Network (default is true)
 * @param shouldUsePassive Specifies if tracker should use the Passive provider (default is true)
 */
class LocationTracker constructor(
        val minTimeBetweenUpdates: Long = 5 * 60 * 1000.toLong(),
        val minDistanceBetweenUpdates: Float = 100f,
        val shouldUseGPS: Boolean = true,
        val shouldUseNetwork: Boolean = true,
        val shouldUsePassive: Boolean = true
) {
    // Android LocationManager
    private lateinit var locationManager: LocationManager

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
            behaviorListener.forEach { l -> l.onProviderDisabled(provider) }
        }

        override fun onProviderEnabled(provider: String) {
            behaviorListener.forEach { l -> l.onProviderEnabled(provider) }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            behaviorListener.forEach { l -> l.onStatusChanged(provider, status, extras) }
        }

    }

    // List used to register the listeners to notify
    private val listeners: MutableSet<Listener> = mutableSetOf()

    // List used to register the behavior listeners to notify
    private val behaviorListener: MutableSet<BehaviorListener> = mutableSetOf()

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
     * Add a listener to the stack so it will be notified once a new location is found
     * @param listener the listener to add to the list.
     * @return true if the listener has been added, false otherwise
     */
    fun addListener(listener: Listener): Boolean = listeners.add(listener)

    /**
     * Remove a listener from the stack
     * @param listener the listener to remove from the list.
     * @return true if the listener has been removed, false otherwise
     */
    fun removeListener(listener: Listener): Boolean = listeners.remove(listener)

    /**
     * Add a behavior listener to the stack so it will be notified when a provider is updated
     * @param listener the listener to add to the list.
     * @return true if the listener has been added, false otherwise
     */
    fun addBehaviorListener(listener: BehaviorListener): Boolean = behaviorListener.add(listener)

    /**
     * Remove a behavior listener from the stack
     * @param listener the listener to remove from the list.
     * @return true if the listener has been removed, false otherwise
     */
    fun removeBehaviorListener(listener: BehaviorListener): Boolean = behaviorListener.remove(listener)


    /**
     * Make the tracker listening for location updates
     * @param context a valid android.content.Context
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun startListening(context: Context) {
        initManagerAndUpdateLastKnownLocation(context)
        if (!isListening) {
            // Listen for GPS Updates
            if (shouldUseGPS) {
                registerForLocationUpdates(LocationManager.GPS_PROVIDER)
            }
            // Listen for Network Updates
            if (shouldUseNetwork) {
                registerForLocationUpdates(LocationManager.NETWORK_PROVIDER)
            }
            // Listen for Passive Updates
            if (shouldUseNetwork) {
                registerForLocationUpdates(LocationManager.PASSIVE_PROVIDER)
            }
            isListening = true
        }
    }

    /**
     * Make the tracker stops listening for location updates
     * @param clearListeners optional (default false) drop all the listeners if set to true
     */
    fun stopListening(clearListeners: Boolean = false) {
        if (isListening) {
            locationManager.removeUpdates(listener)
            isListening = false
            if (clearListeners) {
                listeners.clear()
            }
        }
    }

    /**
     * Best effort, it calls [.onLocationChanged] with static field named [.lastKnownLocation] if it is not null
     * @param context Context
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
        if (lastKnownLocation == null && shouldUseGPS) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
        if (lastKnownLocation == null && shouldUseNetwork) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        if (lastKnownLocation == null && shouldUsePassive) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        }
    }

    @SuppressLint("MissingPermission")
    private fun registerForLocationUpdates(provider: String) {
        if (locationManager.isProviderEnabled(provider)) {
            locationManager.requestLocationUpdates(provider, minTimeBetweenUpdates, minDistanceBetweenUpdates, listener)
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

    interface BehaviorListener {
        /**
         * See android.location.LocationListener#onProviderDisabled
         */
        fun onProviderDisabled(provider: String)

        /**
         * See android.location.LocationListener#onProviderEnabled
         */
        fun onProviderEnabled(provider: String)

        /**
         * See android.location.LocationListener#onStatusChanged
         */
        fun onStatusChanged(provider: String, status: Int, extras: Bundle)
    }

}
