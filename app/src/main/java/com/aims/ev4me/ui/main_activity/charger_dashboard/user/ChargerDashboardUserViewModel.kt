package com.aims.ev4me.ui.main_activity.charger_dashboard.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChargerDashboardUserViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the user's charger dashboard Fragment"
    }
    val text: LiveData<String> = _text
}