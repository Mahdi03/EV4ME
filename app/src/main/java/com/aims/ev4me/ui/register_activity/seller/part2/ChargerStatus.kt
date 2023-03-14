package com.aims.ev4me.ui.register_activity.seller.part2

import com.google.android.gms.maps.model.LatLng

data class ChargerStatus(val listingID: String) {
    var addressString: String=""
    var addressLatLng: LatLng=LatLng(0.0,0.0)
    var averageRating: Int=3
    var numRatings: Int=0
    var chargerPrice: Int=0
    var isChargerUsed: Boolean=false
    var chargerType: ChargerInfo.ChargerType=ChargerInfo.ChargerType.NO_LEVEL
    var chargerName: String=""
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


