package com.github.quentin7b.slt.example

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import fr.quentinklein.slt.LocationTracker
import fr.quentinklein.slt.ProviderError
import kotlinx.android.synthetic.main.example_activity.*

class ExampleActivity : AppCompatActivity() {

    private lateinit var latitudeTv: TextView
    private lateinit var longitudeTv: TextView
    private lateinit var startStopBtn: Button
    lateinit var listViewAdapter: ArrayAdapter<String>
    private var latLngStrings = ArrayList<String>()

    private var tracker: LocationTracker = LocationTracker(
        minTimeBetweenUpdates = 1000L, // one second
        minDistanceBetweenUpdates = 1F, // one meter
        shouldUseGPS = true,
        shouldUseNetwork = true,
        shouldUsePassive = true
    ).also {
        it.addListener(object : LocationTracker.Listener {
            override fun onLocationFound(location: Location) {
                val lat = location.latitude.toString();
                val lng = location.longitude.toString();

                tv_lat.text = lat;
                tv_long.text = lng;

                listViewAdapter.add("$lat, $lng");
            }

            override fun onProviderError(providerError: ProviderError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.example_activity)
        latitudeTv = findViewById(R.id.tv_lat)
        longitudeTv = findViewById(R.id.tv_long)
        startStopBtn = findViewById(R.id.btn_start_stop)
        listViewAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, latLngStrings)
        list_view.adapter = listViewAdapter
    }

    override fun onStart() {
        super.onStart()
        initTracker()
    }

    override fun onDestroy() {
        super.onDestroy()
        tracker.stopListening(clearListeners = true)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        initTracker()
    }

    private fun initTracker() {
        val hasFineLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!hasFineLocation || !hasCoarseLocation) {
            return ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1337)
        }
        startStopBtn.setOnClickListener {
            startStop()
        }

        btn_clear_list.setOnClickListener {
            listViewAdapter.clear()
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun startStop() {
        if (tracker.isListening) {
            startStopBtn.text = "Start"
            tracker.stopListening()
        } else {
            startStopBtn.text = "Stop"
            tracker.startListening(context = baseContext)
        }
    }

}