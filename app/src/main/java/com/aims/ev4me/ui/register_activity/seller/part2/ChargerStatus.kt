package com.aims.ev4me.ui.register_activity.seller.part2

import com.google.android.gms.maps.model.LatLng

data class ChargerStatus(val addressString: String, val addressLatLng: LatLng) {
    var averageRating: Int=3
    var numRatings: Int=0
    var chargerPrice: Int=0
    var isChargerUsed: Boolean=false
    var chargerType: ChargerInfo.ChargerType=ChargerInfo.ChargerType.NO_LEVEL

}


