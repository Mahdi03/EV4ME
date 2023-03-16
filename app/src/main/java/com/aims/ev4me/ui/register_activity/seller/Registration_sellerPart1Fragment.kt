package com.aims.ev4me.ui.register_activity.seller

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aims.ev4me.convertAddressToLatLng
import com.aims.ev4me.databinding.FragmentRegistrationSellerPart1Binding
import com.aims.ev4me.ui.register_activity.seller.part2.ChargerStatus
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore

class Registration_sellerPart1Fragment : Fragment() {

    private var _binding: FragmentRegistrationSellerPart1Binding? = null

    private val binding get() = _binding!!


    private lateinit var inputStreetAddress: EditText
    private lateinit var input_apt_building: EditText
    private lateinit var inputCity: EditText
    private lateinit var inputState: EditText
    private lateinit var inputCountry: EditText
    private lateinit var inputZipCode: EditText
    private lateinit var numChargersInput: EditText


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        _binding = FragmentRegistrationSellerPart1Binding.inflate(inflater, container, false)
        val root: View = binding.root

        val nextPageButton = binding.nextPageButton
        inputStreetAddress = binding.inputStreetAddress
        input_apt_building = binding.inputAptBuilding
        inputCity = binding.inputCity
        inputState = binding.inputState
        inputCountry = binding.inputCountry
        inputZipCode = binding.inputZipCode
        //numChargersInput = binding.inputNumChargers



        nextPageButton.setOnClickListener {
            if (validateInput()) {
                val aptBuilding: String = binding.inputAptBuilding.text.toString()
                val streetAddress: String = inputStreetAddress.text.toString()
                val city: String = inputCity.text.toString()
                val state: String = inputState.text.toString()
                val zipCode: String = inputZipCode.text.toString()
                val country: String = inputCountry.text.toString()

                val fullAddress =
                    if (aptBuilding.isNotBlank()) "$streetAddress $aptBuilding, $city, $state $zipCode, $country"
                    else "$streetAddress, $city, $state $zipCode $country"

                Log.v("Registration_sellerPart1Fragment.kt", fullAddress)

                val latLngResult = runBlocking {
                    convertAddressToLatLng(fullAddress)
                }

                val firestoreDB = Firebase.firestore
                val auth = Firebase.auth
                val UID = auth.currentUser?.uid!!
                val doc = firestoreDB.collection("users").document(UID)
                doc.update("address", fullAddress)
                doc.update("latLng", latLngResult)



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

        if (inputStreetAddress.text.toString().isBlank()) {
            validationPassed = false
            inputStreetAddress.error = "Address field cannot be empty"
        }

        if (inputCity.text.toString().isBlank()) {
            validationPassed = false
            inputCity.error = "City cannot be empty"
        }

        if (inputState.text.toString().isBlank()) {
            validationPassed = false
            inputState.error = "State cannot be empty"
        }

        if (inputCountry.text.toString().isBlank()) {
            validationPassed = false
            inputCountry.error = "Country cannot be empty"
        }

        if (inputZipCode.text.toString().isBlank()) {
            validationPassed = false
            inputZipCode.error = "Zip Code field cannot be empty"
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