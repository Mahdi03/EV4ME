package com.aims.ev4me.ui.main_activity.home_page

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aims.ev4me.databinding.FragmentHomeBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.cameraMoveEvents
import kotlinx.coroutines.ExperimentalCoroutinesApi


class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var currentLocation = LatLng(0.0, 0.0)

    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        fusedLocationProviderClient = activity?.let {
            LocationServices.getFusedLocationProviderClient(it)
        }!!

        if (locationPermissionsGranted()) {
            checkLocationSettings()
        }
        else {
            requestLocationPermissions()
        }

        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mapView = binding.mapView
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)


        return root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle: Bundle? = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }


    override fun onDestroyView() {
        googleMap = null
        mapView.onDestroy()
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }
    override fun onDestroy() {
        //mapView.onDestroy() - don't destroy the map twice..memory leaks on accident
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onMapReady(mGoogleMap: GoogleMap) {
        googleMap = mGoogleMap
        //googleMap!!.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)).title("Marker"))
        //TODO: We need to spawn the camera at the location where the user is
        if (locationPermissionsGranted()) {
            try {
                googleMap!!.isMyLocationEnabled = true
            }
            catch (e: SecurityException) {
                //They did not grant permissions, call failed
                Log.e("HomeFragment.kt", "Location permissions were not granted", e)
                requestLocationPermissions()
            }
        }
        updateCurrentLocation()
        lifecycleScope.launchWhenCreated {
            googleMap!!.cameraMoveEvents().collect {
                //TODO: This will be called every time we move the map, so use this to refresh the locations
                // of charging spots nearby (within the camera view)

                //TODO: be careful this runs every frame AS we are moving the
                // camera or when the camera automatically moves
                Log.v("HomeFragment", "We moved the mappp")
            }
        }
    }


    private fun updateCurrentLocation() {
        try {
            fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        /*
                        We wrap this call in try-catch because by the time it loads, the user
                        might be off the screen already on another page of the app and then
                        when googleMap calls the update, the fragment no longer exists and
                        throws a NullPointerException (although there could be other uses for
                        that exception so keep an eye out in Logcat.
                        */
                        try {
                            googleMap!!.moveCamera(
                                CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.Builder().apply {
                                        target(currentLocation)
                                        zoom(GOOGLE_MAPS_ZOOM)
                                    }.build()
                                )
                            )
                        }
                        catch (e: NullPointerException) {
                            Log.e("HomeFragment.kt", "Probably tried to update the map's location when the user already selected another page", e)
                        }

                    } else {
                        //Try again since it was null last time
                        updateCurrentLocation()
                    }
                }
        }
        catch (e: SecurityException) {
            //They did not grant permissions, call failed
            Log.e("HomeFragment.kt", "Location permissions were not granted", e)
            requestLocationPermissions()
        }
    }

    private fun locationPermissionsGranted() = LOCATION_PERMISSIONS.all { it ->
        activity?.let { itt -> ContextCompat.checkSelfPermission(itt.baseContext, it) } == PackageManager.PERMISSION_GRANTED
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        isGranted ->
        if (isGranted) {

        }
        else {

        }
    }
    private fun requestLocationPermissions() {

        activity?.let {
            ActivityCompat.requestPermissions(
                it,
                LOCATION_PERMISSIONS,
                LOCATION_PERMISSIONS_REQUEST_CODE
            )
        }

        LOCATION_PERMISSIONS.forEach {
            permissionLauncher.launch(it)
        }
        if (!locationPermissionsGranted()) {
            requestLocationPermissions()
        }
        else {
            updateCurrentLocation()
        }
    }

    private fun checkLocationSettings() {
        //Don't mess with this even if it says deprecated and crosses out almost
        // all of the code, it is still necessary
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            isWaitForAccurateLocation =
                false //Wait a couple of seconds until location is a little more accurate
        }
        val locationSettings = LocationSettingsRequest.Builder().apply {
            addLocationRequest(locationRequest)
        }.build()
        val checkLocationSettingsTask = activity?.let { LocationServices.getSettingsClient(it).checkLocationSettings(locationSettings) }
        checkLocationSettingsTask?.addOnSuccessListener {
            //Now we can use their location
            updateCurrentLocation()
        }
        checkLocationSettingsTask?.addOnFailureListener{ exception ->
            if (exception is ResolvableApiException) {
                //We can do something to fix the issue in settings
                try {
                    activity?.let {
                        exception.startResolutionForResult(
                            it,
                            LOCATION_SETTINGS_REQUEST_CODE
                        )
                    }
                } catch (sendException: IntentSender.SendIntentException) {
                    //Ignore for whatever reason
                }
            } else {
                //TODO: show user error prompt that app will not work on their device?
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSIONS_REQUEST_CODE -> {
                //This doesn't run because it can't find the onRequestPermissionsResult callback inside the fragment since the call is attached to an activity
                if (locationPermissionsGranted()) {
                    //TODO: Now we can get their current location and set the map's camera to it
                    updateCurrentLocation()
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
                        updateCurrentLocation()
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
        private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"


        //Constants for dealing with location
        private val LOCATION_PERMISSIONS = mutableListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).apply {  }.toTypedArray()
        private const val LOCATION_PERMISSIONS_REQUEST_CODE = 20
        private const val LOCATION_SETTINGS_REQUEST_CODE = 30

        //Constants working with Google Maps

        private const val GOOGLE_MAPS_ZOOM = 12.5F //TODO: Customize the zoom to the right level of default zoom
    }

}