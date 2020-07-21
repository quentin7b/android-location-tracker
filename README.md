android-location-tracker
========================

Android Simple Location Tracker is an Android library that helps you get user location with a object named `LocationTracker`

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-android--location--tracker-green.svg?style=flat)](https://android-arsenal.com/details/1/2088)

### Installation

Add this to your `build.gradle` file

```gradle
repositories {
    maven {
        url "https://jitpack.io"
    }
}

dependencies {
        compile 'com.github.quentin7b:android-location-tracker:4.0'
}
```

Don't forget to add the following permissions to your *AndroidManifest.xml*

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

Be aware of `Android Marshmallow`'s new [permission system](https://developer.android.com/preview/features/runtime-permissions.html)

```xml
<permission-group
   android:name="android.permission-group.LOCATION"
   android:label="A label for your permission"
   android:description="A description for the permission" />
```

### Use

As its name says, it's a *simple* library.
To create a tracker you just need to add the below code in your Android Activity / Service / WorkManager

> Be aware, you'll have to manage runtime permissions on Manifest.permission.ACCESS_FINE_LOCATION & Manifest.permission.ACCESS_COARSE_LOCATION

#### Create the tracker

Constructor is defined as this

```kotlin
val locationTracker = LocationTracker(
    val minTimeBetweenUpdates: Long = 5 * 60 * 1000.toLong(),
    val minDistanceBetweenUpdates: Float = 100f,
    val shouldUseGPS: Boolean = true,
    val shouldUseNetwork: Boolean = true,
    val shouldUsePassive: Boolean = true
)
```

 #### Add a listener

```kotlin
locationTracker.addListener(object: Listener {

	fun onLocationFound(location: Location) {
	}

	fun onProviderError(providerError: ProviderError) {
	}

});
```


#### Start and stop listening

```kotlin
locationTracker.startListening(context)
//and
locationTracker.stopListening()
```

### Provide custom use

You can create a `LocationTracker` with custom parameters.

- `minTimeBetweenUpdates` minimum time between two locations to respect before notifying the listeners in milliseconds). Default is *5 minutes*
- `minDistanceBetweenUpdates` minimum distance between two locations to respect before notifying the listeners in meters). Default is *100 meters*
- `shouldUseGPS` specifies if the tracker should use the GPS locations or not. Default is *true*
- `shouldUseNetwork` specifies if the tracker should use the GPS locations or not. Default is *true*
- `shouldUsePassive` specifies if the tracker should use the passive locations or not. Default is *true*

With the default parameters, when a location is found, the tracker will not call `onLocationFound()` again during *5 minutes*.
Moreover, if the distance between the new location and the older one is less than *100m*, `onLocationFound()` will not be called.

> Be aware that the priority of the **time** parameter is higher than the priority of the *distance* parameter.
> So even if the user has moved from 200km, the `tracker` will call `onLocationFound()` only after *30 minutes*.

### Cleaning

> Be aware! A `LocationTracker` never stops running until you tell it to do so.

 If the tracker is running in foreground and not in a service, it might be a good idea to link to the `lifecycle`

The `stopListening` method has an optional parameter `cleanListeners` that is *false* by default.
(calling `stopListening(false)` is the same as calling `stopListening()`).

When calling `stopListening` we do not remove the listeners you've set, so they will be notified once you start listening again.
But calling `stopListening(true)` will clear the list of registered listeners (same as calling `removeListener` for every registered listener).

```kotlin
tracker.addListener(listener)
tracker.startListening(context)
// listener will be notified
...
tracker.stopListening()
// listener won't receive updated
tracker.startListening(context)
// listener will be notified
...
tracker.stopListening(cleanListeners = true)
// listener won't receive updated for ever
tracker.startListening(context)
// listener won't be notified
```
