package com.aims.ev4me.ui.register_activity.allUsers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aims.ev4me.databinding.FragmentRegistrationAllusersPart1Binding

class Registration_allUsersPart1Fragment : Fragment() {
    private var _binding: FragmentRegistrationAllusersPart1Binding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationAllusersPart1Binding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}