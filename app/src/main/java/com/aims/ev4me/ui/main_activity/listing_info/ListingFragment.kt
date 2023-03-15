package com.aims.ev4me.ui.main_activity.listing_info


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.aims.ev4me.databinding.FragmentListingBinding
import com.aims.ev4me.ui.register_activity.seller.part2.ChargerListing
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ListingFragment : Fragment() {

    private var _binding: FragmentListingBinding? = null

    private val binding get() = _binding!!

    private val navigationArgs : ListingFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val realtimeDB = Firebase.database.reference

        val chargerListingInfoJsonString = navigationArgs.chargerListingInfo
        val chargerListing = Json.decodeFromString<ChargerListing>(chargerListingInfoJsonString)

        binding.AddressActual.text = chargerListing.addressString
        binding.SpeedActual.text = chargerListing.chargerType.toReadableString()
        binding.RatingActual.text = chargerListing.averageRating.toString()

        binding.chargerBookingButton.setOnClickListener {
            //When we click this we want to go into realtime db listings w/ this ID and make this charger unavailable
            Log.v("ListingFragment", "Charger UID: ${chargerListing.chargerUID}")
            realtimeDB.child("Listings").child(chargerListing.chargerUID).child("isChargerUsed").setValue(true).addOnSuccessListener {
                Log.v("ListingFragment.kt", "It workeddddd!!!")
            }
            //TODO: Set this charger as their current charger and take them to charger dashboard??

        }

        return root
    }


}