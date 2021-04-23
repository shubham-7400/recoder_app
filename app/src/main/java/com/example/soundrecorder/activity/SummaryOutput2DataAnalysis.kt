package com.example.soundrecorder.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.clabs.caddyapp.network.ApiClient
import com.clabs.caddyapp.network.ApiInterface
import com.example.soundrecorder.R
import com.example.soundrecorder.config.AppPreferences
import com.example.soundrecorder.databinding.ActivitySummaryOutput2DataAnalysisBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.lang.reflect.Type

class SummaryOutput2DataAnalysis : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivitySummaryOutput2DataAnalysisBinding
    var list: ArrayList<String>? = null
    var pSharedPref: SharedPreferences? = null
    lateinit var appPreferences: AppPreferences
    private lateinit var heading1SelectRankingAdapter: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryOutput2DataAnalysisBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setUiAction()
    }

    private fun setUiAction() {
        appPreferences = AppPreferences()
        appPreferences.init(this)

        binding.backBtn.setOnClickListener(this)
        binding.heading1ViewListingForRanking2Btn.setOnClickListener(this)
        binding.heading1ViewListingForRanking3Btn.setOnClickListener(this)

        if (pSharedPref == null){
            pSharedPref = getSharedPreferences("MyOutputs", Context.MODE_PRIVATE)
        }
        var hashSetToHoldOptionBox6Title  =  pSharedPref?.getString("hashSetToHoldOptionBox6Title", "hashSetToHoldOptionBox6TitleNotExist")
        var gson = Gson()
        var type: Type? = object : TypeToken<HashSet<String>>() {}.getType()
        var SumarryOutput1DataClassHashSet: HashSet<String> = gson.fromJson(hashSetToHoldOptionBox6Title, type)
        list = ArrayList<String>(SumarryOutput1DataClassHashSet)

        var heading1SelectionAdapter = ArrayAdapter(this, R.layout.spinner_item, list!!)
        heading1SelectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.materialSelectionSpinner.adapter = heading1SelectionAdapter

        /*if (intent.getStringExtra("selectedMaterialType") != "")
        {
            Log.i(TAG, "setUiAction: siess "+binding.materialSelectionSpinner.getCount())
            var i = 0
            while (i <  binding.materialSelectionSpinner.getCount() - 1) {
                if (binding.materialSelectionSpinner.getItemAtPosition(i).toString().equals(intent.getStringExtra("selectedMaterialType"))) {
                    binding.materialSelectionSpinner.setSelection(i)
                    break
                }
                i++
            }
        }else{
            var i = 0
            while (i <  binding.materialSelectionSpinner.getCount() - 1) {
                Log.i(TAG, "setUiAction: siesaaas "+binding.materialSelectionSpinner.getItemAtPosition(i))
                if (binding.materialSelectionSpinner.getItemAtPosition(i).toString().equals("Concrete 100")) {
                    binding.materialSelectionSpinner.setSelection(i)
                    break
                }
                i++
            }
        }*/


        heading1SelectRankingAdapter = ArrayAdapter(this, R.layout.spinner_item, arrayListOf("world", "county", "state", "provience", "country"))
        heading1SelectRankingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ranking2ForHeading1.adapter = heading1SelectRankingAdapter
        binding.ranking3ForHeading1.adapter = heading1SelectRankingAdapter

        callApiToDataAnalysis()
    }

    private fun callApiToDataAnalysis() {
        binding.cpCardview.visibility = View.VISIBLE
        var map = HashMap<String, Any>()
        map.put("userId", appPreferences.uuid)
        map.put("selectedMaterial", "Concrete 100"/*binding.materialSelectionSpinner.selectedItem.toString()*/)
        map.put("selectedRanking2", binding.ranking2ForHeading1.selectedItem.toString())
        map.put("selectedRanking3", binding.ranking3ForHeading1.selectedItem.toString())
        val call = ApiClient().getClient(this)!!.create(ApiInterface::class.java)
        call.getDataAnalysisOne(map, appPreferences.token).enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                try {
                    if (response.body() == null) {
                        binding.cpCardview.visibility = View.GONE
                        var errorBody = response.errorBody()
                        val errorText = errorBody?.string()!!
                        val errorJsonObj = JSONObject(errorText)
                    } else {
                        val res = Gson().toJson(response.body())
                        val mainObject = JSONObject(res)
                        Log.d("DATA_ANALYSIS_ONE", mainObject.toString())
                        if (mainObject.getBoolean("success")) {
                            binding.cpCardview.visibility = View.GONE
                            var obj = mainObject.getJSONObject("data")
                            getInitializeHeading1(obj.opt("baseLine"), obj.opt("average"), obj.opt("peak"), obj.opt("highestOutput3"), obj.opt("ranking2"), obj.opt("ranking3"))
                            /*Log.i( TAG, "onResponse: got success"+mainObject.getJSONObject("data").opt("ranking"))
                            var heading3Obj = mainObject.getJSONObject("data").getJSONObject("heading3")
                            getInitializeHeading3(heading3Obj.opt("baseLine"), heading3Obj.opt("average"), heading3Obj.opt("peak"))
                            var heading4Obj = mainObject.getJSONObject("data").getJSONObject("heading4")
                            getInitializeHeading4(heading4Obj.opt("baseLine"), heading4Obj.opt("average"), heading4Obj.opt("peak"),mainObject.getJSONObject("data").opt("ranking"))*/
                        } else {
                            binding.cpCardview.visibility = View.GONE
                            Toast.makeText(
                                    this@SummaryOutput2DataAnalysis,
                                    "problem is " + mainObject.getString("message"),
                                    Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    binding.cpCardview.visibility = View.GONE
                    Log.i(TAG, "onResponse: exception is her " + e.message.toString())
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<Any>, throwable: Throwable) {
                binding.cpCardview.visibility = View.GONE
                call.cancel()
                Log.e("onFailure  ->", throwable.toString())
            }
        })
    }

    private fun getInitializeHeading1(baseLine: Any?, average: Any?, peak: Any?, highestOutput3: Any?, ranking2: Any?, ranking3: Any?) {
        binding.heading1AverageTextview.text = average.toString()
        binding.heading1PeakTextview.text = peak.toString()
        binding.heading1PlusMinusAverageTextview.text = ((average.toString().toDouble() - baseLine.toString().toDouble()).toString())
        binding.heading1PlusMinusPeakTextview.text = ((peak.toString().toDouble() - baseLine.toString().toDouble()).toString())
        binding.heading1Ranking2Textview.text = ranking2.toString()
        binding.heading1Ranking3Textview.text = ranking3.toString()
        binding.heading1HighestTextview.text = highestOutput3.toString()
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.backBtn -> {
                super.onBackPressed();
            }
            binding.heading1ViewListingForRanking2Btn -> {
                var intent = Intent(this, Output2ViewListingActivity::class.java)
                intent.putExtra("selectedMaterial", "Concrete 100"  /*binding.materialSelectionSpinner.selectedItem*/)
                intent.putExtra("isOutputTwo", true)
                startActivity(intent)
            }
            binding.heading1ViewListingForRanking3Btn -> {
                var intent = Intent(this, Output2ViewListingActivity::class.java)
                intent.putExtra("selectedMaterial", "Concrete 100"  /*binding.materialSelectionSpinner.selectedItem*/)
                intent.putExtra("isOutputTwo", false)
                startActivity(intent)
            }
        }
    }

    companion object {
        private const val TAG = "SummaryOutput2DataAnaly"
    }
}