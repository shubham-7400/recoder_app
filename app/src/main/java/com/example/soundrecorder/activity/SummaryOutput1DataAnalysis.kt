package com.example.soundrecorder.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.clabs.caddyapp.network.ApiClient
import com.clabs.caddyapp.network.ApiInterface
import com.example.soundrecorder.R
import com.example.soundrecorder.config.AppPreferences
import com.example.soundrecorder.databinding.ActivitySummaryOutput1DataAnalysisBinding
import com.example.soundrecorder.models.*
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class SummaryOutput1DataAnalysis : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivitySummaryOutput1DataAnalysisBinding
    private lateinit var heading4SelectionAdapter: ArrayAdapter<String>
    private lateinit var heading4SelectRankingAdapter: ArrayAdapter<String>
    lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryOutput1DataAnalysisBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setUiAction()


    }

    private fun setUiAction() {
        appPreferences = AppPreferences()
        appPreferences.init(this)

        binding.viewListingForHeading3Heading4Btn.setOnClickListener(this)
        binding.backBtn.setOnClickListener(this)

        heading4SelectionAdapter = ArrayAdapter(this, R.layout.spinner_item, arrayListOf("a", "b", "c", "d", "e", "f", "g", "s"))
        heading4SelectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.selectionForHeading4Spinner.adapter = heading4SelectionAdapter

        heading4SelectRankingAdapter = ArrayAdapter(this, R.layout.spinner_item, arrayListOf("world", "county", "state", "provience", "country"))
        heading4SelectRankingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.rankingForHeading4Spinner.adapter = heading4SelectRankingAdapter

        callApiToDataAnalysis()

    }

    private fun callApiToDataAnalysis() {
        binding.cpCardview.visibility = View.VISIBLE
        var map = HashMap<String,Any>()
        map.put("userId",appPreferences.uuid)
        map.put("selectedFactor",binding.selectionForHeading4Spinner.selectedItem.toString())
        map.put("selectedRanking",binding.rankingForHeading4Spinner.selectedItem.toString())
        val call = ApiClient().getClient(this)!!.create(ApiInterface::class.java)
        call.getDataAnalysis(map, appPreferences.token).enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                try {
                    val res = Gson().toJson(response.body())
                    val mainObject = JSONObject(res)
                    Log.d("GET_DATA_ANALYSIS", mainObject.toString())
                    if (mainObject.getBoolean("success")) {
                        binding.cpCardview.visibility = View.GONE
                        Log.i(TAG, "onResponse: got success"+mainObject.getJSONObject("data").opt("ranking"))
                        var heading3Obj = mainObject.getJSONObject("data").getJSONObject("heading3")
                        getInitializeHeading3(heading3Obj.opt("baseLine"), heading3Obj.opt("average"), heading3Obj.opt("peak"))
                        var heading4Obj = mainObject.getJSONObject("data").getJSONObject("heading4")
                        getInitializeHeading4(heading4Obj.opt("baseLine"), heading4Obj.opt("average"), heading4Obj.opt("peak"),mainObject.getJSONObject("data").opt("ranking"))
                    } else {
                        binding.cpCardview.visibility = View.GONE
                        Toast.makeText(
                                this@SummaryOutput1DataAnalysis,
                                "problem is " + mainObject.getString("message"),
                                Toast.LENGTH_LONG
                        ).show()
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

    private fun getInitializeHeading3(baseLine: Any?, average: Any?, peak: Any?) {
        binding.heading3BaselineTextview.text = baseLine.toString()
        binding.heading3AverageTextview.text = average.toString()
        binding.heading3PeakTextview.text = peak.toString()
        binding.heading3PlusMinusAverageTextview.text = ((average.toString().toDouble() - baseLine.toString().toDouble()).toString())
        binding.heading3PlusMinusPeakTextview.text = (peak.toString().toDouble() - baseLine.toString().toDouble()).toString()
    }

    private fun getInitializeHeading4(baseLine: Any?, average: Any?, peak: Any?, ranking: Any?) {
        binding.heading4PlusMinusAverageTextview.text = ((average.toString().toDouble() - baseLine.toString().toDouble()).toString())
        binding.heading4PlusMinusPeakTextview.text = (peak.toString().toDouble() - baseLine.toString().toDouble()).toString()
        binding.rankingForHeading4Textview.text = ranking.toString()
    }

    companion object {
        private const val TAG = "SummaryOutput1DataAnaly"
    }

    override fun onClick(v: View?) {
         when(v){
             binding.backBtn -> {
                 super.onBackPressed();
             }

             binding.viewListingForHeading3Heading4Btn -> {
                 var intent = Intent(this, Output1ViewListingActivity::class.java)
                 intent.putExtra("selectedFactor",binding.selectionForHeading4Spinner.selectedItem.toString())
                 intent.putExtra("selectedRanking", binding.rankingForHeading4Spinner.selectedItem.toString())
                 startActivity(intent)
             }
         }
    }
}