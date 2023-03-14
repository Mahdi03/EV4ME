package com.aims.ev4me.ui.main_activity.charger_dashboard.lender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aims.ev4me.databinding.FragmentChargerDashboardLenderBinding
import com.aims.ev4me.databinding.FragmentChargerDashboardUserBinding

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

        val textView: TextView = binding.AddressTextView
        textView.text = "<a href = 'google.com'> 149 Temp Address Dr., Change this-CA, 94582 </a>"
        textView.setMovementMethod(LinkMovementMethod.getInstance())
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}