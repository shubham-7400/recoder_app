package com.example.soundrecorder.viewmodels

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clabs.caddyapp.network.ApiClient
import com.clabs.caddyapp.network.ApiInterface
import com.example.soundrecorder.activity.Output1ViewListingActivity
import com.example.soundrecorder.models.Output1ViewListingDataClass
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class Output1ViewListingViewModel(val output1ViewListingActivity: Output1ViewListingActivity) : ViewModel()  {
    var arrayList  = ArrayList<Output1ViewListingDataClass>()
    var arrayListOfOutput1ViewListingDataClass = MutableLiveData<ArrayList<Output1ViewListingDataClass>>()

    fun callApiTogetViewListingResultForOutput1(selectedFactor: String?, selectedRanking: String?) {
        output1ViewListingActivity!!.binding.cpCardview.visibility = View.VISIBLE
        var map = HashMap<String,Any>()
        map.put("selectedFactor", selectedFactor!!)
        map.put("selectedRanking", selectedRanking!!)
        val call = ApiClient().getClient(output1ViewListingActivity)!!.create(ApiInterface::class.java)
        call.viewListing(map, output1ViewListingActivity!!.appPreferences.token).enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                try {
                    val res = Gson().toJson(response.body())
                    val mainObject = JSONObject(res)
                    Log.d("VIEW_LISTING", mainObject.toString())
                    if (mainObject.getBoolean("success")) {
                        output1ViewListingActivity!!.binding.cpCardview.visibility = View.GONE
                        var i = 0
                        while (i < mainObject.getJSONArray("data").length() - 1) {
                            var obj = mainObject.getJSONArray("data").getJSONObject(i)
                            arrayList.add(Output1ViewListingDataClass(obj.opt("userId") as String, obj.opt("outputDate") as String, obj.opt("output1") as String, obj.opt("factorPercentage") as String))
                            i++
                        }
                        arrayListOfOutput1ViewListingDataClass.value = arrayList
                    } else {
                        output1ViewListingActivity!!.binding.cpCardview.visibility = View.GONE
                        Toast.makeText(
                                output1ViewListingActivity,
                                "problem is " + mainObject.getString("message"),
                                Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    output1ViewListingActivity!!.binding.cpCardview.visibility = View.GONE
                    Log.i( TAG, "onResponse: exception is her " + e.message.toString())
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<Any>, throwable: Throwable) {
                output1ViewListingActivity!!.binding.cpCardview.visibility = View.GONE
                call.cancel()
                Log.e("onFailure  ->", throwable.toString())
            }
        })
    }


    companion object {
        private const val TAG = "Output1ViewListingViewM"
    }
}