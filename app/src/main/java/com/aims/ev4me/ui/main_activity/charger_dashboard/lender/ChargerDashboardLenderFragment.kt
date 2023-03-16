package com.aims.ev4me.ui.main_activity.charger_dashboard.lender

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aims.ev4me.databinding.FragmentChargerDashboardLenderBinding


class ChargerDashboardLenderFragment : Fragment() {

    private var _binding: FragmentChargerDashboardLenderBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val chargerDashboardLenderViewModel =
            ViewModelProvider(this).get(ChargerDashboardLenderViewModel::class.java)

        _binding = FragmentChargerDashboardLenderBinding.inflate(inflater, container, false)
        val root: View = binding.root


        //val textView: TextView = binding.addressTextView
        //textView.text = "525 UCEN Rd, Isla Vista, CA 93117 USA"

        val latLongStr = "34.4115988,-119.8460491"
        //Set up google maps intent
        val googleMapsDirectionsIntent = Uri.parse("google.navigation:q=$latLongStr")
        val googleMapsIntent = Intent(Intent.ACTION_VIEW, googleMapsDirectionsIntent)
        googleMapsIntent.`package` = "com.google.android.apps.maps"
//        textView.setOnClickListener {
//            activity?.let { it1 -> startActivity(googleMapsIntent) }
//        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}