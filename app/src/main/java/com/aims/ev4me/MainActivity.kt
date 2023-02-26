package com.aims.ev4me

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.aims.ev4me.databinding.ActivityMainBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }



    /* We need to get their location */

    private fun locationPermissionsGranted() = LOCATION_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, LOCATION_PERMISSIONS_REQUEST_CODE)
    }
    private fun checkLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            isWaitForAccurateLocation =
                true //Wait a couple of seconds until location is a little more accurate
        }
        val locationSettings = LocationSettingsRequest.Builder().apply {
            addLocationRequest(locationRequest)
        }.build()
        val checkLocationSettingsTask = LocationServices.getSettingsClient(this).checkLocationSettings(locationSettings)
        checkLocationSettingsTask.addOnSuccessListener {
            //TODO: Now we can use their location
        }
        checkLocationSettingsTask.addOnFailureListener{exception ->
            if (exception is ResolvableApiException) {
                //We can do something to fix the issue in settings
                try {
                    exception.startResolutionForResult(this@MainActivity, LOCATION_SETTINGS_REQUEST_CODE)
                }
                catch (sendException: IntentSender.SendIntentException) {
                    //Ignore for whatever reason
                }
            }
            else {
                //TODO: show user error prompt that app will not work on their device?
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSIONS_REQUEST_CODE -> {
                if (locationPermissionsGranted()) {
                    //TODO: Now we can get their current location and set the map's camera to it
                }
                else {
                    //Nah, run that back until they get it right
                    requestLocationPermissions()
                }
            }
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            LOCATION_SETTINGS_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        //TODO: Use the location now
                    }
                    else -> {
                        //If something went wrong, try again? this is probably poor logic but oh well
                        checkLocationSettings()
                    }
                }
            }
        }
    }



    companion object {
        //Constants for dealing with location
        private val LOCATION_PERMISSIONS = mutableListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).apply {  }.toTypedArray()
        const val LOCATION_PERMISSIONS_REQUEST_CODE = 20
        const val LOCATION_SETTINGS_REQUEST_CODE = 30

    }

}