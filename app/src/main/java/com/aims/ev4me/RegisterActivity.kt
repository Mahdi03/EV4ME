package com.aims.ev4me

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.aims.ev4me.databinding.RegisterActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: RegisterActivityBinding
    private var navController: NavController? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RegisterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_register_activity)
        //val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController = navHostFragment?.findNavController()



        auth = Firebase.auth

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            //Link them to the MainActivity, they are already logged in
            goToMainActivity()
        }
    }

    private fun goToMainActivity() {
        Log.d("RegisterActivity.kt", "How did we get here?? Redirect them to the MainActivity")
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        //We need to set these flags so that they can't come back to the login activity through the back stack without logging out
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) //Kotlin doesn't support |, use `or` instead
        startActivity(mainActivityIntent)
    }

}