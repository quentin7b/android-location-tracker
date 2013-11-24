android-location-tracker
========================

Android Simple Location Tracker is an Android library that helps you get user location with a object named `LocationTracker`

JavaDoc can be found at [quentinklein.fr/aslt/javadoc](http://quentinklein.fr/aslt/javadoc)

### Installation

To install Android Location Tracker, just download the *.jar* file at [quentinklein.fr/aslt/jar](http://quentinklein.fr/aslt/jar)

For now the only version is *androidsimplelocationtracker-v1.jar*

Copy the *androidsimplelocationtracker-vX.jar* file in your Android project *libs/* folder.
Add it to your *Build Path*.

Dont forget to add the following permissions to your *AndroidManifest.xml*

	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

### Use

As its name says, it's a *simple* library.
To create a tracker you just need to add the below code in your Android Activity/Service

	// In this case, this is a context, 
	// you can pass an ui Context but it is not mandatory getApplicationContext()
	// would also works
	new LocationTracker(this) {
		
		@Override
		public void onLocationFound(Location location) {
			// Do some stuff
		}
	}

And it's done, as soon as a location has been found, it will call the `onLocationFound()` method and you can do the job.

### Providers custom use

You can call a `LocationTracker` with custom parameters.
To do this, use the following constructor

	LocationTracker(Context context, boolean useGPS, boolean useNetwork, boolean usePassive)

As an example, considering my `Activity` is named *MyActivity*

	new LocationTracker(MyActivity.this, true, false, false) {
		
		@Override
		public void onLocationFound(Location location) {
			// Do some stuff when a new GPS Location has been found
		}
	}

This, will call a location tracker that is looking *ONLY* for *GPS* updates.
*Network* and *Passive* updates will not be catched by the Tracker.

### Providers AND metrics custom use

`LocationTracker` allows you to define some custom metrics like
<ul>
<li> The minimum time interval between location updates, in milliseconds </li>
<li> The minimum distance between location updates, in meters </li>
</ul>

To specify those parameters, `LocationTracker` has another constructor
	
	public LocationTracker(	Context context, boolean useGPS, boolean useNetwork, boolean usePassive, 
							long minTimeBetweenUpdates, float minMetersBetweenUpdates)

Here is an example of call:
	
	new LocationTracker(this, true, true, true, (30 * 60 * 1000), 100) {
		
		@Override
		public void onLocationFound(Location location) {
			// Do some stuff when a new location has been found.
		}
	}

In this case, when a location is found, the tracker will not call `onLocationFound()` again during *30 minutes*.
Moreover, if the new location distance with the older one is less than 100m, `onLocationFound()` will not be called.

Be aware that the *time* parameter's priority is higher than the *distance* parameter. So even if the user has moved from 2km, the `tracker` will call `onLocationFound()` only after *30 minutes*.

### Manage the tracker

By default, after a `LocationTracker` is created, it automatically starts listening to updates... and never stops.
`LocationTracker` has two methods to *start* and *stop* listening for updates.

If you want to *stop* listening for updates, just call the `stopListen()` method.
For example, if you need a *one shot* position, you can do that:

	new LocationTracker(MyActivity.this, true, false, false) {
		
		@Override
		public void onLocationFound(Location location) {
			// Stop listening for updates
			stopListen()
			// Do some stuff when a new GPS Location has been found
		}
	}

You can also do it in the `onPause()` Activity method if you want.

	@Override
	protected void onPause() {
		if(myTracker != null) {
			myTracker.stopListen();
		}
		super.onPause();
	}

REMEMBER! A `LocationTracker` never stops untill you tell it to do so.

You may want to start listening for updates after all. To do that, `LocationTracker` has a public method named `startListen()`, call it when you want.

For example, in the `onResume()` Activity method:

	@Override
	protected void onResume() {
		if(myTracker != null) {
			myTracker.startListen();
		}
		super.onResume();
	}

### Overriding

`LocationTracker` implements Android's [LocationListener](http://developer.android.com/reference/android/location/LocationListener.html) interface.

By default, it does nothing but logging. 

Excepts the `onLocationChanged()` method, you can override all the [LocationListener](http://developer.android.com/reference/android/location/LocationListener.html)'s metods, so here is the list:
<ul>
<li>onProviderDisabled(String provider)</li>
<li>onProviderEnabled(String provider)</li>
<li>onStatusChanged(String provider, int status, Bundle extras)</li>
</ul>

### Contact & Questions

If you have any questions, fell free to send me a mail.
You can also fork this project !