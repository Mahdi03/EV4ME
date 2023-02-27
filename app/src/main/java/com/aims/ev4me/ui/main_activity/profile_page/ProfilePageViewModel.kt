package com.aims.ev4me.ui.main_activity.profile_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfilePageViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the profile page Fragment"
    }
    val text: LiveData<String> = _text
}