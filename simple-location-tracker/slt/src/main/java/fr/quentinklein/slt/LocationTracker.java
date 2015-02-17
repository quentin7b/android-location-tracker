package fr.quentinklein.slt.tracker;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * @author qklein, yasiralijaved
 * Helper that tracks user location
 *
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
	private static Location location;
	
	/**
	 * The manager used to track the location
	 */
	private LocationManager locationManagerService;
	/**
	 * Indicates if Tracker is listening for updates or not
	 */
	private boolean isListening = false;
	/**
	 * Indicates if Tracker has found the location or not
	 */
	private boolean isLocationFound = false;
	
	private TrackerSettings mTrackerSettings;

	
	
	/**
	 * Customized LocationTracker, uses the specified services and starts listening for a location.
	 * @param context Android context, uiContext is not mandatory.
	 * @param trackerSettings {@link TrackerSettings.java}
	 */
	public LocationTracker(Context context, TrackerSettings trackerSettings){
		
		this.mTrackerSettings = trackerSettings;
		
		// Get the location manager
		this.locationManagerService = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// default
		if(location == null && trackerSettings.shouldUseGPS()) {
			LocationTracker.location = locationManagerService.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		if(location == null && trackerSettings.shouldUseNetwork()) {
			LocationTracker.location = locationManagerService.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if(location == null && trackerSettings.shouldUsePassive()) {
			LocationTracker.location = locationManagerService.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		}
		// Start Listen for updates
		startListen();
	}

	/**
	 * Make the tracker listening for location updates
	 */
	public final void startListen(){
		if(!this.isListening) {
			Log.i(TAG, "LocationTracked is now listening for location updates");
			// Listen for GPS Updates
			if(locationManagerService.isProviderEnabled(LocationManager.GPS_PROVIDER) && this.mTrackerSettings.shouldUseGPS()){
				locationManagerService.requestLocationUpdates(LocationManager.GPS_PROVIDER, this.mTrackerSettings.getTimeBetweenUpdates(), this.mTrackerSettings.getMetersBetweenUpdates(), this);
			} else if(this.mTrackerSettings.shouldUseGPS()){
				Log.i(TAG, "Problem, GPS_PROVIDER is not enabled");
			}
			// Listen for Network Updates
			if(locationManagerService.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && this.mTrackerSettings.shouldUseNetwork()){
				locationManagerService.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, this.mTrackerSettings.getTimeBetweenUpdates(), this.mTrackerSettings.getMetersBetweenUpdates(), this);
			} else if(this.mTrackerSettings.shouldUseNetwork()){
				Log.i(TAG, "Problem, NETWORK_PROVIDER is not enabled");
			}
			// Listen for Passive Updates
			if(locationManagerService.isProviderEnabled(LocationManager.PASSIVE_PROVIDER) && this.mTrackerSettings.shouldUsePassive()){
				locationManagerService.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, this.mTrackerSettings.getTimeBetweenUpdates(), this.mTrackerSettings.getMetersBetweenUpdates(), this);
			} else if(this.mTrackerSettings.shouldUsePassive()){
				Log.i(TAG, "Problem, PASSIVE_PROVIDER is not enabled");
			}
			this.isListening = true;
			
			new Handler().postDelayed(new Runnable() {
				  @Override
				  public void run() {
				    if(!isLocationFound && isListening && mTrackerSettings.getTimeout() != -1) {
				      Log.i(TAG, "No location found in the meantime");
				      LocationTracker.this.stopListen();
				      onTimeout();
				    }
				  }
				}, mTrackerSettings.getTimeout());
		} else {
			Log.i(TAG, "Relax, LocationTracked is already listening for location updates");
		}
	}

	/**
	 * Make the tracker stops listening for location updates
	 */
	public final void stopListen(){
		if(this.isListening){
			Log.i(TAG, "LocationTracked has stopped listening for location updates");
			locationManagerService.removeUpdates(this);
			this.isListening = false;
		} else {
			Log.i(TAG, "LocationTracked wasn't listening for location updates anyway");
		}
	}
	
	/**
	 * Best effort, it calls {@link #onLocationChanged(Location)} with static field named {@link #location} if it is not null
	 */
	public final void quickFix(){
		if(LocationTracker.location != null){
			onLocationChanged(LocationTracker.location);
		}
	}

	/**
	 * Called when the tracker had found a location
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public final void onLocationChanged(Location location) {
		Log.i(TAG, "Location has changed, new location is "+location);
		LocationTracker.location = new Location(location);
		isLocationFound = true;
		onLocationFound(location);
	}

	/**
	 * Called when the tracker had found a location
	 * @param location the found location
	 */
	public abstract void onLocationFound(Location location);
	
	/**
	 * Called when the tracker had not found any location and the timeout just happened
	 */
	public abstract void onTimeout();

	/** 
	 * Called when a provider has been disabled.
	 * By default, this method do nothing but a Log on i
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String provider) {
		// By default do nothing but log
		Log.i(TAG, "Provider ("+provider+") has been disabled");
	}

	/** 
	 * Called when a provider has been enabled.
	 * By default, this method do nothing but a Log on i
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String provider) {
		// By default do nothing but log
		Log.i(TAG, "Provider ("+provider+") has been enabled");		
	}

	/** 
	 * Called when status has changed.
	 * By default, this method do nothing but a Log on i
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// By default do nothing but log
		Log.i(TAG, "Provider ("+provider+") status has changed, new status is "+status);	
	}

}
