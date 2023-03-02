package com.aims.ev4me.ui.register_activity.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aims.ev4me.databinding.FragmentRegistrationSellerPart1Binding

class Registration_sellerPart1Fragment : Fragment() {

    private var _binding: FragmentRegistrationSellerPart1Binding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationSellerPart1Binding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    }