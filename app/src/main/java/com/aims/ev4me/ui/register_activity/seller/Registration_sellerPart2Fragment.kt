package com.aims.ev4me.ui.register_activity.seller

import android.os.Bundle
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
import com.aims.ev4me.ui.register_activity.seller.part2.ChargerListing
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Registration_sellerPart2Fragment : Fragment() {
    private var _binding: FragmentRegistrationSellerPart2Binding? = null

    private val binding get() = _binding!!

    //Get the navigation arguments object passed through navigation
    val navigationArgs: Registration_sellerPart2FragmentArgs by navArgs()


    private lateinit var recyclerView: RecyclerView
    private var arrListOfEmptyChargerInfoForRecyclerViewToPopulate = ArrayList<ChargerInfo>()
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
        //Use this value now

        //Populate an empty arrayList of a certain number of elements that will then be affected by the recyclerview's adapter
        for (i in 1..numChargers) {
            arrListOfEmptyChargerInfoForRecyclerViewToPopulate.add(ChargerInfo(chargerName="", chargerType=ChargerInfo.ChargerType.NO_LEVEL))
        }

        recyclerViewAdapter = context?.let { ChargerInfoRecyclerViewAdapter(it, arrListOfEmptyChargerInfoForRecyclerViewToPopulate) }!!
        recyclerView.adapter = recyclerViewAdapter

        finishRegistrationButton = binding.finishRegistrationButton
        finishRegistrationButton.setOnClickListener {
            val arrListOfAllChargerDataFromRecyclerView: ArrayList<ChargerInfo> = recyclerViewAdapter.arrayList
            var validationPassed = true
            for (i: Int in 0 until arrListOfAllChargerDataFromRecyclerView.size) {
                val viewHolder: ChargerInfoRecyclerViewAdapter.ViewHolder? = recyclerView.findViewHolderForAdapterPosition(i) as ChargerInfoRecyclerViewAdapter.ViewHolder?
                //So this ugly-ass loop will only overwrite our validation if it is false, if it is true
                // we don't want to touch it since something else could have already set our flag to false
                // and we don't want to undo that
                viewHolder?.let {
                    validationPassed = if (it.validateInputs(i)) validationPassed else false
                }
            }
            if (!validationPassed) { return@setOnClickListener; }

            //Now that we've made it this far, let's store the data in both databases
            val firestoreDB = Firebase.firestore
            val auth = Firebase.auth
            val UID = auth.currentUser?.uid!!


            //Start with the Real time DB, since pushing it will return a unique ID we can then save in firestore

            // all the chargers they add here will have the same address belonging to this current user so first get the address string and the latlong
            var latLong: LatLng?
            var addressString: String?
            firestoreDB.collection("users").document(UID).get()
                .addOnSuccessListener {snapshot ->
                    //Convert the LatLong of this user to a LatLng obj and store it for use
                    val latLongHashMap: HashMap<String, Any> = snapshot["latLng"] as HashMap<String, Any>
                    val latitude: Double = latLongHashMap["latitude"].toString().toDouble()
                    val longitude: Double = latLongHashMap["longitude"].toString().toDouble()
                    latLong = LatLng(latitude, longitude)

                    //Store the string address of this user and store it for later use
                    addressString = snapshot["address"].toString()


                    //Now that we have the lat long and the string address from the Firestore DB pertaining
                    // to this current seller, we can now attach it to every single charger they own
                    //Now we want to upload each charger as its own listing, so loop through each charger,
                    // create the listing with the information, and then after storing it in realtimeDB,
                    // we will have our unique ID which we can then attach to the charger info and then
                    // push to firebase firestore!!!!

                    //Uncomment These 2 Lines for Realtime Emulator
                    //val databaseEmulator = Firebase.database
                    //databaseEmulator.useEmulator("10.0.2.2", 9000)
                    // Uncomment These 2 Lines for Real Realtime Database
                    val realtimeDB: DatabaseReference = Firebase.database.reference
                    /*
                    val faketimeDB = Firebase.database
                    faketimeDB.useEmulator("10.0.2.2", 9000)
                     */
                    val arrListOfParceledChargerData = ArrayList<HashMap<String, String>>()

                    arrListOfAllChargerDataFromRecyclerView.forEachIndexed { index, chargerInfo ->

                        /*****************************Realtime Database**************************************/
                        //Create ChargerListing
                        val chargerListing = addressString?.let { myAddressString ->
                            latLong?.let { myLatLong ->
                                ChargerListing(addressString = myAddressString,
                                    addressLatLng = myLatLong, chargerType = chargerInfo.chargerType,
                                    chargerName = chargerInfo.chargerName)
                            }
                        }
                        //Push this charger listing to Realtime Database (should later trigger all other clients to update)
                        val newChargerListingLocationInDB = realtimeDB.child("Listings").push()
                        //val newChargerListingLocationInDB = faketimeDB.reference.child("Listings").push()
                        if (chargerListing != null) {
                            newChargerListingLocationInDB.setValue(chargerListing.toHashMap())
                        }

                        //Now that we have pushed it into the database, we can store the ID in the charger info object
                        /*****************************Firestore**************************************/
                        //Add each charger data to the firestore database under the user
                        arrListOfAllChargerDataFromRecyclerView[index].chargerUID =
                            newChargerListingLocationInDB.key.toString()
                        arrListOfParceledChargerData.add(chargerInfo.toHashMap())
                    }
                    //Save all this data to the database under their UUID
                    firestoreDB.collection("users").document(UID).update("chargers", arrListOfParceledChargerData)
                }

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