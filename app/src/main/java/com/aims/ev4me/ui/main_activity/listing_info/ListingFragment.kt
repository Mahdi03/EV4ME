package com.aims.ev4me.ui.main_activity.listing_info


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.aims.ev4me.databinding.FragmentListingBinding
import com.aims.ev4me.ui.register_activity.seller.part2.ChargerListing
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

        val chargerListingInfoJsonString = navigationArgs.chargerListingInfo
        val chargerListing = Json.decodeFromString<ChargerListing>(chargerListingInfoJsonString)

        binding.AddressActual.text = chargerListing.addressString
        binding.SpeedActual.text = chargerListing.chargerType.toReadableString()
        binding.RatingActual.text = chargerListing.averageRating.toString()

        return root
    }


}