package com.example.soundrecorder.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.clabs.caddyapp.network.ApiClient
import com.clabs.caddyapp.network.ApiInterface
import com.example.soundrecorder.R
import com.example.soundrecorder.config.AppPreferences
import com.example.soundrecorder.databinding.ActivitySigupTwoBinding
import com.example.soundrecorder.kotlinclasses.NothingSelectedSpinnerAdapter
import com.example.soundrecorder.models.Country
import com.example.soundrecorder.utils.GlobalOperation
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class SignupTwoActivity : AppCompatActivity() {

    lateinit var binding: ActivitySigupTwoBinding
    private lateinit var stateTypeAdapter: ArrayAdapter<String>
    private lateinit var genderAdapter: ArrayAdapter<String>
    private lateinit var ageAdapter: ArrayAdapter<String>
    private lateinit var stateListAdapter: ArrayAdapter<String>
    private lateinit var countryListAdapter: ArrayAdapter<String>
    private var allCountryArrayList = ArrayList<String>()
    private var allCountryObjArrayList = ArrayList<Country>()
    private var listOfStateOfSelectedCountry = ArrayList<String>()
    private var userId: String = ""
    lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_sigup_two)

        binding = ActivitySigupTwoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        appPreferences = AppPreferences()
        appPreferences.init(this)


        val bundle = intent.extras
        if(bundle != null){
            userId = bundle.getString("user_id").toString()
        }

        setAdapterOnEachSpinner()
        setOnItemSelectedListenerOnEachSpinner()

        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.btnContinue.setOnClickListener {
          if ( binding.clubOrSchoolEdt.text.isBlank()){
             GlobalOperation.showDialog(this,"Club or school can not be empty")
             binding.clubOrSchoolEdt.requestFocus()
         }else if (binding.competitionEdt.text.isBlank()){
             GlobalOperation.showDialog(this,"Please enter competition.")
             binding.competitionEdt.requestFocus()
         }else if (binding.selectCountry.selectedItem == null){
             GlobalOperation.showDialog(this,"Please select country.")
             binding.selectCountry.requestFocus()
         }else if (binding.stateType.selectedItem == null){
             GlobalOperation.showDialog(this,"Please select state type.")
             binding.stateType.requestFocus()
         }else if (binding.state.selectedItem == null){
             GlobalOperation.showDialog(this,"Please select state.")
             binding.state.requestFocus()
         }else if (binding.ageSpinner.selectedItem == null){
             GlobalOperation.showDialog(this,"Please select age.")
             binding.ageSpinner.requestFocus()
         }else if (binding.ageSpinner.selectedItem == null){
             GlobalOperation.showDialog(this,"Please select gender.")
             binding.ageSpinner.requestFocus()
         }else {
              callRegister()
          }
        }
    }

    private fun setAdapterOnEachSpinner() {


        var genderArraList = arrayListOf<String>("Male", "Female")
        genderAdapter = ArrayAdapter(this, R.layout.spinner_item, genderArraList)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = NothingSelectedSpinnerAdapter(genderAdapter, R.layout.on_nothing_selection, this)

        var ageArraList = arrayListOf<String>("Under 11", "Under 12","Under 13","Under 14","Under 15","Under 17","Senior")
        ageAdapter = ArrayAdapter(this, R.layout.spinner_item, ageArraList)
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ageSpinner.adapter = NothingSelectedSpinnerAdapter(ageAdapter, R.layout.on_nothing_selection, this)


        var arrayListOfStateType = arrayListOf<String>("State", "Provience", "County")
        stateTypeAdapter = ArrayAdapter(this, R.layout.spinner_item, arrayListOfStateType)
        stateTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.stateType.adapter = NothingSelectedSpinnerAdapter(stateTypeAdapter, R.layout.on_nothing_selection, this)

        getAllCountries()
        countryListAdapter = ArrayAdapter(this@SignupTwoActivity, R.layout.spinner_item, allCountryArrayList)
        countryListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.selectCountry.adapter = NothingSelectedSpinnerAdapter(countryListAdapter, R.layout.on_nothing_selection, this@SignupTwoActivity)

        stateListAdapter = ArrayAdapter(this@SignupTwoActivity, R.layout.spinner_item, listOfStateOfSelectedCountry)
        stateListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.state.adapter = NothingSelectedSpinnerAdapter(stateListAdapter, R.layout.on_nothing_selection, this@SignupTwoActivity)
    }

    private fun setOnItemSelectedListenerOnEachSpinner() {
        binding.selectCountry.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    binding.selectCountryHint.visibility = View.GONE
                    if (binding.selectCountry.selectedItem != null) {
                        for (obj in allCountryObjArrayList) {
                            if (obj.countryName.toString().equals(binding.selectCountry.selectedItem.toString())){
                                getStateOfSelectedCountry(obj.shortName.toString())
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        binding.stateType.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0)
                    binding.stateTypeHint.visibility = View.GONE
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        binding.state.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0)
                    binding.stateNameHint.visibility = View.GONE
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        binding.genderSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0)
                    binding.genderHint.visibility = View.GONE
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        binding.ageSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0)
                    binding.ageHint.visibility = View.GONE
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
    }


    private fun getAllCountries() {
        binding.cpCardview.visibility = View.VISIBLE
        val call = ApiClient().getClient(this)!!.create(ApiInterface::class.java)
        call.getAllCountries().enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                try {
                    if (response.body() == null) {
                        binding.cpCardview.visibility = View.GONE
                        var errorText = response.errorBody()
                        Toast.makeText(this@SignupTwoActivity, "" + errorText, Toast.LENGTH_SHORT).show()
                        Log.i(TAG, "onResponse: error body " + errorText)
                    } else {
                        val res = Gson().toJson(response.body())
                        val mainObject = JSONObject(res)
                        Log.d("ALL_COUNTRY_RESPONSE", mainObject.toString())
                        if (mainObject.getBoolean("success")) {
                            binding.cpCardview.visibility = View.GONE
                            var i = 0
                            while (i < mainObject.getJSONArray("data").length()) {
                                var obj = mainObject.getJSONArray("data").getJSONObject(i)
                                allCountryArrayList.add(obj.getString("countryName"))
                                allCountryObjArrayList.add(Country(obj.getString("countryName"), obj.getString("shortName")))
                                i++
                            }
                            Log.i(TAG, "onResponse: sisse "+allCountryObjArrayList.size)
                            countryListAdapter.notifyDataSetChanged()
                        } else {
                            binding.cpCardview.visibility = View.GONE
                            Toast.makeText(
                                this@SignupTwoActivity,
                                "problem is " + mainObject.getString("message"),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    binding.cpCardview.visibility = View.GONE
                    Log.i(TAG, "onResponse: exception isss her " + e.message.toString())
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

    private fun getStateOfSelectedCountry(countryShortName: String) {
        var selectedCountryNameCode = countryShortName
        binding.cpCardview.visibility = View.VISIBLE
        val map = HashMap<String,Any>()
        map.put("countryShortName",selectedCountryNameCode)
        val call = ApiClient().getClient(this)!!.create(ApiInterface::class.java)
        call.getAllState(map).enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                try {
                    if (response.body() == null) {
                        binding.cpCardview.visibility = View.GONE
                        var errorText = response.errorBody()
                        Toast.makeText(this@SignupTwoActivity, "" + errorText, Toast.LENGTH_SHORT).show()
                        Log.i(TAG, "onResponse: error body " + errorText)
                    } else {
                        listOfStateOfSelectedCountry.clear()
                        val res = Gson().toJson(response.body())
                        val mainObject = JSONObject(res)
                        Log.d("ALL_STATE_RESPONSE", mainObject.toString())
                        if (mainObject.getBoolean("success")) {
                            binding.cpCardview.visibility = View.GONE
                            var i = 0
                            while (i < mainObject.getJSONArray("data").length()) {
                                listOfStateOfSelectedCountry.add(mainObject.getJSONArray("data").getJSONObject(i).getString("stateName"))
                                i++
                            }
                            stateListAdapter.notifyDataSetChanged()
                        } else {
                            binding.cpCardview.visibility = View.GONE
                            Toast.makeText(
                                this@SignupTwoActivity,
                                "problem is " + mainObject.getString("message"),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    binding.cpCardview.visibility = View.GONE
                    Log.i(TAG, "onResponse: exception isss her " + e.message.toString())
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



    @RequiresApi(Build.VERSION_CODES.M)
    private fun callRegister() {
        binding.cpCardview.visibility = View.VISIBLE
        val map = HashMap<String,Any>()
        map.put("clubOrSchoolName",binding.clubOrSchoolEdt.text.toString())
        map.put("competition",binding.competitionEdt.text.toString())
        map.put("ageGroup",binding.ageSpinner.selectedItem.toString())
        map.put("gender",binding.genderSpinner.selectedItem.toString())
        map.put("country",binding.selectCountry.selectedItem.toString())
        map.put("state",binding.state.selectedItem.toString())
        map.put("stateType",binding.stateType.selectedItem.toString())
        map.put("userId",userId)
        System.out.println("signup_map_is two "+map.toString())
        val call = ApiClient().getClient(this)!!.create(ApiInterface::class.java)
        call.updateProfile(map,appPreferences.token).enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                try {
                    if (response.body() == null) {
                        binding.cpCardview.visibility = View.GONE
                        var errorText = response.errorBody()
                        Toast.makeText(this@SignupTwoActivity, "" + errorText, Toast.LENGTH_SHORT)
                            .show()
                        Log.i(TAG, "onResponse: error body text "+errorText)
                    }else{
                        val res = Gson().toJson(response.body())
                        val mainObject = JSONObject(res)
                        Log.d("SIGN_UP_RESPONSE", mainObject.toString())
                        if (mainObject.getBoolean("success")) {
                            Toast.makeText(
                                this@SignupTwoActivity,
                                "Profile info inserted.",
                                Toast.LENGTH_LONG
                            ).show()
                            //  appPreferences.isLogin = true
//                            appPreferences.email = mainObject.getJSONObject("data").optString("email")
//                            appPreferences.uuid = mainObject.getJSONObject("data").optString("uuid")
//                            appPreferences.token = mainObject.getJSONObject("data").optString("token")
                            binding.cpCardview.visibility = View.GONE
                            val intent = Intent(this@SignupTwoActivity, PaymentActivity::class.java)
                            startActivity(intent)
                        } else {
                            binding.cpCardview.visibility = View.GONE
                            Toast.makeText(
                                this@SignupTwoActivity,
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

    companion object {
        private const val TAG = "SignupTwoActivity"
    }
}