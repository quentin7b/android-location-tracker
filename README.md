android-location-tracker
========================

Android Simple Location Tracker is an Android library that helps you get user location with a object named `LocationTracker`

JavaDoc can be found at [quentinklein.fr/aslt/javadoc](http://quentinklein.fr/aslt/javadoc)

### Installation

Either this repository and import the `slt` module as a module dependency in `Android Studio`
Or add this to your `build.gradle` file

	repositories {
	    maven {
	        url "https://jitpack.io"
	    }
	}
	
	dependencies {
	        compile 'com.github.quentin7b:android-location-tracker:2.0'
	}

Dont forget to add the following permissions to your *AndroidManifest.xml*

	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

### Use

As its name says, it's a *simple* library.
To create a tracker you just need to add the below code in your Android Activity/Service

	// You can pass an ui Context but it is not mandatory getApplicationContext() would also works
	new LocationTracker(context) {
		
		@Override
		public void onLocationFound(Location location) {
			// Do some stuff
		}
	}

And it's done, as soon as a location has been found, it will call the `onLocationFound()` method and you can do the job.

### Provide custom use

You can call a `LocationTracker` with custom parameters.
To do this, use the following constructor

	LocationTracker(Context context, TrackerSettings settings)

As an example, considering my `Activity` is named *MyActivity*

	TrackerSettings settings = new TrackerSettings();
	settings.setUseGPS(true);
	settings.setUseNetwork(false);
	settings.setUsePassive(false);
	new LocationTracker(MyActivity.this, settings) {
		
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

To specify those parameters, `LocationTracker` you can set more settings.
Here is an example of call:
	
	TrackerSettings settings = new TrackerSettings();
	settings.setUseGPS(true);
	settings.setUseNetwork(true);
	settings.setUsePassive(true);
	settings.setTimeBetweenUpdates(30 * 60 * 1000);
	settings.setMetersBetweenUpdates(100);
	new LocationTracker(this, settings) {
		
		@Override
		public void onLocationFound(Location location) {
			// Do some stuff when a new location has been found.
		}
	}

In this case, when a location is found, the tracker will not call `onLocationFound()` again during *30 minutes*.
Moreover, if the distance between the new location and the older one is less than 100m, `onLocationFound()` will not be called.

Be aware that the *time* parameter's priority is higher than the *distance* parameter. So even if the user has moved from 2km, the `tracker` will call `onLocationFound()` only after *30 minutes*.

### Manage the tracker

By default, after a `LocationTracker` is created, it automatically starts listening to updates... and never stops.
`LocationTracker` has two methods to *start* and *stop* listening for updates.

If you want to *stop* listening for updates, just call the `stopListen()` method.
For example, if you need a *one shot* position, you can do that:

	new LocationTracker(MyActivity.this) {
		
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

---
v2.0 New settings version
---

Add the `TrackerSettings` object to handle the multiple settings instead of parameters.
Add a default `timeout` of `1min` before the tracker stops itself if no location found.

---
v1.1 Adds
---

### QuickFix

`LocationTracker` has a new method called `quickFix()`.

The only thing this method is doing is calling `onLocationChanged()` with the static field `location` as a parameter.

For example, if in your code, you called a `LocationTracker` at a time and did something with the location.... Now you call it again, in a new instance of `LocationTracker`, and you want a location as quick as possible. 
Just call `quickFix()` to get the last known location.

Here is a code example

	new LocationTracker(context){
		@Override
		public void onLocationFound(Location location) {
			// Do some stuff when a new GPS Location has been found
		}
	}.quickFix();

Or if it is stored in a variable

	LocationTracker myTracker = new LocationTracker(context){
		@Override
		public void onLocationFound(Location location) {
			// Do some stuff when a new GPS Location has been found
		}
	};

	myTracker.quickFix();

### Contact & Questions

If you have any questions, fell free to send me a mail.
You can also fork this project, or open an issue :)
