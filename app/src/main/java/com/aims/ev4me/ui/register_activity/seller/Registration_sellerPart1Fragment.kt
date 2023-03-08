package com.aims.ev4me.ui.register_activity.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aims.ev4me.databinding.FragmentRegistrationSellerPart1Binding

class Registration_sellerPart1Fragment : Fragment() {

    private var _binding: FragmentRegistrationSellerPart1Binding? = null

    private val binding get() = _binding!!


    private lateinit var addressInput: EditText
    private lateinit var numChargersInput: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationSellerPart1Binding.inflate(inflater, container, false)
        val root: View = binding.root

        val nextPageButton = binding.nextPageButton
        addressInput = binding.inputAddress
        numChargersInput = binding.inputNumChargers

        nextPageButton.setOnClickListener {
            if (validateInput()) {

                val numChargers: Int = numChargersInput.text.toString().toInt()

                val action = Registration_sellerPart1FragmentDirections
                    .actionRegisterNavigationSellerPart1ToRegisterNavigationSellerPart2(numChargers)
                findNavController().navigate(action)
            }
        }

        return root
    }

    private fun validateInput(): Boolean {
        var validationPassed: Boolean = true

        if (addressInput.text.toString().isBlank()) {
            validationPassed = false
            addressInput.error = "Address field cannot be empty"
        }

        if (numChargersInput.text.toString().isBlank()) {
            validationPassed = false
            numChargersInput.error = "Value cannot be empty"
        }
        else if (!numChargersInput.text.toString().isDigitsOnly()) {
            validationPassed = false
            numChargersInput.error = "Value must be numbers"
        }
        else {
            try {
                if (numChargersInput.text.toString().toInt() <= 0) {
                    validationPassed = false
                    numChargersInput.error = "Value must be a positive nonzero number"
                }
            } catch (e: NumberFormatException) {
                validationPassed = false
                numChargersInput.error = "Value must be numbers"
            }
        }
        return validationPassed
    }
    
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}