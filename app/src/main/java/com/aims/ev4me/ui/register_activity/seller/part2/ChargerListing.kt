package com.aims.ev4me.ui.register_activity.seller.part2

import com.google.android.gms.maps.model.LatLng

data class ChargerListing(var addressString: String, var addressLatLng: LatLng,
                          var chargerType: ChargerInfo.ChargerType=ChargerInfo.ChargerType.NO_LEVEL,
                          var chargerName: String) {
    var averageRating = 3
    var numRatings = 0
    var chargerPrice = 0
    var isChargerUsed = false
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


