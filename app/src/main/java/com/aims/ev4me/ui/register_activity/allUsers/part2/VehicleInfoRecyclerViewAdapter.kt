package com.aims.ev4me.ui.register_activity.allUsers.part2

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.aims.ev4me.R


class VehicleInfoRecyclerViewAdapter(context: Context, arrayList: ArrayList<VehicleInfo>) :
    RecyclerView.Adapter<VehicleInfoRecyclerViewAdapter.ViewHolder>() {

    //We want these to be protected so that internal classes can also access their values
    private lateinit var context: Context
    //Make this one public so that we can access it from our actual fragment
    var arrayList = ArrayList<VehicleInfo>()

    init {
        this.context = context
        this.arrayList = arrayList
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView
        private val carMakeInput: EditText //Spinner
        private val carModelInput: EditText //Spinner
        private val carColorInput: EditText //Spinner
        private val licensePlateNumberInput: EditText

        init {
            titleText = itemView.findViewById(R.id.titleText)
            carMakeInput = itemView.findViewById(R.id.input_carMake)
            carModelInput = itemView.findViewById(R.id.input_carModel)
            carColorInput = itemView.findViewById(R.id.input_carColor)
            licensePlateNumberInput = itemView.findViewById(R.id.input_licensePlateNumber)

            /*
            class CarMakeOnSelectListener: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    arrayList[adapterPosition].make = ""
                    TODO("Not yet implemented")

                }

            }
            makeSelectorInput.onItemSelectedListener = CarMakeOnSelectListener()
             */


            carMakeInput.addTextChangedListener {
                arrayList[adapterPosition].carMake = it.toString()
                Log.v("VehicleInfoRecyclerViewAdapter.kt", "Car make on change" + it.toString())
            }
            carModelInput.addTextChangedListener {
                arrayList[adapterPosition].carModel = it.toString()
                Log.v("VehicleInfoRecyclerViewAdapter.kt", "Car model on change" + it.toString())
            }
            carColorInput.addTextChangedListener {
                arrayList[adapterPosition].carColor = it.toString()
                Log.v("VehicleInfoRecyclerViewAdapter.kt", "Car color on change" + it.toString())
            }
            licensePlateNumberInput.addTextChangedListener {
                arrayList[adapterPosition].licensePlateNumber = it.toString()
                Log.v("VehicleInfoRecyclerViewAdapter.kt", "License Plate Number on change" + it.toString())
            }
        }
        fun setTitleTextPos(myInt: Int) {
            //Have the title of the view change based on what index we are on
            titleText.text = "Vehicle #${myInt+1}:"
        }

        public fun validateInputs(): Boolean {
            var validationSucceeded: Boolean = true
            for (input: EditText in arrayOf(carMakeInput, carModelInput, carColorInput, licensePlateNumberInput)) {
                if (input.text.toString().isBlank()) {
                    input.error = "This field cannot be left blank"
                    validationSucceeded = false
                }
            }
            //TODO: Figure out a way to validate the license plate
            return validationSucceeded
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VehicleInfoRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.fragment_registration_allusers_part2_reusable_vehicle_info_form, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: VehicleInfoRecyclerViewAdapter.ViewHolder,
        position: Int
    ) {
        //Use this to set the real position of the elements
        holder.setTitleTextPos(position)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}

