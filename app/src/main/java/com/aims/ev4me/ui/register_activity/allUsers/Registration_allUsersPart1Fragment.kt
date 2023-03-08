package com.aims.ev4me.ui.register_activity.allUsers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aims.ev4me.databinding.FragmentRegistrationAllusersPart1Binding

class Registration_allUsersPart1Fragment : Fragment() {
    private var _binding: FragmentRegistrationAllusersPart1Binding? = null

    private val binding get() = _binding!!

    private lateinit var numCarsInput: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationAllusersPart1Binding.inflate(inflater, container, false)
        val root: View = binding.root

        val nextPageButton = binding.nextPageButton
        numCarsInput = binding.numberOfCarsInput

        nextPageButton.setOnClickListener {
            //Check whether they at least filled in a value
            if (validateInput()) {
                //Get the integer and convert it, since they passed the validation stage this should not cause any errors
                val numCars: Int = numCarsInput.text.toString().toInt()
                //This line uses the Navigation API cuz f*** me if I'm actually trying to
                // understand what this does: https://developer.android.com/guide/navigation/navigation-pass-data#groovy

                //Set navigation action and pass in data we want to send
                val action = Registration_allUsersPart1FragmentDirections
                    .actionRegisterNavigationAllUsersPart1ToRegisterNavigationAllUsersPart2(numCars)
                findNavController().navigate(action)
            }
        }

        return root
    }

    private fun validateInput(): Boolean {
        var validationPassed: Boolean = true

        if (numCarsInput.text.toString().isBlank()) {
            validationPassed = false
            numCarsInput.error = "Value cannot be empty"
        }
        if (!numCarsInput.text.toString().isDigitsOnly()) {
            validationPassed = false
            numCarsInput.error = "Value must be numbers"
        }
        try {
            if (numCarsInput.text.toString().toInt() <= 0) {
                validationPassed = false
                numCarsInput.error = "Value must be a positive nonzero number"
            }
        } catch (e: NumberFormatException) {
            validationPassed = false
            numCarsInput.error = "Value must be numbers"
        }
        return validationPassed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}