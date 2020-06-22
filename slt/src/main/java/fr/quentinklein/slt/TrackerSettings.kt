package fr.quentinklein.slt

/**
 * Build the settings for the tracker
 * @param timeBetweenUpdates The minimum time interval between location updates, in milliseconds by default its value is 5 minutes
 * @param metersBetweenUpdates The minimum distance between location updates in meters, by default its value is 100m
 * @param shouldUseGPS Specifies if tracker should use the GPS (default is true)
 * @param shouldUseNetwork Specifies if tracker should use the Network (default is true)
 * @param shouldUsePassive Specifies if tracker should use the Passive provider (default is true)
 */
class TrackerSettings(
        val timeBetweenUpdates: Long = 5 * 60 * 1000.toLong(),
        val metersBetweenUpdates: Float = 100f,
        val shouldUseGPS: Boolean = true,
        val shouldUseNetwork: Boolean = true,
        val shouldUsePassive: Boolean = true
)