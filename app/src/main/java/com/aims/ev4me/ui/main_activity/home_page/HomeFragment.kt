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
import androidx.navigation.fragment.findNavController
import com.aims.ev4me.databinding.FragmentHomeBinding
import com.aims.ev4me.ui.register_activity.seller.part2.ChargerListing
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.maps.android.ktx.cameraMoveEvents
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var currentLocation = LatLng(0.0, 0.0)

    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

    //Just start off with it initialized so that we can add stuff to it
    private var chargerListingsByID = HashMap<String, ChargerListing>()

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
        } else {
            requestLocationPermissions()
        }

        //After we request location permissions, let's load database data before init map

        //We need to fetch database info
        //val faketimeDB = Firebase.database
        //faketimeDB.useEmulator("10.0.2.2", 9000)
        val realtimeDB: DatabaseReference = Firebase.database.reference

        realtimeDB.child("Listings").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (chargerListingSnapshot in snapshot.children) {
                    //Log.i("HomeFragment.kt", chargerListingSnapshot.value.toString())

                    //Temporarily add the charger id inside the listing for when we only pass this in
                    val chargerListingID = chargerListingSnapshot.key as String
                    val chargerListing = chargerListingSnapshot.getValue<ChargerListing>()!!
                    chargerListing.chargerUID = chargerListingID
                    chargerListingsByID[chargerListingID] = chargerListing
                }
                collectPoints()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment.kt", "The database listener was cancelled", error.toException())
            }

        })


        val databaseChildEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //We need to add this to our general main object
                val newChargerListing = snapshot.getValue<ChargerListing>()!!
                val chargerListingKeyToAdd = snapshot.key!!
                chargerListingsByID[chargerListingKeyToAdd] = newChargerListing
                //TODO: send the general object back to the flow
                collectPoints()
                Log.i("HomeFragment.kt", "Child added")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //we need to reflect this child's changes in the general main object
                val newChargerListing = snapshot.getValue<ChargerListing>()!!
                val chargerListingKeyToChange = snapshot.key!!
                chargerListingsByID[chargerListingKeyToChange] = newChargerListing
                //TODO: send the general object back to the flow
                collectPoints()
                Log.i("HomeFragment.kt", "Child changed")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                //remove this child from our general main object
                val chargerListingKeyToRemove = snapshot.key!!
                chargerListingsByID.remove(chargerListingKeyToRemove)
                //TODO: send back to flow
                collectPoints()
                Log.i("HomeFragment.kt", "Child removed")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //I don't think this one really matters for us???
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment.kt", "The database listener was cancelled", error.toException())
            }

        }

        realtimeDB.child("Listings").addChildEventListener(databaseChildEventListener)

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
        try {
            mapView.onSaveInstanceState(mapViewBundle)
        } catch (e: Exception) {
            Log.v("HomeFragment.kt", "IDC")
            e.printStackTrace()
        }
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

        if (locationPermissionsGranted()) {
            try {
                googleMap!!.isMyLocationEnabled = true
            } catch (e: SecurityException) {
                //They did not grant permissions, call failed
                Log.e("HomeFragment.kt", "Location permissions were not granted", e)
                requestLocationPermissions()
            }
        }
        updateCurrentLocation()

        collectPoints()
        //Use gesture-driven events to keep track of when they move the map as to update the map
        var touchMovementFlag: Boolean = false
        googleMap!!.setOnCameraMoveStartedListener {reason ->
            when (reason) {
                OnCameraMoveStartedListener.REASON_GESTURE -> {
                    touchMovementFlag = true
                }
            }

        }
        googleMap!!.setOnCameraIdleListener {
            if (touchMovementFlag) {
                //Here we want to update the flow again and then change the google map marker
                collectPoints()
                touchMovementFlag = false
            }
        }
        lifecycleScope.launchWhenCreated {
            googleMap!!.cameraMoveEvents().collect {
                //TODO: This will be called every time we move the map

                //Log.v("HomeFragment", "We moved the mappp")
            }

        }
        googleMap!!.setInfoWindowAdapter(context?.let { ChargerListingInfoWindowAdapter(it) })
        googleMap!!.setOnInfoWindowClickListener {
            //Open the listing fragment and pass the it.snippet string as JSON to it
            val jsonString = it.snippet
            val action = jsonString?.let { it1 ->
                HomeFragmentDirections.actionNavigationHomeToChargerListingInfo(it1)
            }
            if (action != null) {
                findNavController().navigate(action)
            }
        }
    }

    private fun collectPoints() {
        googleMap?.let {
            lifecycleScope.launch {
                ChargerListingsChangeHandlerForMapMarkers(googleMap!!, chargerListingsByID).initFlow()
                    .collect {listOfChargerListingsToPlaceOnMap ->
                        placeMarkersOnMapFromChargerListings(listOfChargerListingsToPlaceOnMap)
                    }
            }
        }
    }

    private fun placeMarkersOnMapFromChargerListings(listOfChargerListingsToPlaceOnMap: ArrayList<ChargerListing>) {
        //We don't want to update any markers that already exist unnecessarily
        googleMap!!.clear()
        for (chargerListing in listOfChargerListingsToPlaceOnMap) {
            googleMap!!.addMarker(MarkerOptions()
                .position(chargerListing.addressLatLng.getLatLng())
                .title(chargerListing.chargerName)
                    //Send over all chargerListing info to window adapter using JSON serialization
                .snippet(Json.encodeToString(ChargerListing.serializer(), chargerListing))
                .icon(BitmapDescriptorFactory.defaultMarker(
                    if (chargerListing.isChargerUsed) {BitmapDescriptorFactory.HUE_MAGENTA} else {BitmapDescriptorFactory.HUE_RED}
                ))
            )
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
                        } catch (e: NullPointerException) {
                            Log.e(
                                "HomeFragment.kt",
                                "Probably tried to update the map's location when the user already selected another page",
                                e
                            )
                        }

                    } else {
                        //Try again since it was null last time
                        updateCurrentLocation()
                    }
                }
        } catch (e: SecurityException) {
            //They did not grant permissions, call failed
            Log.e("HomeFragment.kt", "Location permissions were not granted", e)
            requestLocationPermissions()
        }
    }

    private fun locationPermissionsGranted() = LOCATION_PERMISSIONS.all { it ->
        activity?.let { itt ->
            ContextCompat.checkSelfPermission(
                itt.baseContext,
                it
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {

        } else {

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
        } else {
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
        val checkLocationSettingsTask = activity?.let {
            LocationServices.getSettingsClient(it).checkLocationSettings(locationSettings)
        }
        checkLocationSettingsTask?.addOnSuccessListener {
            //Now we can use their location
            updateCurrentLocation()
        }
        checkLocationSettingsTask?.addOnFailureListener { exception ->
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
                } else {
                    //Nah, run that back until they get it right
                    requestLocationPermissions()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
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
        ).apply { }.toTypedArray()
        private const val LOCATION_PERMISSIONS_REQUEST_CODE = 20
        private const val LOCATION_SETTINGS_REQUEST_CODE = 30

        //Constants working with Google Maps

        private const val GOOGLE_MAPS_ZOOM =
            12.5F //TODO: Customize the zoom to the right level of default zoom
    }

}