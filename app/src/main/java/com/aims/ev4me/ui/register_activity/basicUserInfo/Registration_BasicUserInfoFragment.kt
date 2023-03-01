package com.aims.ev4me.ui.register_activity.basicUserInfo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aims.ev4me.R
import com.aims.ev4me.databinding.FragmentRegistrationBasicUserInfoBinding
import com.aims.ev4me.isValidEmail
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Registration_BasicUserInfoFragment : Fragment() {

    private var _binding: FragmentRegistrationBasicUserInfoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var accountTypeDropdownInput: Spinner
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var nextPageButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBasicUserInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = Firebase.auth
        firebaseAnalytics = Firebase.analytics

        firstNameInput = binding.inputFirstName
        lastNameInput = binding.inputLastName
        accountTypeDropdownInput = binding.inputAccountTypeDropdown
        emailInput = binding.inputEmail
        passwordInput = binding.inputPassword
        confirmPasswordInput = binding.inputConfirmPassword

        passwordInput.setOnKeyListener { _, _, _ ->
            crossCheckPasswordInputs() //ig we rename unused parameters to underscores now :/
            return@setOnKeyListener false //We need to return false for the key events to bubble through
        }
        confirmPasswordInput.setOnKeyListener { _, _, _ ->
            crossCheckPasswordInputs() //ig we rename unused parameters to underscores now :/
            return@setOnKeyListener false //We need to return false for the key events to bubble through
        }

        nextPageButton = binding.nextPageButton

        nextPageButton.setOnClickListener {
            attemptRegistration()
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun attemptRegistration() {
        //Attempt to make a new account with this information
        val email: String = emailInput.text.toString()
        val password: String = passwordInput.text.toString()


        //Validate values - client-side validation
        if (!validateRegistrationInfo(email, password)) {
            return
        }

        activity?.let { mActivity ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(mActivity) { task ->
                    if (task.isSuccessful) {
                        registerAccountAndContinueRegistrationProcess()
                    }
                    else {
                        //TODO: do error handling in this case - server-side validation
                        task.exception?.message?.let {
                            Log.e("Registration_BasicUserInfoFragment.kt", it)

                            /**
                             * TODO:
                             * We need to create a variable in our database called `registrationFinished`
                             * associated with each user, that way they start the registration but then
                             * accidentally back out, we need them to be able to continue that registration.
                             *
                             * This means that also at every step of the registration process we'd need
                             * to check the database to see if those values already exist and if so, we'd
                             * need to populate them back into the UI so that they can see that their changes
                             * were saved.
                             *
                             * If their account already exists and `registrationFinished`=true, either we
                             * can choose to redirect them to the login page saying the account already
                             * exists, or we can try to log them in if the password is right and take
                             * them straight to the homepage. (but then this raises a question of what
                             * if their password is wrong)
                             */
                            var errorMessageToDisplay: String = it
                            //TODO: Better error messaging required
                            val TAG: String = "Registration_BasicUserInfoFragment.kt"
                            try {
                                throw task.exception!!
                            }
                            catch (e: FirebaseAuthUserCollisionException) {
                                errorMessageToDisplay = e.message!!
                            }
                            catch (e: Exception) {
                                Log.e(TAG, "Safety catch-all for any other errors", e)
                            }
                            finally {
                                //TODO: this error message doesn't display, add a red TextView in the UI
                                nextPageButton.error = errorMessageToDisplay
                            }

                        }

                    }
                }
        }
    }

    private fun validateRegistrationInfo(email: String, password: String): Boolean {
        //TODO: Better validation and input handling required, this is just temporary
        var validationPassed: Boolean = true
        //Sorryyyyy for the chain of if- statements, if you know a better way then plz implement
        if (firstNameInput.text.toString().isEmpty()) {
            firstNameInput.error = "First name cannot be empty"
            validationPassed = false
        }
        if (lastNameInput.text.toString().isEmpty()) {
            lastNameInput.error = "Last name cannot be empty"
            validationPassed = false
        }
        if (!crossCheckPasswordInputs()) {
            //Returns false when the passwords don't match
            validationPassed = false
        }
        if (email.isEmpty()) {
            emailInput.error = "Email cannot be empty"
            validationPassed = false
        }
        if (password.isEmpty()) {
            passwordInput.error = "Email cannot be empty"
            validationPassed = false
        }
        if (!email.isValidEmail()) {
            emailInput.error = "Email address is not in the correct format"
            validationPassed = false
        }
        //TODO: Add more password validation like special characters etc etc
        if (password.length < 7) {
            //Firebase Auth SDK requires more than 6 characters but we can impose even more restrictions if we'd like
            passwordInput.error = "Password must be more than 6 characters"
            validationPassed = false
        }

        return validationPassed
    }

    private fun crossCheckPasswordInputs(): Boolean {
        val passwordsMatch: Boolean = passwordInput.text.toString() == confirmPasswordInput.text.toString()
        if (!passwordsMatch) {
            //TODO: have an error message in UI
            confirmPasswordInput.error = "Passwords do not match"
        }
        else {
            //TODO: hide the error message
        }
        Log.d("Registration_BasicUserInfoFragment.kt", "Passwords match: $passwordsMatch")
        return passwordsMatch
    }

    private fun registerAccountAndContinueRegistrationProcess() {

        //Firebase analytics to count how many users have registered with us
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.METHOD, "sign_up")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)

        //TODO: Store first name, last name, and account type in database with attached UUID
        val UID = auth.currentUser?.uid
        val firstName = firstNameInput.text.toString()
        val lastName = lastNameInput.text.toString()
        val accountType = accountTypeDropdownInput.selectedItem.toString()
        //Log.v("Registration_BasicUserInfoFragment.kt", accountType)

        //Navigate to next page in the registration process
        when (accountType) {
            "Buyer" -> {
                //Navigate to the `allUsers` section of our app's registration flow
                findNavController().navigate(R.id.action_register_navigation_basicUserInfo_to_register_navigation_allUsers_part1)
                //Navigation.createNavigateOnClickListener(R.id.action_register_navigation_basicUserInfo_to_register_navigation_allUsers_part1, null)
            }
            "Seller" -> {
                //Navigate to the special `seller` component of our app's registration flow
                findNavController().navigate(R.id.action_register_navigation_basicUserInfo_to_register_navigation_seller_part1)
                //Navigation.createNavigateOnClickListener(R.id.action_register_navigation_basicUserInfo_to_register_navigation_seller_part1, null)
            }
        }
    }

}