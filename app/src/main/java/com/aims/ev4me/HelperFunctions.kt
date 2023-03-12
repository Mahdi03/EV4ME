package com.aims.ev4me

import android.util.Patterns
import com.google.android.gms.maps.model.LatLng
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * Used to check whether a string can be validified as an email
 */
fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

/**
 * Asynchronous function to convert a string address into
 * a LatLng object using the Nominatim REST API
 */
suspend fun convertAddressToLatLng(address: String): LatLng {
    val searchQuery = address.encodeURLQueryComponent()
    val url = "https://nominatim.openstreetmap.org/search?&q=$searchQuery&format=json"
    val response = sendHTTPRequest(url)
    
    //Object that holds every value that the JSON string holds
    //All are initialized as JSONElements because sometimes the API has null values
    //Use .toType() to get it to the desired Type
    @Serializable
    data class LocationObject(
        val place_id: JsonElement,
        val licence: JsonElement,
        val osm_type: JsonElement,
        val osm_id: JsonElement,
        val boundingbox: JsonElement,
        val lat: JsonElement,
        val lon: JsonElement,
        val display_name: JsonElement,
        val `class`: JsonElement,
        val type: JsonElement,
        val importance: JsonElement,
        /*
        //TODO: The API says that these values might also come
           with, find a way to handle these as well that doesn't
           break the deserializer
        val icon: JsonElement?,
        val address: JsonElement?,
        val extratags: JsonElement?
         */
    )

    //TODO: Add exception handling here in case we fail to 
    // deserialize the incoming JSON in the specified format 
    //API returns a list of locations so we need to put them in a list
    val locationObjectsArr = Json.decodeFromString<List<LocationObject>>(response) //list of found locations

    val locationObj = locationObjectsArr[0]   //first found location (most confident)
    //We need to replace the quotes because our input comes with extra quotes around the strings
    val latitude = (locationObj.lat).toString().replace("\"", "").toDouble()
    val longitude = (locationObj.lon).toString().replace("\"", "").toDouble()

    return LatLng(latitude,longitude)
}


/**
 * Sends a basic HTTP response and only returns the raw response
 * for later handling
 */
suspend fun sendHTTPRequest(url: String): String {
    val client = HttpClient()
    val response = client.request<String>(url) {
        method = HttpMethod.Get
    }
    client.close()
    return response
}

