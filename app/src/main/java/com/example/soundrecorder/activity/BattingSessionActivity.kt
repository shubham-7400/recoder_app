package com.example.soundrecorder.activity

import android.content.Context
import android.content.SharedPreferences
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
import com.example.soundrecorder.adapters.BattingSessionAdapter
import com.example.soundrecorder.adapters.BowlingSessionAdapter
import com.example.soundrecorder.config.AppPreferences
import com.example.soundrecorder.databinding.ActivityBattingSessionBinding
import com.example.soundrecorder.kotlinclasses.NothingSelectedSpinnerAdapter
import com.example.soundrecorder.models.ViewListingMoldel
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.lang.reflect.Array
import java.util.*

class BattingSessionActivity : AppCompatActivity() {

    lateinit var binding: ActivityBattingSessionBinding

    var pSharedPref: SharedPreferences? = null
    lateinit var appPreferences: AppPreferences
    lateinit var bowlingDataList:ArrayList<ViewListingMoldel>
    var isCall: Boolean = false
    var isOutputTwo: Boolean = false
    var isBowling: Boolean = false
    lateinit var battingSessionAdapter : BattingSessionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_batting_session)

        binding = ActivityBattingSessionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        pSharedPref = getSharedPreferences("MyOutputs", Context.MODE_PRIVATE)

        val callFor = intent.getStringExtra("call_for")
        if(callFor.equals("batting")){
            binding.companyNameText.text = getString(R.string.batting_session_summary)
            binding.speedHeadingTv.text = getString(R.string.bat_swing_speed_km)
            isOutputTwo = true
        }else if(callFor.equals("aerial")){
            binding.companyNameText.text = getString(R.string.areial_travel_distance)
            binding.speedHeadingTv.text = getString(R.string.aerial_travel_distnace_meters)
            isOutputTwo = false
        }else if(callFor.equals("bowling")){
            isBowling = true
            binding.companyNameText.text = getString(R.string.ball_exit_session_summry)
            binding.speedHeadingTv.text = getString(R.string.bowling_speed_km_hr)
        }

        initialize()
    }

    fun initialize(){
        val set: Set<String> = pSharedPref?.getStringSet("titleSet", null) as Set<String>
        var typeArraList = ArrayList<String>()
        for (s in set) {
            typeArraList.add(s)
        }

        val adapter2 = ArrayAdapter.createFromResource(
            this,
            R.array.item_six_array,
            android.R.layout.simple_spinner_item
        )
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.typeSpinner.adapter = adapter2


        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.age_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ageSpinner.adapter = adapter



        appPreferences = AppPreferences()
        appPreferences.init(this)

        bowlingDataList = ArrayList()

        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        binding.backBtn.setOnClickListener {
            finish()
        }


        binding.typeSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                if(isCall){
                    if(!isBowling){
                        viewListingResult()
                    }else{
                        viewListingThreeResult()
                    }

                }
                isCall = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })


        binding.ageSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                if(!isBowling){
                    viewListingResult()
                }else{
                    viewListingThreeResult()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

    }


    private fun viewListingResult() {
        bowlingDataList.clear()
        binding.cpCardview.visibility = View.VISIBLE
        var map = HashMap<String,Any>()
        map.put("selectedMaterial", binding.typeSpinner.selectedItem.toString())
        map.put("isOutputTwo", isOutputTwo)
        map.put("selectedAgeGroup", binding.ageSpinner.selectedItem.toString())
        map.put("userId", appPreferences.uuid)
        System.out.println("input_map_is "+map.toString())
        val call = ApiClient().getClient(this)!!.create(ApiInterface::class.java)
        call.viewListingOne(map, appPreferences.token).enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                try {
                    binding.cpCardview.visibility = View.GONE
                    val res = Gson().toJson(response.body())
                    val mainObject = JSONObject(res)
                    Log.d("VIEW_LISTING response", mainObject.toString())
                    if (mainObject.getBoolean("success")) {

                        for (i in 0..mainObject.getJSONArray("data").length() - 1){
                            val jsonObj = mainObject.getJSONArray("data").getJSONObject(i)
                            bowlingDataList.add(ViewListingMoldel(jsonObj.getString("name"), jsonObj.getString("output"), ""))
                        }
                        battingSessionAdapter = BattingSessionAdapter(bowlingDataList, this@BattingSessionActivity)
                        binding.recyclerview.adapter = battingSessionAdapter

                    } else {
                        Toast.makeText(
                            this@BattingSessionActivity, mainObject.getString("message"),
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

    private fun viewListingThreeResult() {
        bowlingDataList.clear()
        binding.cpCardview.visibility = View.VISIBLE
        var map = HashMap<String,Any>()
        map.put("selectedMaterial", binding.typeSpinner.selectedItem.toString())
        map.put("selectedAgeGroup", binding.ageSpinner.selectedItem.toString())
        map.put("userId", appPreferences.uuid)
        System.out.println("input_map_is 3 "+map.toString())
        val call = ApiClient().getClient(this)!!.create(ApiInterface::class.java)
        call.viewListingThree(map, appPreferences.token).enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                try {
                    binding.cpCardview.visibility = View.GONE
                    val res = Gson().toJson(response.body())
                    val mainObject = JSONObject(res)
                    Log.d("VIEW_LISTING 3 response", mainObject.toString())
                    if (mainObject.getBoolean("success")) {

                        for (i in 0..mainObject.getJSONArray("data").length() - 1){
                            val jsonObj = mainObject.getJSONArray("data").getJSONObject(i)
                            bowlingDataList.add(ViewListingMoldel(jsonObj.getString("name"), jsonObj.getString("output"), ""))
                        }
                        battingSessionAdapter = BattingSessionAdapter(bowlingDataList, this@BattingSessionActivity)
                        binding.recyclerview.adapter = battingSessionAdapter

                    } else {
                        Toast.makeText(
                            this@BattingSessionActivity, mainObject.getString("message"),
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