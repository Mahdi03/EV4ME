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
import com.aims.ev4me.ui.register_activity.seller.part2.ChargerStatus
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Registration_sellerPart2Fragment : Fragment() {
    private var _binding: FragmentRegistrationSellerPart2Binding? = null

    private val binding get() = _binding!!

    //Get the navigation arguments object passed through navigation
    val navigationArgs: Registration_sellerPart2FragmentArgs by navArgs()


    private lateinit var recyclerView: RecyclerView
    private var arrayList = ArrayList<ChargerInfo>()
    private var arrayListRealtime = ArrayList<ChargerStatus>()
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
            //arrayListRealtime.add(ChargerStatus(addressString = ))
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

            val firestoreDB = Firebase.firestore
            val auth = Firebase.auth
            val UID = auth.currentUser?.uid!!

            //TODO: Save all this data to the database under their UUID
            //Add charger to realtime database (acting like a server)
            val id: String = "1"
            var charger_base: ChargerStatus = ChargerStatus(id)

//            val databaseEmulator = Firebase.database
//            databaseEmulator.useEmulator("10.0.2.2", 9000)
            var databaseReal: DatabaseReference
            databaseReal = Firebase.database.reference
            var latlng1: Any?

            firestoreDB.collection("users").document(UID).get()
                .addOnSuccessListener {
                    latlng1 = it["latLng"]
                    var lat1 = ((latlng1 as HashMap<String, Any>)["latitude"]).toString().toDouble()
                    var lng1 = ((latlng1 as HashMap<String, Any>)["longitude"]).toString().toDouble()
                    val latLngRT: LatLng = LatLng(lat1, lng1)
                    charger_base.addressLatLng = latLngRT
                    charger_base.addressString = it["address"].toString()
                    Log.i("firebase 1", charger_base.addressLatLng.toString())
                    val myData = ArrayList<HashMap<String, String>>()
                    var i: Int = 0
                    for (chargerInfo in myFinalArrayList) {
                        Log.v("Registration_sellerPart2Fragment.kt", chargerInfo.toString())
                        // for firestore:
                        myData.add(chargerInfo.toHashMap())
                        // rest is for realtime:
                        var charger = ChargerStatus(listingID = UID+i.toString())
                        charger.chargerType=chargerInfo.chargerType
                        charger.chargerName=chargerInfo.chargerName
                        charger.addressLatLng=charger_base.addressLatLng
                        charger.addressString=charger_base.addressString
                        //databaseEmulator.getReference().child("Listings").child(UID+i.toString()).setValue(charger)
                        databaseReal.child("Listings").child(UID+i.toString()).setValue(charger)
                        Log.i("firebase", charger_base.addressLatLng.toString())
                        Log.i("firebase 2", charger.chargerName)
                        i++
                    }

                    firestoreDB.collection("users").document(UID).update("chargers", myData)
                }
                .addOnFailureListener{ Log.i("firebase", "failed")}



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