package com.example.soundrecorder.viewmodels

import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clabs.caddyapp.network.ApiClient
import com.clabs.caddyapp.network.ApiInterface
import com.example.soundrecorder.R
import com.example.soundrecorder.activity.HomeActivity
import com.example.soundrecorder.activity.WebViewActivity
import com.example.soundrecorder.config.AppPreferences
import com.example.soundrecorder.kotlinclasses.NothingSelectedSpinnerAdapter
import com.example.soundrecorder.models.*
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class HomeViewModel(val homeActivity: HomeActivity) : ViewModel() {
    var onOptionId2OptionTitleChange = MutableLiveData<String>()

    init {
        onOptionId2OptionTitleChange.value = ""
    }

    private var optionId1OptionTitleArray = ArrayList<String>()
    private var optionId1MeterTitleArray = ArrayList<String>()
    private var optionId1yardTitleArray = ArrayList<String>()
    private var optionId2OptionTitleArray = ArrayList<String>()
    private var optionId3OptionTitleArray = ArrayList<String>()
    private var optionId4OptionTitleArray = ArrayList<String>()
    private var optionId5OptionTitleArray = ArrayList<String>()

    private lateinit var optionId1ArrayAdapter: ArrayAdapter<String>
    private lateinit var optionId2ArrayAdapter: ArrayAdapter<String>
    private lateinit var optionId3ArrayAdapter: ArrayAdapter<String>
    private lateinit var optionId4ArrayAdapter: ArrayAdapter<String>
    private lateinit var optionId5ArrayAdapter: ArrayAdapter<String>

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setUiAction() {
        homeActivity.binding.howToUseAppTextview.setPaintFlags(homeActivity.binding.howToUseAppTextview.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        setOnItemSelectedListenerOnEachSpinner()

//        homeActivity.binding.companyNameText.text = homeActivity.pSharedPref?.getString(
//                "companyNameString",
//                "companyNameStringNotExist"
//        ).toString().replace("\"", "");
        homeActivity.binding.howToUseAppTextview.text = homeActivity.pSharedPref!!.getString(
                "howToUseAppUrlTextString",
                "howToUseAppUrlTextStringNotExist"
        ).toString().replace("\"", "")
        val howToUseAppUrl = homeActivity.pSharedPref!!.getString(
                "howToUseAppUrlString",
                "howToUseAppUrlStringNotExist"
        ).toString().replace("\"", "")
        homeActivity.binding.howToUseAppTextview.setOnClickListener {
            val intent = Intent(homeActivity, WebViewActivity::class.java)
            intent.putExtra("url", howToUseAppUrl)
            homeActivity.startActivity(intent)
        }
        homeActivity.appPreferences = AppPreferences()
        homeActivity.appPreferences.init(homeActivity)

        getAllOptions()

        initializeAllSpinner()
    }

    private fun setOnItemSelectedListenerOnEachSpinner() {
        homeActivity.binding.option2.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0)
                    homeActivity.binding.option2Hint.visibility = View.GONE
                if (homeActivity.binding.option2.selectedItem != null) {
                    onOptionId2OptionTitleChange.value = homeActivity.binding.option2.selectedItem.toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        homeActivity.binding.option1.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0)
                    homeActivity.binding.option1Hint.visibility = View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        homeActivity.binding.option3.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0)
                    homeActivity.binding.option3Hint.visibility = View.GONE
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        homeActivity.binding.option4.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0)
                    homeActivity.binding.option4Hint.visibility = View.GONE
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        homeActivity.binding.option5.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0)
                    homeActivity.binding.option5Hint.visibility = View.GONE
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
    }

    private fun initializeAllSpinner() {
        optionId2ArrayAdapter = ArrayAdapter(homeActivity, R.layout.spinner_item, optionId2OptionTitleArray)
        optionId2ArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        homeActivity.binding.option2.adapter = NothingSelectedSpinnerAdapter(
                optionId2ArrayAdapter,
                R.layout.on_nothing_selection,
                homeActivity
        )

        optionId1ArrayAdapter = ArrayAdapter(homeActivity, R.layout.spinner_item, optionId1OptionTitleArray)
        optionId1ArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        homeActivity.binding.option1.adapter = optionId1ArrayAdapter
        homeActivity.binding.option1.adapter = NothingSelectedSpinnerAdapter(
                optionId1ArrayAdapter,
                R.layout.on_nothing_selection,
                homeActivity
        )

        optionId3ArrayAdapter = ArrayAdapter(homeActivity, R.layout.spinner_item, optionId3OptionTitleArray)
        optionId3ArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        homeActivity.binding.option3.adapter = NothingSelectedSpinnerAdapter(
                optionId3ArrayAdapter,
                R.layout.on_nothing_selection,
                homeActivity
        )

        optionId4ArrayAdapter = ArrayAdapter(homeActivity, R.layout.spinner_item, optionId4OptionTitleArray)
        optionId4ArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        homeActivity.binding.option4.adapter = NothingSelectedSpinnerAdapter(
                optionId4ArrayAdapter,
                R.layout.on_nothing_selection,
                homeActivity
        )

        optionId5ArrayAdapter = ArrayAdapter(homeActivity, R.layout.spinner_item, optionId5OptionTitleArray)
        optionId5ArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        homeActivity.binding.option5.adapter = NothingSelectedSpinnerAdapter(
                optionId5ArrayAdapter,
                R.layout.on_nothing_selection,
                homeActivity
        )
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    private fun getAllOptions() {
        homeActivity.binding.cpCardview.visibility = View.VISIBLE
        val call = ApiClient().getClient(homeActivity)!!.create(ApiInterface::class.java)
        Log.i(Companion.TAG, "getAllOptions: token is here " + homeActivity.appPreferences.token)
        call.getAllOptions(homeActivity.appPreferences.token).enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                try {
                    val res = Gson().toJson(response.body())
                    val mainObject = JSONObject(res)
                    Log.d("GET_ALL_OPTIONS", mainObject.toString())
                    if (mainObject.getBoolean("success")) {
                        homeActivity.binding.cpCardview.visibility = View.GONE
                        var data = mainObject.getJSONArray("data")
                        var i = 0
                        while (i < data.length()) {
                            var obj = data.getJSONObject(i)
                            when (obj.opt("optionId").toString().toInt()) {
                                1 -> {
                                    var optionId = obj.opt("optionId").toString()
                                    var heading = obj.opt("optionHeading").toString()
                                    var optionHashSet = HashSet<OptionDataClass>()
                                    var option = obj.getJSONArray("option")
                                    var j = 0

                                    homeActivity.binding.option1Hint.text = heading

                                    while (j < option.length()) {
                                        var optionObj = option.getJSONObject(j)
                                        optionId1OptionTitleArray.add(optionObj.opt("title").toString())
                                        optionHashSet.add(OptionDataClass(optionObj.opt("optionValueId").toString(), optionObj.opt("title").toString(), optionObj.opt("value").toString(), optionObj.opt("factor1").toString(), optionObj.opt("factor2").toString()))
                                        j++
                                    }

                                    var meterHashSet = HashSet<MeterDataClass>()
                                    var meters = obj.getJSONArray("meters")
                                    var k = 0
                                    while (k < meters.length()) {
                                        var metersObj = meters.getJSONObject(k)
                                        optionId1MeterTitleArray.add(metersObj.opt("title").toString())
                                        meterHashSet.add(MeterDataClass(metersObj.opt("title").toString(), metersObj.opt("value").toString()))
                                        k++
                                    }
                                    var yardHashSet = HashSet<YardDataClass>()
                                    var yards = obj.getJSONArray("yards")
                                    var l = 0
                                    while (l < yards.length()) {
                                        var yardsObj = yards.getJSONObject(l)
                                        optionId1yardTitleArray.add(yardsObj.opt("title").toString())
                                        yardHashSet.add(YardDataClass(yardsObj.opt("title").toString(), yardsObj.opt("value").toString()))
                                        l++
                                    }
                                    optionId1ArrayAdapter.notifyDataSetChanged()
                                    var optionId1DataClassObj = OptionId1DataClass(optionId, heading, optionHashSet, meterHashSet, yardHashSet)
                                    var optionId1DataClassObjString = Gson()!!.toJson(optionId1DataClassObj)
                                    homeActivity.pSharedPref!!.edit().putString("optionId1DataClassObjString", optionId1DataClassObjString).apply()
                                }
                                2 -> {
                                    var optionId = obj.opt("optionId").toString()
                                    var heading = obj.opt("optionHeading").toString()
                                    homeActivity.binding.option2Hint.text = heading
                                    var optionHashSet = HashSet<OptionDataClass>()
                                    var option = obj.getJSONArray("option")
                                    var j = 0
                                    while (j < option.length()) {
                                        var optionObj = option.getJSONObject(j)
                                        optionId2OptionTitleArray.add(optionObj.opt("title").toString())
                                        optionHashSet.add(OptionDataClass(optionObj.opt("optionValueId").toString(), optionObj.opt("title").toString(), optionObj.opt("value").toString(), optionObj.opt("factor1").toString(), optionObj.opt("factor2").toString()))
                                        j++
                                    }
                                    optionId2ArrayAdapter.notifyDataSetChanged()
                                    var optionId2DataClassObj = OptionId2DataClass(optionId, heading, optionHashSet)
                                    var optionId2DataClassObjString = Gson()!!.toJson(optionId2DataClassObj)
                                    homeActivity.pSharedPref!!.edit().putString("optionId2DataClassObjString", optionId2DataClassObjString).apply()
                                }
                                3 -> {
                                    var optionId = obj.opt("optionId").toString()
                                    var heading = obj.opt("optionHeading").toString()
                                    homeActivity.binding.option3Hint.text = heading
                                    var optionHashSet = HashSet<OptionDataClass>()
                                    var option = obj.getJSONArray("option")
                                    var j = 0
                                    while (j < option.length()) {
                                        var optionObj = option.getJSONObject(j)
                                        optionId3OptionTitleArray.add(optionObj.opt("title").toString())
                                        optionHashSet.add(OptionDataClass(optionObj.opt("optionValueId").toString(), optionObj.opt("title").toString(), optionObj.opt("value").toString(), optionObj.opt("factor1").toString(), optionObj.opt("factor2").toString()))
                                        j++
                                    }
                                    optionId3ArrayAdapter.notifyDataSetChanged()
                                    var OptionId3DataClassObj = OptionId3DataClass(optionId, heading, optionHashSet)
                                    var OptionId3DataClassObjString = Gson()!!.toJson(OptionId3DataClassObj)
                                    homeActivity.pSharedPref!!.edit().putString("OptionId3DataClassObjString", OptionId3DataClassObjString).apply()
                                }
                                4 -> {
                                    var optionId = obj.opt("optionId").toString()
                                    var heading = obj.opt("optionHeading").toString()
                                    homeActivity.binding.option4Hint.text = heading
                                    var optionHashSet = HashSet<OptionDataClass>()
                                    var option = obj.getJSONArray("option")
                                    var j = 0
                                    while (j < option.length()) {
                                        var optionObj = option.getJSONObject(j)
                                        optionId4OptionTitleArray.add(optionObj.opt("title").toString())
                                        optionHashSet.add(OptionDataClass(optionObj.opt("optionValueId").toString(), optionObj.opt("title").toString(), optionObj.opt("value").toString(), optionObj.opt("factor1").toString(), optionObj.opt("factor2").toString()))
                                        j++
                                    }
                                    optionId4ArrayAdapter.notifyDataSetChanged()
                                    var OptionId4DataClassObj = OptionId4DataClass(optionId, heading, optionHashSet)
                                    var OptionId4DataClassObjString = Gson()!!.toJson(OptionId4DataClassObj)
                                    homeActivity.pSharedPref!!.edit().putString("OptionId4DataClassObjString", OptionId4DataClassObjString).apply()
                                }
                                5 -> {
                                    var optionId = obj.opt("optionId").toString()
                                    var heading = obj.opt("optionHeading").toString()
                                    homeActivity.binding.option5Hint.text = heading
                                    var optionHashSet = HashSet<OptionDataClass>()
                                    var option = obj.getJSONArray("option")
                                    var j = 0
                                    while (j < option.length()) {
                                        var optionObj = option.getJSONObject(j)
                                        optionId5OptionTitleArray.add(optionObj.opt("title").toString())
                                        optionHashSet.add(OptionDataClass(optionObj.opt("optionValueId").toString(), optionObj.opt("title").toString(), optionObj.opt("value").toString(), optionObj.opt("factor1").toString(), optionObj.opt("factor2").toString()))
                                        j++
                                    }
                                    optionId5ArrayAdapter.notifyDataSetChanged()
                                    var OptionId5DataClassObj = OptionId5DataClass(optionId, heading, optionHashSet)
                                    var OptionId5DataClassObjString = Gson()!!.toJson(OptionId5DataClassObj)
                                    homeActivity.pSharedPref!!.edit().putString("OptionId5DataClassObjString", OptionId5DataClassObjString).apply()
                                }
                                6 -> {
                                    var optionId = obj.opt("optionId").toString()
                                    var heading = obj.opt("optionHeading").toString()
                                    var optionHashSet = HashSet<OptionDataClass>()
                                    var option = obj.getJSONArray("option")
                                    var j = 0
                                    var titleSet = HashSet<String>()
                                    while (j < option.length()) {
                                        var optionObj = option.getJSONObject(j)
                                        optionHashSet.add(OptionDataClass(optionObj.opt("optionValueId").toString(), optionObj.opt("title").toString(), optionObj.opt("value").toString(), optionObj.opt("factor1").toString(), optionObj.opt("factor2").toString()))
                                        titleSet.add(optionObj.getString("title"))
                                        j++
                                    }
                                    var optionId6DataClassObj = OptionId6DataClass(optionId, heading, optionHashSet)
                                    var optionId6DataClassObjString = Gson()!!.toJson(optionId6DataClassObj)
                                    homeActivity.pSharedPref!!.edit().putString("optionId6DataClassObjString", optionId6DataClassObjString).apply()
                                    homeActivity.pSharedPref!!.edit().putStringSet("titleSet", titleSet).apply()
                                }
                                7 -> {
                                    var optionId = obj.opt("optionId").toString()
                                    var heading = obj.opt("optionHeading").toString()
                                    var optionHashSet = HashSet<OptionDataClass>()
                                    var option = obj.getJSONArray("option")
                                    var j = 0
                                    while (j < option.length()) {
                                        var optionObj = option.getJSONObject(j)
                                        optionHashSet.add(OptionDataClass(optionObj.opt("optionValueId").toString(), optionObj.opt("title").toString(), optionObj.opt("value").toString(), optionObj.opt("factor1").toString(), optionObj.opt("factor2").toString()))
                                        j++
                                    }
                                    var optionId7DataClassObj = OptionId7DataClass(optionId, heading, optionHashSet)
                                    var optionId7DataClassObjString = Gson()!!.toJson(optionId7DataClassObj)
                                    homeActivity.pSharedPref!!.edit().putString("optionId7DataClassObjString", optionId7DataClassObjString).apply()
                                }
                                8 -> {
                                    var optionId = obj.opt("optionId").toString()
                                    var heading = obj.opt("optionHeading").toString()
                                    var optionHashSet = HashSet<OptionDataClass>()
                                    var option = obj.getJSONArray("option")
                                    var j = 0
                                    while (j < option.length()) {
                                        var optionObj = option.getJSONObject(j)
                                        optionHashSet.add(OptionDataClass(optionObj.opt("optionValueId").toString(), optionObj.opt("title").toString(), optionObj.opt("value").toString(), optionObj.opt("factor1").toString(), optionObj.opt("factor2").toString()))
                                        j++
                                    }
                                    var OptionId8DataClassObj = OptionId8DataClass(optionId, heading, optionHashSet)
                                    var OptionId8DataClassObjString = Gson()!!.toJson(OptionId8DataClassObj)
                                    homeActivity.pSharedPref!!.edit().putString("OptionId8DataClassObjString", OptionId8DataClassObjString).apply()
                                }
                                9 -> {
                                    var optionId = obj.opt("optionId").toString()
                                    var heading = obj.opt("optionHeading").toString()
                                    var optionHashSet = HashSet<OptionDataClass>()
                                    var option = obj.getJSONArray("option")
                                    var j = 0
                                    while (j < option.length()) {
                                        var optionObj = option.getJSONObject(j)
                                        optionHashSet.add(OptionDataClass(optionObj.opt("optionValueId").toString(), optionObj.opt("title").toString(), optionObj.opt("value").toString(), optionObj.opt("factor1").toString(), optionObj.opt("factor2").toString()))
                                        j++
                                    }
                                    var optionId9DataClassObj = OptionId9DataClass(optionId, heading, optionHashSet)
                                    var optionId9DataClassObjString = Gson()!!.toJson(optionId9DataClassObj)
                                    homeActivity.pSharedPref!!.edit().putString("optionId9DataClassObjString", optionId9DataClassObjString).apply()
                                }
                                10 -> {
                                    var optionId = obj.opt("optionId").toString()
                                    var heading = obj.opt("optionHeading").toString()
                                    var optionHashSet = HashSet<OptionDataClass>()
                                    var option = obj.getJSONArray("option")
                                    var j = 0
                                    while (j < option.length()) {
                                        var optionObj = option.getJSONObject(j)
                                        optionHashSet.add(OptionDataClass(optionObj.opt("optionValueId").toString(), optionObj.opt("title").toString(), optionObj.opt("value").toString(), optionObj.opt("factor1").toString(), optionObj.opt("factor2").toString()))
                                        j++
                                    }
                                    var optionId10DataClassObj = OptionId10DataClass(optionId, heading, optionHashSet)
                                    var optionId10DataClassObjString = Gson()!!.toJson(optionId10DataClassObj)
                                    homeActivity.pSharedPref!!.edit().putString("optionId10DataClassObjString", optionId10DataClassObjString).apply()
                                }
                            }
                            i++
                        }
                    } else {
                        homeActivity.binding.cpCardview.visibility = View.GONE
                        Toast.makeText(
                                homeActivity,
                                "problem is " + mainObject.getString("message"),
                                Toast.LENGTH_LONG
                        ).show()
                        Log.i(TAG, "onResponse: ok here")
                    }
                } catch (e: Exception) {
                    homeActivity.binding.cpCardview.visibility = View.GONE
                    Log.i(TAG, "onResponse: exception is her " + e.message.toString())
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<Any>, throwable: Throwable) {
                homeActivity.binding.cpCardview.visibility = View.GONE
                call.cancel()
                Log.e("onFailure  ->", throwable.toString())
            }
        })

    }

    fun changeOptionId1DataInSpinner() {
        if (homeActivity.binding.option2.selectedItem != null) {
            if (homeActivity.binding.option2.selectedItem.toString() == "Metric") {
                optionId1ArrayAdapter = ArrayAdapter(homeActivity, R.layout.spinner_item, optionId1MeterTitleArray)
                optionId1ArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                homeActivity.binding.option1.adapter = NothingSelectedSpinnerAdapter(
                        optionId1ArrayAdapter,
                        R.layout.on_nothing_selection,
                        homeActivity
                )
            } else {
                optionId1ArrayAdapter = ArrayAdapter(homeActivity, R.layout.spinner_item, optionId1yardTitleArray)
                optionId1ArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                homeActivity.binding.option1.adapter = NothingSelectedSpinnerAdapter(
                        optionId1ArrayAdapter,
                        R.layout.on_nothing_selection,
                        homeActivity
                )
            }
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}