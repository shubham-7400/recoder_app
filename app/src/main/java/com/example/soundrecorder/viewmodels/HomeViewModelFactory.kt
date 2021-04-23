package com.example.soundrecorder.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.soundrecorder.activity.HomeActivity

class HomeViewModelFactory(val homeActivity: HomeActivity) : ViewModelProvider.Factory   {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(homeActivity) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}