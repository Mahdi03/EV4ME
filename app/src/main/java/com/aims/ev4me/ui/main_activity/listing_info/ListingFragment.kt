package com.aims.ev4me.ui.main_activity.listing_info

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aims.ev4me.R
import com.google.firebase.database.FirebaseDatabase

class ListingFragment : Fragment() {

    private var address: String = "";


    companion object {
        fun newInstance() = ListingFragment()
    }

    private lateinit var viewModel: ListingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FirebaseDatabase.getInstance().getReference().child("")
        return inflater.inflate(R   .layout.fragment_listing, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ListingViewModel::class.java)
        // TODO: Use the ViewModel
    }

}