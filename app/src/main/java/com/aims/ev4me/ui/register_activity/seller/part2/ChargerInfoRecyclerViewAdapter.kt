package com.aims.ev4me.ui.register_activity.seller.part2

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.aims.ev4me.R


class ChargerInfoRecyclerViewAdapter(context: Context, arrayList: ArrayList<ChargerInfo>) :
    RecyclerView.Adapter<ChargerInfoRecyclerViewAdapter.ViewHolder>() {

    //We want these to be protected so that internal classes can also access their values
    private lateinit var context: Context
    //Make this one public so that we can access it from our actual fragment
    var arrayList = ArrayList<ChargerInfo>()

    init {
        this.context = context
        this.arrayList = arrayList
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView
        private val chargerNameInput: EditText //Spinner
        private val chargerTypeDropdown: Spinner

        init {
            titleText = itemView.findViewById(R.id.titleText)
            chargerNameInput = itemView.findViewById(R.id.input_chargerName)
            chargerTypeDropdown = itemView.findViewById(R.id.input_chargerTypeDropdown)

            chargerNameInput.addTextChangedListener {
                arrayList[adapterPosition].chargerName = it.toString()
                Log.v("VehicleInfoRecyclerViewAdapter.kt", "Car make on change" + it.toString())
            }

            val chargerTypes = context.resources.getStringArray(R.array.charger_types)

            class ChargerTypeDropdownOnSelectListener : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    //arrayList[adapterPosition].chargerType
                    //Log.v("ChargerInfoRecyclerViewAdapter.kt", parent?.getItemAtPosition(position).toString())

                    arrayList[adapterPosition].chargerType =
                    when (parent?.getItemAtPosition(position).toString()) {
                        chargerTypes[0] -> {
                            ChargerInfo.ChargerType.LEVEL1
                        }
                        chargerTypes[1] -> {
                            ChargerInfo.ChargerType.LEVEL2
                        }
                        else -> {
                            ChargerInfo.ChargerType.NO_LEVEL
                        }
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    arrayList[adapterPosition].chargerType = ChargerInfo.ChargerType.NO_LEVEL
                }

            }
            chargerTypeDropdown.onItemSelectedListener = ChargerTypeDropdownOnSelectListener()
        }
        fun setTitleTextPos(myInt: Int) {
            //Have the title of the view change based on what index we are on
            titleText.text = "Charger #${myInt+1}:"
        }

        public fun validateInputs(index: Int): Boolean {
            var validationSucceeded: Boolean = true
            if (chargerNameInput.text.toString().isBlank()) {
                chargerNameInput.error = "This field cannot be left blank"
                validationSucceeded = false
            }
            if (arrayList[index].chargerType == ChargerInfo.ChargerType.NO_LEVEL) {
                (chargerTypeDropdown.selectedView as TextView).error = "Please make sure to select a charger type"
                validationSucceeded = false
            }

            //TODO: Figure out a way to validate the license plate
            return validationSucceeded
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChargerInfoRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.fragment_registration_sellers_part2_reusable_charger_info_form, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ChargerInfoRecyclerViewAdapter.ViewHolder,
        position: Int
    ) {
        //Use this to set the real position of the elements
        holder.setTitleTextPos(position)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}

