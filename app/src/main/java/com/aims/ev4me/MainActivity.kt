package com.aims.ev4me

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.aims.ev4me.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
        //val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val navController = navHostFragment?.findNavController()
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.


        var appBarConfiguration: AppBarConfiguration? = null/* = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard_user, R.id.navigation_profile
            )
        )*/

        val firestoreDB = Firebase.firestore
        firestoreDB.collection("users")
            .document(Firebase.auth.currentUser?.uid!!)
            .get()
            .addOnSuccessListener { document ->
                val accountType: String = document?.data?.get("accountType").toString()
                when (accountType) {
                    "User" -> {
                        navView.menu.clear()
                        navView.inflateMenu(R.menu.bottom_nav_menu_user)
                        appBarConfiguration = AppBarConfiguration(
                            setOf(
                                R.id.navigation_home, R.id.navigation_dashboard_user, R.id.navigation_profile
                            )
                        )
                    }
                    "Lender" -> {
                        navView.menu.clear()
                        navView.inflateMenu(R.menu.bottom_nav_menu_lender)
                        appBarConfiguration = AppBarConfiguration(
                            setOf(
                                R.id.navigation_home, R.id.navigation_dashboard_lender, R.id.navigation_profile
                            )
                        )
                    }
                    else -> {
                        //Let's sign them out and send them back to the LoginActivity.kt
                        Firebase.auth.signOut()
                        goToLoginActivity()
                    }
                }

                if (navController != null) {
                    appBarConfiguration?.let { setupActionBarWithNavController(navController, it) }
                }
                if (navController != null) {
                    navView.setupWithNavController(navController)
                }
                Log.v("MainActivity.kt", "The current user is a $accountType")
            }

        /*
        if (navController != null) {
            setupActionBarWithNavController(navController, appBarConfiguration)
        }
        if (navController != null) {
            navView.setupWithNavController(navController)
        }*/
        supportActionBar?.hide()


    }

    private fun goToLoginActivity() {
        Log.d("ProfilePageFragment.kt", "Sign out successful, going to LoginActivity")
        val loginActivityIntent = Intent(applicationContext, LoginActivity::class.java)
        //We need to set these flags so that they can't come back to the login activity through the back stack without logging out
        loginActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) //Kotlin doesn't support |, use `or` instead
        startActivity(loginActivityIntent)
    }
}