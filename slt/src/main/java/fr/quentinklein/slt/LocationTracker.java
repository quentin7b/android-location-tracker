package fr.quentinklein.slt;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.util.Log;

/**
 * @author Quentin Klein <klein.quentin@gmail.com>, Yasir.Ali <ali.yasir0@gmail.com>
 *         <p>
 *         Helper that tracks user location
 *         </p>
 */
public abstract class LocationTracker implements LocationListener {

    /**
     * Tag used for logs
     */
    private static final String TAG = "LocationTracker";
    /**
     * The user location
     * This value is static because, wherever you call a LocationTracker, user location is the same
     */
    private static Location sLocation;

    /**
     * The manager used to track the sLocation
     */
    private LocationManager mLocationManagerService;
    /**
     * Indicates if Tracker is listening for updates or not
     */
    private boolean mIsListening = false;
    /**
     * Indicates if Tracker has found the location or not
     */
    private boolean mIsLocationFound = false;

    /**
     * Any context useful
     */
    private Context mContext;

    /**
     * Settings for the tracker
     */
    private TrackerSettings mTrackerSettings;

    /**
     * Default LocationTracker, uses default values for settings
     *
     * @param context Android context, uiContext is not mandatory.
     */
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    public LocationTracker(@NonNull Context context) {
        this(context, TrackerSettings.DEFAULT);
    }

    /**
     * Customized LocationTracker, uses the specified services and starts listening for a location.
     *
     * @param context         Android context, uiContext is not mandatory.
     * @param trackerSettings {@link TrackerSettings}, the tracker settings
     */
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    public LocationTracker(@NonNull Context context, @NonNull TrackerSettings trackerSettings) {
        this.mContext = context;
        this.mTrackerSettings = trackerSettings;

        // Get the location manager
        this.mLocationManagerService = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // default
        if (sLocation == null && trackerSettings.shouldUseGPS()) {
            LocationTracker.sLocation = mLocationManagerService.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (sLocation == null && trackerSettings.shouldUseNetwork()) {
            LocationTracker.sLocation = mLocationManagerService.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (sLocation == null && trackerSettings.shouldUsePassive()) {
            LocationTracker.sLocation = mLocationManagerService.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
    }

    /**
     * Deprecated since 3.1
     * Used to make the tracker listening for location updates
     *
     * @see #startListening()
     */
    @Deprecated
    public final void startListen() {
        startListening();
    }

    /**
     * Make the tracker listening for location updates
     */
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    public final void startListening() {
        if (!mIsListening) {
            Log.i(TAG, "LocationTracked is now listening for location updates");
            // Listen for GPS Updates
            if (mTrackerSettings.shouldUseGPS()) {
                if (LocationUtils.isGpsProviderEnabled(mContext)) {
                    mLocationManagerService.requestLocationUpdates(LocationManager.GPS_PROVIDER, mTrackerSettings.getTimeBetweenUpdates(), mTrackerSettings.getMetersBetweenUpdates(), this);
                } else {
                    onProviderError(new ProviderError(LocationManager.GPS_PROVIDER, "Provider is not enabled"));
                }
            }
            // Listen for Network Updates
            if (mTrackerSettings.shouldUseNetwork()) {
                if (LocationUtils.isNetworkProviderEnabled(mContext)) {
                    mLocationManagerService.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mTrackerSettings.getTimeBetweenUpdates(), mTrackerSettings.getMetersBetweenUpdates(), this);
                } else {
                    onProviderError(new ProviderError(LocationManager.NETWORK_PROVIDER, "Provider is not enabled"));
                }
            }
            // Listen for Passive Updates
            if (mTrackerSettings.shouldUsePassive()) {
                if (LocationUtils.isPassiveProviderEnabled(mContext)) {
                    mLocationManagerService.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, mTrackerSettings.getTimeBetweenUpdates(), this.mTrackerSettings.getMetersBetweenUpdates(), this);
                } else {
                    onProviderError(new ProviderError(LocationManager.PASSIVE_PROVIDER, "Provider is not enabled"));
                }
            }
            mIsListening = true;

            // If user has set a timeout
            if (mTrackerSettings.getTimeout() != -1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!mIsLocationFound && mIsListening) {
                            Log.i(TAG, "No location found in the meantime");
                            stopListening();
                            onTimeout();
                        }
                    }
                }, mTrackerSettings.getTimeout());
            }
        } else {
            Log.i(TAG, "Relax, LocationTracked is already listening for location updates");
        }
    }

    /**
     * Deprecated since 3.1
     * Used to make the tracker stops listening for location updates
     *
     * @see #stopListening()
     */
    @Deprecated
    public final void stopListen() {
        stopListening();
    }

    /**
     * Make the tracker stops listening for location updates
     */
    public final void stopListening() {
        if (mIsListening) {
            Log.i(TAG, "LocationTracked has stopped listening for location updates");
            mLocationManagerService.removeUpdates(this);
            mIsListening = false;
        } else {
            Log.i(TAG, "LocationTracked wasn't listening for location updates anyway");
        }
    }

    /**
     * Best effort, it calls {@link #onLocationChanged(Location)} with static field named {@link #sLocation} if it is not null
     */
    public final void quickFix() {
        if (LocationTracker.sLocation != null) {
            onLocationChanged(LocationTracker.sLocation);
        }
    }

    /**
     * Getter used to know if the Tracker is listening at this time.
     *
     * @return true if the tracker is listening, false otherwise
     */
    public final boolean isListening() {
        return mIsListening;
    }

    /**
     * Called when the tracker had found a location
     *
     * @see android.location.LocationListener#onLocationChanged(android.location.Location)
     */
    @Override
    public final void onLocationChanged(@NonNull Location location) {
        Log.i(TAG, "Location has changed, new location is " + location);
        LocationTracker.sLocation = new Location(location);
        mIsLocationFound = true;
        onLocationFound(location);
    }

    /**
     * Called when the tracker had found a location
     *
     * @param location the found location
     */
    public abstract void onLocationFound(@NonNull Location location);

    /**
     * Called when the tracker had not found any location and the timeout just happened
     */
    public abstract void onTimeout();

    /**
     * Called when a provider has been disabled.
     * By default, this method do nothing but a Log on i
     *
     * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
     */
    @Override
    public void onProviderDisabled(@NonNull String provider) {
        // By default do nothing but log
        Log.i(TAG, "Provider (" + provider + ") has been disabled");
    }

    /**
     * Called when a provider has been enabled.
     * By default, this method do nothing but a Log on i
     *
     * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
     */
    @Override
    public void onProviderEnabled(@NonNull String provider) {
        // By default do nothing but log
        Log.i(TAG, "Provider (" + provider + ") has been enabled");
    }

    /**
     * Called when status has changed.
     * By default, this method do nothing but a Log on i
     *
     * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
     */
    @Override
    public void onStatusChanged(@NonNull String provider, int status, Bundle extras) {
        // By default do nothing but log
        Log.i(TAG, "Provider (" + provider + ") status has changed, new status is " + status);
    }

    /**
     * Triggered when there was an error with a provider, most of the time, when this one is not enabled
     *
     * @param providerError the provider error
     */
    public void onProviderError(@NonNull ProviderError providerError) {
        // By default do nothing but log
        Log.w(TAG, "Provider (" + providerError.getProvider() + ")", providerError);
    }

}