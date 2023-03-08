package com.aims.ev4me.ui.register_activity.seller

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aims.ev4me.R
import com.aims.ev4me.databinding.FragmentRegistrationSellerPart2Binding
import com.aims.ev4me.ui.register_activity.seller.part2.ChargerInfo
import com.aims.ev4me.ui.register_activity.seller.part2.ChargerInfoRecyclerViewAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Registration_sellerPart2Fragment : Fragment() {
    private var _binding: FragmentRegistrationSellerPart2Binding? = null

    private val binding get() = _binding!!

    //Get the navigation arguments object passed through navigation
    val navigationArgs: Registration_sellerPart2FragmentArgs by navArgs()


    private lateinit var recyclerView: RecyclerView
    private var arrayList = ArrayList<ChargerInfo>()
    private lateinit var finishRegistrationButton: Button
    private lateinit var recyclerViewAdapter: ChargerInfoRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationSellerPart2Binding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerView = binding.recyclerViewForChargerInfoForms

        recyclerView.setHasFixedSize(true)
        //We need to specify a layout manager so our recycler view knows how to orient all the views it will populate
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val numChargers: Int = navigationArgs.numChargers
        //TODO: Use this value now

        //Populate an empty arrayList of a certain number of elements that will then be affected by the recyclerview's adapter
        for (i in 1..numChargers) {
            arrayList.add(ChargerInfo(chargerName="", chargerType=ChargerInfo.ChargerType.NO_LEVEL))
        }

        recyclerViewAdapter = context?.let { ChargerInfoRecyclerViewAdapter(it, arrayList) }!!
        recyclerView.adapter = recyclerViewAdapter

        finishRegistrationButton = binding.finishRegistrationButton
        finishRegistrationButton.setOnClickListener {
            val myFinalArrayList: ArrayList<ChargerInfo> = recyclerViewAdapter.arrayList
            var validationPassed = true
            for (i: Int in 0 until myFinalArrayList.size) {
                val viewHolder: ChargerInfoRecyclerViewAdapter.ViewHolder? = recyclerView.findViewHolderForAdapterPosition(i) as ChargerInfoRecyclerViewAdapter.ViewHolder?
                //So this ugly-ass loop will only overwrite our validation if it is false, if it is true
                // we don't want to touch it since something else could have already set our flag to false
                // and we don't want to undo that
                viewHolder?.let {
                    validationPassed = if (it.validateInputs(i)) validationPassed else false
                }
            }
            if (!validationPassed) { return@setOnClickListener; }

            //TODO: Save all this data to the database under their UUID

            //TODO: Yo someone please fix this data structure I just kinda got it in here for now
            val myData = ArrayList<HashMap<String, String>>()
            for (vehicleInfo in myFinalArrayList) {
                Log.v("Registration_sellerPart2Fragment.kt", vehicleInfo.toString())
                myData.add(vehicleInfo.toHashMap())
            }
            val firestoreDB = Firebase.firestore
            val auth = Firebase.auth
            val UID = auth.currentUser?.uid!!
            //For this current user, create a new field called "vehicles" and store all their vehicle data there
            firestoreDB.collection("users").document(UID).update("chargers", myData)
            //NOW we can finally move on to the rest of the regular registration
            findNavController().navigate(R.id.action_register_navigation_seller_part2_to_register_navigation_allUsers_part1)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}