package com.aims.ev4me

import android.util.Patterns
import com.google.android.gms.maps.model.LatLng

fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun convertAddressToLatLng(address: String): LatLng {
    //TODO: Return LatLng using `sendHTTPRequest`
    val apiKey = BuildConfig.POSITION_STACK_API_KEY
    //Use: https://positionstack.com/documentation to set up the right email
    val latLngString: String = sendHTTPRequest("http://api.positionstack.com/v1/forward/$?access_key=$apiKey&query=$address")
    return LatLng(0.0, 0.0);
}

fun sendHTTPRequest(url: String): String {
    //TODO: Implement this, try not to use any 3rd party libraries
    // asynchronous if possible
    return ""
}