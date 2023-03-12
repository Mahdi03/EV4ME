package com.aims.ev4me

import android.util.Patterns
import com.google.android.gms.maps.model.LatLng
//import com.android.volley.toolbox.Volley
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement


fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

suspend fun convertAddressToLatLng(address: String): LatLng {
    val apiKey = BuildConfig.POSITION_STACK_API_KEY
    val response = runBlocking { sendHTTPRequestForward("http://api.positionstack.com/v1/forward?access_key=$apiKey", address) }

    //Object that holds every value that the JSON string holds
    //All are initialized as JSONElements because sometimes the API has null values
    //Use .toType() to get it to the desired Type
    @kotlinx.serialization.Serializable
    data class Lat_Lng(val latitude: JsonElement, val longitude: JsonElement, val type: JsonElement, val name: JsonElement,
                       val number: JsonElement, val postal_code: JsonElement, val street: JsonElement, val confidence: JsonElement,
                       val region: JsonElement, val region_code: JsonElement, val county: JsonElement, val locality: JsonElement,
                       val administrative_area: JsonElement, val neighbourhood: JsonElement, val country: JsonElement,
                       val country_code: JsonElement, val continent: JsonElement, val label: JsonElement
    )

    //API returns a list of locations so we need to put them in a list
    @kotlinx.serialization.Serializable
    data class Lat_LngResponses(val data: List<Lat_Lng>)

    val Lat_LngResponse = Json.decodeFromString<Lat_LngResponses>(response) //list of found locations

    val Lat_Lng1 = Lat_LngResponse.data[0]   //first found location (most confident)
    val latitude = (Lat_Lng1.latitude).toString().toDouble()
    val longitude = (Lat_Lng1.longitude).toString().toDouble()
    return LatLng(latitude,longitude)
}

suspend fun sendHTTPRequestForward(url: String, address: String): String {
    val client = HttpClient()
    val searchQuery = address.encodeURLQueryComponent()


    val urlFull = url+"&query="+searchQuery
    val response = client.request<String>(urlFull) {
        method= HttpMethod.Get
    }
    client.close()
    return response
}

