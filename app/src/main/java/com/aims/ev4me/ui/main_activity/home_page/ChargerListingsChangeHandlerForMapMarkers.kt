package com.aims.ev4me.ui.main_activity.home_page

import com.aims.ev4me.ui.register_activity.seller.part2.ChargerListing
import com.google.android.gms.maps.GoogleMap
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ChargerListingsChangeHandlerForMapMarkers @Inject constructor(private val googleMap: GoogleMap, private val chargerListingsHashMap: HashMap<String, ChargerListing>) {
    fun initFlow() : Flow<ArrayList<ChargerListing>> = callbackFlow {
        //TODO: Do some legit lat long calculations here and then return the result as the markers that only show up
        val set = mutableSetOf<String>()
        val arr = ArrayList<ChargerListing>()
        for (chargerListing in chargerListingsHashMap.values) {
            //TODO: Group all the lat longs with the same address together
            //We can create a hashmap and set the address there as a charger

            //For now, this filters out only one charger to display at each location,
            // usually the first one
            if (!set.contains(chargerListing.addressString)) {
                set.add(chargerListing.addressString)
                arr.add(chargerListing)
            }


        }
        trySend(arr)
        awaitClose {

        }
    }
}