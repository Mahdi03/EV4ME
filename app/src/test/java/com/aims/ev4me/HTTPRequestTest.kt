//package com.aims.ev4me
//
//import androidx.compose.runtime.*
//import kotlinx.coroutines.launch
//import org.junit.Test
//import org.junit.Assert.*
//import io.ktor.client.*
//import io.ktor.client.request.*
//
//class HTTPRequestTest {
//
//    @Test
//    fun requestHTTP() {
//        val scope = rememberCoroutineScope()
//        var text by remember { mutableStateOf("Loading") }
//        LaunchedEffect(true) {
//            scope.launch {
//               text=sendHTTPRequest(" ")
//                println(text)
//            }
//        }
//        val apiKey = BuildConfig.POSITION_STACK_API_KEY
//    }
//}
//
//suspend fun sendHTTPRequest(url: String): String {
//    //http://api.positionstack.com/v1/forward?access_key=1e47f8b127477bed4dd7482df870ee8e&query=1600%20Pennsylvania%20Ave%20NW,%20Washington%20DC
//    //TODO: Implement this, try not to use any 3rd party libraries
//    // asynchronous if possible
//    val client = HttpClient()
//    var response1 = client.get<String>("https://example.com")
//    client.close()
//    return response1
//    //return "" //STUB
//}

package com.aims.ev4me

import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.junit.Assert.*
import org.junit.Test
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonElement

class HTTPRequestTest {

    @Test
    fun requestHTTP() {
        val apiKey = BuildConfig.POSITION_STACK_API_KEY
        val response = runBlocking { sendHTTPRequest("http://api.positionstack.com/v1/forward?access_key=$apiKey") }

        //Object that holds every value that the JSON string holds
        //All are initialized as JSONElements because sometimes the API has null values
        //Use .toType() to get it to the desired Type
        @kotlinx.serialization.Serializable
        data class Lat_Lng(val latitude: JsonElement, val longitude: JsonElement, val type: JsonElement, val name: JsonElement,
                           val number: JsonElement, val postal_code: JsonElement, val street: JsonElement, val confidence: JsonElement,
                           val region: JsonElement, val region_code: JsonElement, val county: JsonElement, val locality: JsonElement,
                           val administrative_area: JsonElement, val neighbourhood: JsonElement, val country: JsonElement,
                           val country_code: JsonElement, val continent: JsonElement, val label: JsonElement)

        //API returns a list of locations so we need to put them in a list
        @kotlinx.serialization.Serializable
        data class Lat_LngResponses(val data: List<Lat_Lng>)

        val Lat_LngResponse = Json.decodeFromString<Lat_LngResponses>(response) //list of found locations

        val Lat_Lng1 = Lat_LngResponse.data[0]   //first found location (most confident)
        val latitude = (Lat_Lng1.latitude).toString().toDouble()
        val longitude = (Lat_Lng1.longitude).toString().toDouble()

        println("Latitude=" + latitude + " Longitude=" + longitude)


    }
}

suspend fun sendHTTPRequest(url: String): String {
    val client = HttpClient()
    //TODO: Apt/Unit numbers don't work, fix that later?
    val searchQuery = "6598 Trigo Road, Isla Vista California".encodeURLQueryComponent()


    val urlFull = url+"&query="+searchQuery
    val response = client.request<String>(urlFull) {
        method= HttpMethod.Get
    }
    client.close()
    return response
}
