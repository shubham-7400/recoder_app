package com.example.soundrecorder.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.soundrecorder.activity.Output1ViewListingActivity

class Output1ViewListingViewModelFactory(val output1ViewListingActivity: Output1ViewListingActivity) : ViewModelProvider.Factory  {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(Output1ViewListingViewModel::class.java)) {
            return Output1ViewListingViewModel(output1ViewListingActivity) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}