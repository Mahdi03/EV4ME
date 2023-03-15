package com.aims.ev4me.ui.main_activity.home_page

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.aims.ev4me.R
import com.aims.ev4me.ui.register_activity.seller.part2.ChargerListing
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.Marker
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ChargerListingInfoWindowAdapter(var context: Context) : InfoWindowAdapter {

    private var mainView: View =
        LayoutInflater.from(context)
            .inflate(R.layout.google_maps_marker_info_window_container, null)

    private fun renderWindow(marker: Marker) {
        val chargerListing = marker.snippet?.let { Json.decodeFromString<ChargerListing>(it) }!!
        //Set chargerListingTitle
        val titleTextView = mainView.findViewById<TextView>(R.id.chargerListingTitle)
        titleTextView.text = chargerListing.chargerName
        //Set chargerListingAddress
        val addressTextView = mainView.findViewById<TextView>(R.id.chargerListingAddress)
        addressTextView.text = chargerListing.addressString
        //Set chargerListingType
        val chargerTypeTextView = mainView.findViewById<TextView>(R.id.chargerListingType)
        chargerTypeTextView.text = "Type: ${chargerListing.chargerType.toReadableString()}"

    }

    override fun getInfoContents(p0: Marker): View {
        renderWindow(p0)
        return mainView
    }

    override fun getInfoWindow(p0: Marker): View {
        renderWindow(p0)
        return mainView
    }
}