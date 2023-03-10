package com.aims.ev4me

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.aims.ev4me.databinding.LoginActivityBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


/*
* See: https://firebase.google.com/docs/auth/android/password-auth?hl=en&authuser=0
* for user sign in and sign up
* */

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var binding: LoginActivityBinding

    private lateinit var emailInputField: EditText
    private lateinit var passwordInputField: EditText
    private lateinit var loginButton: Button
    private lateinit var loginErrorMessageTextView: TextView
    private lateinit var registerButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
         * https://developer.android.com/topic/libraries/view-binding
         * if a layout file is called result_profile.xml, the resulting
         * class becomes ResultProfileBinding, and so we import that
         * and then populate the content view with its root
         * */

        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emailInputField = findViewById(R.id.email_input_field)
        passwordInputField = findViewById(R.id.password_input_field)
        loginButton = findViewById(R.id.login_button)
        loginErrorMessageTextView = findViewById(R.id.login_error_message)
        registerButton = findViewById(R.id.register_button)

        auth = Firebase.auth
        firebaseAnalytics = Firebase.analytics

        // Just testing emulator stuff on the first page that appears to make sure data
        // was being added to the database on localhost
        // If you want to test if the emulator is working on your end:
        // Run firebase emulators:start in Command Line at this directory
        // Open http://127.0.0.1:4000/database/ev4me-d47bd-default-rtdb/data
        // Uncomment the 3 lines of code below and run the app
        // Opening the login page should add info to database
//        val database = Firebase.database
//        database.useEmulator("10.0.2.2", 9000)
//        database.getReference().child("Test2").setValue("Hello World")
        // Testing HashMap Stuff to make sure it modified properly, it does!
//        var hashMap: HashMap<String, Int> = HashMap<String, Int> ()
//        hashMap.put("Akshay", 20)
//        hashMap.put("Manasvi", 14)
//        hashMap.put("Anikait", 147)
//
//        database.getReference().child("Test").child("Test Child").updateChildren(hashMap as Map<String, Int>)
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            //Link them to the MainActivity, they are already logged in
            goToMainActivity()
        }
        //If we made it this far, that means they need to login, we can set
        //up the onclick listeners now
        loginButton.setOnClickListener {
            attemptLogin()
        }
        registerButton.setOnClickListener {
            goToRegisterActivity()
        }

    }

    private fun attemptLogin() {
        val email: String = emailInputField.text.toString()
        val password: String = passwordInputField.text.toString()

        if (!validateLoginInfo(email, password)) {
            return
        }
        //If we made it this far then we made it past the "client-side" validation
        signIn(email, password)
    }

    private fun validateLoginInfo(email: String, password: String): Boolean {
        //TODO: sanitize strings properly, this is just a temporary fix
        var validationPassed: Boolean = true
        if (!email.isValidEmail()) {
            emailInputField.error = "The email address is badly formatted"
            validationPassed = false
        }
        if (email.isBlank()) {
            emailInputField.error = "Email cannot be empty"
            validationPassed = false
        }
        if (password.isBlank()) {
            passwordInputField.error = "Password cannot be empty"
            validationPassed = false
        }
        return validationPassed
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d("LoginActivity.kt", "Login succeeded")
                //Link them to the MainActivity, they are now signed in
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.METHOD, "login")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                goToMainActivity()
            }
            else {
                //Show user error messages
                task.exception?.message?.let {
                    Log.e("LoginActivity.kt", "Sign in w/ email failed", task.exception)


                    var errorMessageToDisplay: String = it
                    val TAG: String = "Registration_BasicUserInfoFragment.kt"
                    try {
                        throw task.exception!!
                    }
                    catch (e: FirebaseAuthInvalidUserException) {
                        //The user does not exist
                        errorMessageToDisplay = "The user $email does not exist. Click \"Register Now\" to create your account today!"
                    }
                    catch (e: FirebaseAuthInvalidCredentialsException) {
                        //They got the password wrong
                        errorMessageToDisplay = "Either the email or password was not entered correctly"
                    }
                    catch (e: Exception){
                        Log.e(TAG, "Safety catch-all for any other errors", e)
                    }
                    finally {
                        //this error message doesn't display, add a red TextView in the UI
                        loginErrorMessageTextView.text = errorMessageToDisplay
                        loginErrorMessageTextView.visibility = View.VISIBLE
                        loginButton.error = errorMessageToDisplay
                    }

                }
            }
        }
    }

    private fun goToMainActivity() {
        Log.d("LoginActivity.kt", "Sign in successful, going to MainActivity")
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        //We need to set these flags so that they can't come back to the login activity through the back stack without logging out
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) //Kotlin doesn't support |, use `or` instead
        startActivity(mainActivityIntent)
    }

    private fun goToRegisterActivity() {
        val registerActivityIntent = Intent(this, RegisterActivity::class.java)
        registerActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(registerActivityIntent)
    }
}