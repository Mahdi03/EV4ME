package com.aims.ev4me.ui.register_activity.allUsers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aims.ev4me.MainActivity
import com.aims.ev4me.databinding.FragmentRegistrationAllusersPart2Binding
import com.aims.ev4me.ui.register_activity.allUsers.part2.VehicleInfo
import com.aims.ev4me.ui.register_activity.allUsers.part2.VehicleInfoRecyclerViewAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Registration_allUsersPart2Fragment : Fragment() {
    private var _binding: FragmentRegistrationAllusersPart2Binding? = null

    private val binding get() = _binding!!

    //Get the navigation arguments object passed through navigation
    val navigationArgs: Registration_allUsersPart2FragmentArgs by navArgs()


    private lateinit var recyclerView: RecyclerView
    private var arrayList = ArrayList<VehicleInfo>()
    private lateinit var finishRegistrationButton: Button
    private lateinit var recyclerViewAdapter: VehicleInfoRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationAllusersPart2Binding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerView = binding.recyclerViewForVehicleInfoForms

        recyclerView.setHasFixedSize(true)
        //We need to specify a layout manager so our recycler view knows how to orient all the views it will populate
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val numCars: Int = navigationArgs.numCars
        //TODO: Use this value now

        //Populate an empty arrayList of a certain number of elements that will then be affected by the recyclerview's adapter
        for (i in 1..numCars) {
            arrayList.add(VehicleInfo(carMake="", carModel="", carColor="", licensePlateNumber=""))
        }

        recyclerViewAdapter = context?.let { VehicleInfoRecyclerViewAdapter(it, arrayList) }!!
        recyclerView.adapter = recyclerViewAdapter

        finishRegistrationButton = binding.finishRegistrationButton
        finishRegistrationButton.setOnClickListener {
            val myFinalArrayList: ArrayList<VehicleInfo> = recyclerViewAdapter.arrayList
            var validationPassed = true
            for (i in 0 until myFinalArrayList.size) {
                val viewHolder: VehicleInfoRecyclerViewAdapter.ViewHolder? = recyclerView.findViewHolderForAdapterPosition(i) as VehicleInfoRecyclerViewAdapter.ViewHolder?
                //So this ugly-ass loop will only overwrite our validation if it is false, if it is true
                // we don't want to touch it since something else could have already set our flag to false
                // and we don't want to undo that
                viewHolder?.let {
                    validationPassed = if (it.validateInputs()) validationPassed else false
                }
            }
            if (!validationPassed) { return@setOnClickListener; }

            //TODO: Save all this data to the database under their UUID

            //TODO: Yo someone please fix this data structure I just kinda got it in here for now
            val myData = ArrayList<HashMap<String, String>>()
            for (vehicleInfo in myFinalArrayList) {
                Log.v("Registration_allUsersPart2Fragment.kt", vehicleInfo.toString())
                myData.add(vehicleInfo.toHashMap())
            }
            val firestoreDB = Firebase.firestore
            val auth = Firebase.auth
            val UID = auth.currentUser?.uid!!
            //For this current user, create a new field called "vehicles" and store all their vehicle data there
            firestoreDB.collection("users").document(UID).update("vehicles", myData)
            //NOW we can finally move on to the main home screen
            goToMainActivity()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun goToMainActivity() {
        Log.d("LoginActivity.kt", "Sign in successful, going to MainActivity")
        val mainActivityIntent = Intent(context, MainActivity::class.java)
        //We need to set these flags so that they can't come back to the login activity through the back stack without logging out
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) //Kotlin doesn't support |, use `or` instead
        startActivity(mainActivityIntent)
    }
}