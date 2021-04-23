package com.example.soundrecorder.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.clabs.caddyapp.network.ApiClient
import com.clabs.caddyapp.network.ApiInterface
import com.example.soundrecorder.adapters.Output1ViewListingAdapter
import com.example.soundrecorder.config.AppPreferences
import com.example.soundrecorder.databinding.ActivityOuptut1ViewListingBinding
import com.example.soundrecorder.models.Output1ViewListingDataClass
import com.example.soundrecorder.viewmodels.Output1ViewListingViewModel
import com.example.soundrecorder.viewmodels.Output1ViewListingViewModelFactory
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class Output1ViewListingActivity : AppCompatActivity() {
    lateinit var binding: ActivityOuptut1ViewListingBinding
    lateinit var appPreferences: AppPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOuptut1ViewListingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        appPreferences = AppPreferences()
        appPreferences.init(this)

        val viewModelFactory = Output1ViewListingViewModelFactory(this)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(Output1ViewListingViewModel::class.java)

        val output1ViewListingAdapter = Output1ViewListingAdapter(this)
        binding.recyclerview.adapter = output1ViewListingAdapter
        viewModel.arrayListOfOutput1ViewListingDataClass.observe(this, androidx.lifecycle.Observer {
            Log.i(TAG, "onCreate: size is "+it.size)
            output1ViewListingAdapter.submitList(null);
            output1ViewListingAdapter.submitList(it)
        })

        var selectedFactor = intent.getStringExtra("selectedFactor")
        var selectedRanking = intent.getStringExtra("selectedRanking")

        if (selectedFactor == "s" || selectedFactor == "g" || selectedFactor == "f"){
            binding.selectedFactorType.text = "Factor 2"
        }else{
            binding.selectedFactorType.text = "Factor 1"
        }

        binding.selectedFactor.text = selectedFactor

        viewModel.callApiTogetViewListingResultForOutput1(selectedFactor, selectedRanking)
    }


    companion object {
        private const val TAG = "Output1ViewListingActiv"
    }
}