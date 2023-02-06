package com.aims.ev4me.ui.charger_dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChargerDashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the charger dashboard Fragment"
    }
    val text: LiveData<String> = _text
}