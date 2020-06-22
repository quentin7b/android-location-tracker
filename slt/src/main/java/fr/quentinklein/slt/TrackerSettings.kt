package fr.quentinklein.slt

import android.support.annotation.FloatRange
import android.support.annotation.IntRange

/**
 * @author Quentin Klein <klein.quentin></klein.quentin>@gmail.com>, Yasir.Ali <ali.yasir0></ali.yasir0>@gmail.com>
 *
 *
 * Helps the LocationTracker to set the attributes:
 *
 *  * useGPS  * true if GPS usage is wanted * false otherwise
 *  * useNetwork  * true if Network usage is wanted * false otherwise
 *  * usePassive  * true if Passive usage is wanted * false otherwise
 *  * minTimeBetweenUpdates the minimum time interval between location updates in milliseconds
 *  * minMetersBetweenUpdates the minimum distance between location updates, in meters
 *  * timeout the minimum time delay before the tracker stops scanning for location in milliseconds
 *
 *
 */
class TrackerSettings {
    /**
     * The minimum time interval between location updates, in milliseconds by default its value is [.DEFAULT_MIN_TIME_BETWEEN_UPDATES]
     */
    private var mTimeBetweenUpdates: Long = -1

    /**
     * The minimum distance between location updates in meters, by default its value is [.DEFAULT_MIN_METERS_BETWEEN_UPDATES]
     */
    private var mMetersBetweenUpdates = -1f

    /**
     * The value of mTimeout to stop the listener after a specified time in case the listener is unable to get the location for a specified time
     */
    private var mTimeout = -1

    /**
     * Specifies if tracker should use the GPS (default is true)
     */
    private var mUseGPS = true

    /**
     * Specifies if tracker should use the Network (default is true)
     */
    private var mUseNetwork = true

    /**
     * Specifies if tracker should use the Passive provider (default is true)
     */
    private var mUsePassive = true

    /**
     * Set the delay between updates of the location
     *
     * @param timeBetweenUpdates the delay between the updates
     * @return the instance of TrackerSettings
     */
    fun setTimeBetweenUpdates(@FloatRange(from = 1) timeBetweenUpdates: Long): TrackerSettings {
        if (timeBetweenUpdates > 0) {
            mTimeBetweenUpdates = timeBetweenUpdates
        }
        return this
    }

    val timeBetweenUpdates: Long
        get() = if (mTimeBetweenUpdates <= 0) DEFAULT_MIN_TIME_BETWEEN_UPDATES else mTimeBetweenUpdates

    /**
     * Set the distance between updates of the location
     *
     * @param metersBetweenUpdates the distance between the updates
     * @return the instance of TrackerSettings
     */
    fun setMetersBetweenUpdates(@FloatRange(from = 1) metersBetweenUpdates: Float): TrackerSettings {
        if (metersBetweenUpdates > 0) {
            mMetersBetweenUpdates = metersBetweenUpdates
        }
        return this
    }

    val metersBetweenUpdates: Float
        get() = if (mMetersBetweenUpdates <= 0) DEFAULT_MIN_METERS_BETWEEN_UPDATES else mMetersBetweenUpdates

    /**
     * Set the timeout before giving up if no updates
     *
     * @param timeout the timeout before giving up
     * @return the instance of TrackerSettings
     */
    fun setTimeout(@IntRange(from = 1) timeout: Int): TrackerSettings {
        if (timeout > 0) {
            mTimeout = timeout
        }
        return this
    }

    val timeout: Int
        get() = if (mTimeout <= -1) DEFAULT_TIMEOUT else mTimeout

    /**
     * Set the usage of the GPS for the tracking
     *
     * @param useGPS true if the tracker should use the GPS, false if not
     * @return the instance of TrackerSettings
     */
    fun setUseGPS(useGPS: Boolean): TrackerSettings {
        mUseGPS = useGPS
        return this
    }

    fun shouldUseGPS(): Boolean {
        return mUseGPS
    }

    /**
     * Set the usage of network for the tracking
     *
     * @param useNetwork true if the tracker should use the network, false if not
     * @return the instance of TrackerSettings
     */
    fun setUseNetwork(useNetwork: Boolean): TrackerSettings {
        mUseNetwork = useNetwork
        return this
    }

    fun shouldUseNetwork(): Boolean {
        return mUseNetwork
    }

    /**
     * Set the usage of the passive sensor for the tracking
     *
     * @param usePassive true if the tracker should listen to passive updates, false if not
     * @return the instance of TrackerSettings
     */
    fun setUsePassive(usePassive: Boolean): TrackerSettings {
        mUsePassive = usePassive
        return this
    }

    fun shouldUsePassive(): Boolean {
        return mUsePassive
    }

    companion object {
        /**
         * Basic tracker settings, with all the default parameters
         *
         *  * 5min between updates
         *  * 100m between updates
         *  * 100m between updates
         *  * 1m timeout
         *  * Uses Network
         *  * Uses Passive
         *
         */
        val DEFAULT = TrackerSettings()

        /**
         * The default time interval between location updates
         * Its value is 5 minutes
         */
        const val DEFAULT_MIN_TIME_BETWEEN_UPDATES = 5 * 60 * 1000.toLong()

        /**
         * The default distance between location updates
         * Its value is 100m
         */
        const val DEFAULT_MIN_METERS_BETWEEN_UPDATES = 100f

        /**
         * The default value of timeout that helps to stop the listener if the listener is taking too much time
         * Its value is 1 minutes
         */
        const val DEFAULT_TIMEOUT = 60 * 1000
    }
}