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
	 * The minimum amount of time between two location updates, by default its value is 60s
	 * This time is in milliseconds
	 */
	private static long timeBetweenUpdates = 1 * 60 * 1000;
	/**
	 * The minimum amount of meters between two location updates, by default its value is 100m
	 */
	private static float metersBetweenUpdates = 100;
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
		// FOA, get the vars
		this.useGPS = useGPS;
		this.useNetwork = useNetwork;
		this.usePassive = usePassive;
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
	 * Changes the minimum amount of time between to location updates
	 * @param minTimeBetweenUpdates the minimum amount of time between updates in milliseconds
	 */
	public static void setMinTimeBetweenUpdates(long minTimeBetweenUpdates){
		LocationTracker.timeBetweenUpdates = minTimeBetweenUpdates;
	}

	/**
	 * Changes the minimum amount of meters between to location updates
	 * @param minMetersBetweenUpdates the minimum amount of meters between updates
	 */
	public static void setMinMetersBetweenUpdates(float minMetersBetweenUpdates){
		LocationTracker.metersBetweenUpdates = minMetersBetweenUpdates;
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
	 * Called when the tracker had found a location
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location location) {
		Log.i(TAG, "Location has changed, new location is "+location);
		LocationTracker.location = new Location(location);
		onLocationChanged(location);
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