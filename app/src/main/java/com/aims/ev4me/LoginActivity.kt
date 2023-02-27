package com.aims.ev4me

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.aims.ev4me.databinding.LoginActivityBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
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
        registerButton = findViewById(R.id.register_button)

        auth = Firebase.auth
        firebaseAnalytics = Firebase.analytics

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
            //Get email and password from input fields and then attempt the firebase login
            val email: String = emailInputField.text.toString()
            val password: String = passwordInputField.text.toString()
            //TODO: sanitize strings properly
            if (email.isEmpty()) {
                emailInputField.error = "Email cannot be empty"
            }
            if (password.isEmpty()) {
                passwordInputField.error = "Password cannot be empty"
            }
            if (email.isNotEmpty() && password.isNotEmpty()) {
                signIn(email, password)
            }
        }
        registerButton.setOnClickListener {
            goToRegisterActivity()
        }

    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d("LoginActivity.kt", "EYYYY IT WORKEDDDDDDDD")
                //Link them to the MainActivity, they are now signed in
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.METHOD, "login")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                goToMainActivity()
            }
            else {
                Log.e("LoginActivity.kt", "Sign in w/ email failed", task.exception)
                //TODO: Show user error messages
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