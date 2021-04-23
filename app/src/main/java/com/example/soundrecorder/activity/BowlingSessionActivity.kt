package com.example.soundrecorder.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.clabs.caddyapp.network.ApiClient
import com.clabs.caddyapp.network.ApiInterface
import com.example.soundrecorder.R
import com.example.soundrecorder.adapters.BowlingSessionAdapter
import com.example.soundrecorder.config.AppPreferences
import com.example.soundrecorder.databinding.ActivityBowlingSessionBinding
import com.example.soundrecorder.models.Output1ViewListingDataClass
import com.example.soundrecorder.models.ViewListingMoldel
import com.example.soundrecorder.viewmodels.Output1ViewListingViewModel
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.util.*


class BowlingSessionActivity : AppCompatActivity(){

    lateinit var binding: ActivityBowlingSessionBinding
    lateinit var appPreferences: AppPreferences
    lateinit var bowlingDataList:ArrayList<ViewListingMoldel>
    lateinit var bowlingSessionAdapter: BowlingSessionAdapter
    var isCall: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_bowling_session)
        binding = ActivityBowlingSessionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        appPreferences = AppPreferences()
        appPreferences.init(this)

        bowlingDataList = ArrayList()

        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        binding.backBtn.setOnClickListener {
            finish()
        }
        setAdapterOnEachSpinner()
    }


    private fun setAdapterOnEachSpinner() {

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.age_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ageSpinner.adapter = adapter


        val adapter2 = ArrayAdapter.createFromResource(
            this,
            R.array.type_array,
            android.R.layout.simple_spinner_item
        )
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.typeSpinner.adapter = adapter2




        binding.ageSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                if(isCall){
                    viewListingResult()
                }
               isCall = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        binding.typeSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                viewListingResult()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })


    }

private fun viewListingResult() {
        bowlingDataList.clear()
        binding.cpCardview.visibility = View.VISIBLE
        var map = HashMap<String,Any>()
        map.put("selectedFactor", binding.typeSpinner.selectedItem.toString())
        map.put("selectedRanking", "")
        map.put("selectedAgeGroup", binding.ageSpinner.selectedItem.toString())
        map.put("userId", appPreferences.uuid)
        System.out.println("input_map_is "+map.toString())
        val call = ApiClient().getClient(this)!!.create(ApiInterface::class.java)
        call.viewListing(map, appPreferences.token).enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                try {
                    binding.cpCardview.visibility = View.GONE
                    val res = Gson().toJson(response.body())
                    val mainObject = JSONObject(res)
                    Log.d("VIEW_LISTING response", mainObject.toString())
                    if (mainObject.getBoolean("success")) {

                        for (i in 0..mainObject.getJSONArray("data").length() - 1){
                            val jsonObj = mainObject.getJSONArray("data").getJSONObject(i)
                            bowlingDataList.add(ViewListingMoldel(jsonObj.getString("name"), jsonObj.getString("output1"), jsonObj.getString("factorPercentage")))
                        }
                        bowlingSessionAdapter = BowlingSessionAdapter(bowlingDataList, this@BowlingSessionActivity)
                        binding.recyclerview.adapter = bowlingSessionAdapter

                    } else {
                        Toast.makeText(
                            this@BowlingSessionActivity, mainObject.getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    Log.i("TAG", "onResponse: exception is her " + e.message.toString())
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<Any>, throwable: Throwable) {
                call.cancel()
                binding.cpCardview.visibility = View.GONE
                Log.e("VIEW_LISTING onFailure", throwable.toString())
            }
        })
    }
}