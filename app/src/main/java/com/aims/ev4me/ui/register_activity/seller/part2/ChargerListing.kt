package com.aims.ev4me.ui.register_activity.seller.part2

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable

/*
We need to define our own latlng class that has a default empty constructor
to store and pull from the realtime database so make sure to call `.getLatLng()`
before using it anywhere
 */
@Serializable
data class MyLatLng(var latitude: Double, var longitude: Double) {
    constructor() : this(0.0, 0.0)
    fun getLatLng() : LatLng {
        return LatLng(latitude, longitude)
    }
}

@Serializable
data class ChargerListing(var addressString: String, var addressLatLng: MyLatLng,
                          var chargerType: ChargerInfo.ChargerType=ChargerInfo.ChargerType.NO_LEVEL,
                          var chargerName: String
                          ) {
    //We apparently need to provide a default no argument constructor for the realtime DB
    constructor() : this("", MyLatLng(0.0, 0.0), ChargerInfo.ChargerType.NO_LEVEL, "")

    var averageRating: Int = 3
    var numRatings: Int = 0
    var chargerPrice: Int = 0
    var isChargerUsed: Boolean = false

    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "addressString" to addressString,
            "addressLatLng" to addressLatLng,
            "averageRating" to averageRating,
            "numRatings" to numRatings,
            "chargerPrice" to chargerPrice,
            "isChargerUsed" to isChargerUsed,
            "chargerType" to chargerType,
            "chargerName" to chargerName
        )
    }
}


