package com.aims.ev4me.ui.profile_page

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aims.ev4me.LoginActivity
import com.aims.ev4me.databinding.FragmentProfileBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfilePageFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var signOutButton: Button

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profilePageViewModel =
            ViewModelProvider(this).get(ProfilePageViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        profilePageViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        signOutButton = binding.signOutButton
        signOutButton.setOnClickListener {
            Firebase.auth.signOut()
            goToLoginActivity()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun goToLoginActivity() {
        Log.d("ProfilePageFragment.kt", "Sign out successful, going to LoginActivity")
        val loginActivityIntent = Intent(context, LoginActivity::class.java)
        //We need to set these flags so that they can't come back to the login activity through the back stack without logging out
        loginActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) //Kotlin doesn't support |, use `or` instead
        startActivity(loginActivityIntent)
    }
}