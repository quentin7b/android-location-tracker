package fr.quentinklein.slt.tracker;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * @author qklein
 * Helper that tracks user location
 *
 */
public abstract class LocationTracker implements LocationListener {

	/**
	 * Tag used for logs
	 */
	private static final String TAG = "LocationTracker";
	/**
	 * The default time interval between location updates 
	 * Its value is 5 minutes
	 */
	public static final long DEFAULT_MIN_TIME_BETWEEN_UPDATES = 5 * 60 * 1000;
	/**
	 * The default distance between location updates 
	 * Its value is 100m
	 */
	public static final float DEFAULT_MIN_METERS_BETWEEN_UPDATES = 100;

	/**
	 * The minimum time interval between location updates, in milliseconds by default its value is {@link DEFAULT_MIN_TIME_BETWEEN_UPDATES}
	 */
	private long timeBetweenUpdates;
	/**
	 * The minimum distance between location updates in meters, by default its value is {@link DEFAULT_MIN_METERS_BETWEEN_UPDATES}
	 */
	private float metersBetweenUpdates;
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
	 * Specifies if tracker should use the GPS
	 */
	private boolean useGPS;
	/**
	 * Specifies if tracker should use the Network
	 */
	private boolean useNetwork;
	/**
	 * Specifies if tracker should use the Passive provider
	 */
	private boolean usePassive;

	/**
	 * Default LocationTracker, uses GPS, Network and Passive services and starts listening for a location.
	 * @param context Android context, uiContext is not mandatory.
	 */
	public LocationTracker(Context context){
		this(context, true, true, true);
	}

	/**
	 * Customized LocationTracker, uses the specified services and starts listening for a location.
	 * @param context Android context, uiContext is not mandatory.
	 * @param useGPS <ul><li>true if GPS usage is wanted</li><li>false otherwise</li></ul>
	 * @param useNetwork <ul><li>true if Network usage is wanted</li><li>false otherwise</li></ul>
	 * @param usePassive <ul><li>true if Passive usage is wanted</li><li>false otherwise</li></ul>
	 */
	public LocationTracker(Context context, boolean useGPS, boolean useNetwork, boolean usePassive){
		this(context, useGPS, useNetwork, usePassive, DEFAULT_MIN_TIME_BETWEEN_UPDATES, DEFAULT_MIN_METERS_BETWEEN_UPDATES);
	}
	
	/**
	 * Customized LocationTracker, uses the specified services and starts listening for a location.
	 * @param context Android context, uiContext is not mandatory.
	 * @param useGPS <ul><li>true if GPS usage is wanted</li><li>false otherwise</li></ul>
	 * @param useNetwork <ul><li>true if Network usage is wanted</li><li>false otherwise</li></ul>
	 * @param usePassive <ul><li>true if Passive usage is wanted</li><li>false otherwise</li></ul>
	 * @param minTimeBetweenUpdates the minimum time interval between location updates in milliseconds
	 * @param minMetersBetweenUpdates the minimum distance between location updates, in meters
	 */
	public LocationTracker(Context context, boolean useGPS, boolean useNetwork, boolean usePassive, long minTimeBetweenUpdates, float minMetersBetweenUpdates){
		// FOA, get the vars
		this.useGPS = useGPS;
		this.useNetwork = useNetwork;
		this.usePassive = usePassive;
		// Set cusom metrics
		this.metersBetweenUpdates = minMetersBetweenUpdates;
		this.timeBetweenUpdates = minTimeBetweenUpdates;
		// Get the location manager
		this.locationManagerService = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// default
		if(location == null && useGPS) {
			LocationTracker.location = locationManagerService.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		if(location == null && useNetwork) {
			LocationTracker.location = locationManagerService.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if(location == null && usePassive) {
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
			if(locationManagerService.isProviderEnabled(LocationManager.GPS_PROVIDER) && this.useGPS){
				locationManagerService.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeBetweenUpdates, metersBetweenUpdates, this);
			} else if(this.useGPS){
				Log.i(TAG, "Problem, GPS_PROVIDER is not enabled");
			}
			// Listen for Network Updates
			if(locationManagerService.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && this.useNetwork){
				locationManagerService.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, timeBetweenUpdates, metersBetweenUpdates, this);
			} else if(this.useNetwork){
				Log.i(TAG, "Problem, NETWORK_PROVIDER is not enabled");
			}
			// Listen for Passive Updates
			if(locationManagerService.isProviderEnabled(LocationManager.PASSIVE_PROVIDER) && this.usePassive){
				locationManagerService.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, timeBetweenUpdates, metersBetweenUpdates, this);
			} else if(this.usePassive){
				Log.i(TAG, "Problem, PASSIVE_PROVIDER is not enabled");
			}
			this.isListening = true;
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
		onLocationFound(location);
	}

	/**
	 * Called when the tracker had found a location
	 * @param location the found location
	 */
	public abstract void onLocationFound(Location location);

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
