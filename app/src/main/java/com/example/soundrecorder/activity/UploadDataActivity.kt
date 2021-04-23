package com.example.soundrecorder.activity

import android.R
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.clabs.caddyapp.network.ApiClient
import com.clabs.caddyapp.network.ApiInterface
import com.example.soundrecorder.adapters.SelectedUserToUploadDataAdapter
import com.example.soundrecorder.adapters.UploadDataAdapter
import com.example.soundrecorder.config.AppPreferences
import com.example.soundrecorder.databinding.ActivityUploadDataBinding
import com.example.soundrecorder.models.*
import com.example.soundrecorder.utils.GlobalOperation
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.lang.reflect.Type
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class UploadDataActivity : AppCompatActivity(),  View.OnClickListener {
    lateinit var binding: ActivityUploadDataBinding
    var pSharedPref: SharedPreferences? = null
    var mContext: Context? = null
    val dec = DecimalFormat("####.0000")
    private var uploadDataAdapter: UploadDataAdapter? = null
    lateinit var appPreferences: AppPreferences
    private var listOfUser = ArrayList<UserDataClass>()
    private var selectedUserToUploadDataAdapter: SelectedUserToUploadDataAdapter? = null
    private var listOfSelectedUser = ArrayList<UserDataClass>()
    private var listOfSelectedUserUUID = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadDataBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        setUiActions()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                getUsers(newText)
                if (newText == "") {
                    binding.verticalRecyclerviewOfUserToShareData.visibility = View.GONE
                    binding.horizontalRecyclerviewOfUserToShareData.visibility = View.VISIBLE
                }
                return false
            }

        })


    }

    private fun getUsers(newText: String?) {
        listOfUser.clear()
        listOfSelectedUserUUID.clear()
        for (user in listOfSelectedUser){
            listOfSelectedUserUUID.add(user.uuid)
        }
        binding.horizontalRecyclerviewOfUserToShareData.visibility = View.GONE
        binding.verticalRecyclerviewOfUserToShareData.visibility = View.VISIBLE
        var map = HashMap<String, Any>()
        map.put("userId", appPreferences.uuid)
        map.put("searchString", newText!!)
        map.put("selectedUser", listOfSelectedUserUUID)
        val call = ApiClient().getClient(this)!!.create(ApiInterface::class.java)
        call.getAllUser(map, appPreferences.token).enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                try {
                    if (response.body() == null) {
                        val errorText = response.errorBody()?.string()!!
                        val errorJsonObj = JSONObject(errorText)
                        Toast.makeText(this@UploadDataActivity, "" + errorText, Toast.LENGTH_SHORT)
                                .show()
                        Log.i(TAG, "onResponse: error body " + errorJsonObj.get("message"))
                    } else {
                        val res = Gson().toJson(response.body())
                        val mainObject = JSONObject(res)
                        Log.d("GET_ALL_USER", mainObject.toString())
                        if (mainObject.getJSONArray("data").length() != 0) {
                            if (mainObject.getBoolean("success")) {
                                for (i in 0..mainObject.getJSONArray("data").length() - 1) {
                                    var userObj = mainObject.getJSONArray("data").getJSONObject(i)
                                    val user = UserDataClass(
                                            userObj.opt("id").toString(),
                                            userObj.opt("name").toString(),
                                            userObj.opt("uuid").toString(),
                                            userObj.opt("phoneNumber").toString()
                                    )
                                    listOfUser.add(user)
                                }
                                uploadDataAdapter!!.notifyDataSetChanged()
                            }
                        } else {
                            binding.horizontalRecyclerviewOfUserToShareData.visibility = View.VISIBLE
                            binding.verticalRecyclerviewOfUserToShareData.visibility = View.GONE
                        }
                    }
                } catch (e: Exception) {
                    binding.horizontalRecyclerviewOfUserToShareData.visibility = View.VISIBLE
                    binding.verticalRecyclerviewOfUserToShareData.visibility = View.GONE
                    Log.i(Companion.TAG, "onResponse: exception is her " + e.message.toString())
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<Any>, throwable: Throwable) {
                call.cancel()
                Log.e("onFailure  ->", throwable.toString())
            }
        })

    }

    private fun setUiActions() {
        pSharedPref = getSharedPreferences("MyOutputs", MODE_PRIVATE)
        binding.companyNameText.text = pSharedPref?.getString(
                "companyNameString",
                "companyNameStringNotExist"
        ).toString().replace("\"", "");

        mContext = this

        appPreferences = AppPreferences()
        appPreferences.init(this)

        binding.verticalRecyclerviewOfUserToShareData.visibility = View.GONE
        binding.horizontalRecyclerviewOfUserToShareData.visibility = View.GONE

        selectedUserToUploadDataAdapter = SelectedUserToUploadDataAdapter(
                listOfSelectedUser,
                this,
                listOfSelectedUserUUID
        )
        binding.horizontalRecyclerviewOfUserToShareData.adapter = selectedUserToUploadDataAdapter
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.SPACE_BETWEEN
        binding.horizontalRecyclerviewOfUserToShareData.setLayoutManager(layoutManager)

        uploadDataAdapter = UploadDataAdapter(
                listOfUser,
                this,
                listOfSelectedUser,
                selectedUserToUploadDataAdapter!!,
                listOfSelectedUserUUID
        )
        binding.verticalRecyclerviewOfUserToShareData.adapter = uploadDataAdapter

        binding.backBtn.setOnClickListener(this)
        binding.uploadDataBtn.setOnClickListener(this)
    }




    override fun onClick(v: View?) {
         when (v){
             binding.backBtn -> {
                 super.onBackPressed();
             }

             binding.uploadDataBtn -> {
                 uploadData()
             }
         }
    }

    private fun uploadData() {
        var jsHashSetOutput1String = pSharedPref!!.getString("jsHashSetOutput1String", "jsHashSetOutput1StringNotExist")
        var gson = Gson()
        var typeOutput1DataClass: Type? = object : TypeToken<HashSet<SumarryOutput1DataClass>>() {}.getType()
        var SumarryOutput1DataClassHashSet: HashSet<SumarryOutput1DataClass> = gson.fromJson(jsHashSetOutput1String, typeOutput1DataClass)
        var listOfOutput1DataClassObj: ArrayList<SumarryOutput1DataClass> = ArrayList<SumarryOutput1DataClass>(SumarryOutput1DataClassHashSet)

        var jsHashSetOutput2String = pSharedPref!!.getString("jsHashSetOutput2String", "jsHashSetOutput2StringgNotExist")
        var typeOutput2DataClass: Type? = object : TypeToken<HashSet<SumarryOutput2DataClass>>() {}.getType()
        var SumarryOutput2DataClassHashSet: HashSet<SumarryOutput2DataClass> = gson.fromJson(jsHashSetOutput2String, typeOutput2DataClass)
        var listOfOutput2DataClassObj  = ArrayList<SumarryOutput2DataClass>(SumarryOutput2DataClassHashSet)

        var jsHashSetOutput3String = pSharedPref!!.getString("jsHashSetOutput3String", "jsHashSetOutput3StringNotExist")
        var typeOutput3DataClass: Type? = object : TypeToken<HashSet<SumarryOutput3DataClass>>() {}.getType()
        var SumarryOutput3DataClassHashSet: HashSet<SumarryOutput3DataClass> = gson.fromJson(jsHashSetOutput3String, typeOutput3DataClass)
        val listOfOutput3DataClassObj  = ArrayList<SumarryOutput3DataClass>(SumarryOutput3DataClassHashSet)

        var hashSetToHoldOptionBox6ObjString = pSharedPref?.getString("hashSetToHoldOptionBox6ObjJsonString", "hashSetToHoldOptionBox6ObjJsonStringNotExist")
        var type: Type? = object : TypeToken<HashSet<OptionBox6DataClass>>() {}.getType()
        var hashSetToHoldOptionBox6Obj: HashSet<OptionBox6DataClass> = gson.fromJson(hashSetToHoldOptionBox6ObjString, type)
        var arrayListToHoldOptionBox6Obj = ArrayList<OptionBox6DataClass>(hashSetToHoldOptionBox6Obj)

        val peakOfOutput1 = getOutput1Peak(listOfOutput1DataClassObj)
        var mapOfPeakOutputsAccordingToTitle = getPeakOutputsAccordingTotitle(listOfOutput2DataClassObj, listOfOutput3DataClassObj, arrayListToHoldOptionBox6Obj)

        callApiToUploadResult(peakOfOutput1, mapOfPeakOutputsAccordingToTitle)
    }

    private fun callApiToUploadResult(
            peakOfOutput1: Double,
            mapOfPeakOutputsAccordingToTitle: HashMap<String, Any>
    ) {
        var jsHashSetOutput1String = pSharedPref!!.getString("jsHashSetOutput1String", "jsHashSetOutput1StringNotExist")
        binding.companyNameText.text = pSharedPref?.getString("companyNameString", "companyNameStringNotExist").toString().replace("\"", "");
        var gson = Gson()
        var type: Type? = object : TypeToken<HashSet<SumarryOutput1DataClass>>() {}.getType()
        var SumarryOutput1DataClassHashSet: HashSet<SumarryOutput1DataClass> = gson.fromJson(jsHashSetOutput1String, type)
        var list: ArrayList<SumarryOutput1DataClass> = ArrayList<SumarryOutput1DataClass>(SumarryOutput1DataClassHashSet)

        listOfSelectedUserUUID.clear()
        for (user in listOfSelectedUser){
            listOfSelectedUserUUID.add(user.uuid)
        }

        var map = mapOfPeakOutputsAccordingToTitle
        map.put("output1", peakOfOutput1)
        map.put("outputDate", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()))
        map.put("shareWithUser", listOfSelectedUserUUID)
        map.put("userId", appPreferences.uuid)
        map.put("arrayOfFactor1", GlobalOperation.getFactor1ArrayList(list))
        map.put("arrayOfFactor2", GlobalOperation.getFactor2ArrayList(list))
        Log.i(TAG, "callApiToUploadResult: map final is here " + map)
        binding.cpCardview.visibility = View.VISIBLE
        val call = ApiClient().getClient(this)!!.create(ApiInterface::class.java)
        call.uploadResult(map, appPreferences.token).enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                try {
                    if (response.body() == null) {
                        binding.cpCardview.visibility = View.GONE
                        val errorText = response.errorBody()?.string()!!
                        val errorJsonObj = JSONObject(errorText)
                        Toast.makeText(this@UploadDataActivity, "" + errorText, Toast.LENGTH_SHORT)
                                .show()
                        Log.i(TAG, "onResponse: error bodyyy " + errorJsonObj.get("message"))
                    } else {
                        val res = Gson().toJson(response.body())
                        val mainObject = JSONObject(res)
                        Log.d("UPLOAD_RESULT", mainObject.toString())
                        if (mainObject.getBoolean("success")) {
                            binding.cpCardview.visibility = View.GONE
                            GlobalOperation.showDataUploadedDialog(mContext)
                        } else {
                            binding.cpCardview.visibility = View.GONE
                            Toast.makeText(
                                    this@UploadDataActivity,
                                    " data did't get upload ",
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    binding.cpCardview.visibility = View.GONE
                    Log.i(Companion.TAG, "onResponse: exception is her " + e.message.toString())
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

    private fun getPeakOutputsAccordingTotitle(
            listOfOutput2DataClassObj: ArrayList<SumarryOutput2DataClass>,
            listOfOutput3DataClassObj: ArrayList<SumarryOutput3DataClass>,
            arrayListToHoldOptionBox6Obj: ArrayList<OptionBox6DataClass>
    ): HashMap<String, Any> {
        var  map = HashMap<String, Any>()
        for (option6Obj in arrayListToHoldOptionBox6Obj){
            var peakOutput2 = 0.0
            var peakOutput3 = 0.0
            var peakOutput4 = 0.0
            for (obj in listOfOutput2DataClassObj){
                if (option6Obj.title.toString() == obj.type)
                {
                    if (peakOutput2 < obj.output2.toDouble()){
                        peakOutput2 = obj.output2.toDouble()
                        peakOutput4 = obj.output4.toDouble()
                    }
                }
            }
            for (obj in listOfOutput3DataClassObj){
                if (option6Obj.title == obj.type){
                    if (peakOutput4 < obj.output3.toDouble()){
                        peakOutput4 = obj.output3.toDouble()
                    }
                }
            }
            map.put(option6Obj.title, arrayListOf(peakOutput2, peakOutput3, peakOutput4))
        }
        Log.i(TAG, "getPeakOutputsAccordingTotitle: map is now " + map)
        return map
    }

    private fun getOutput1Peak(listOfOutput1DataClassObj: ArrayList<SumarryOutput1DataClass>) : Double {
        var highestOutput = listOfOutput1DataClassObj[0].output.toDouble()
        var totalOutput = 0.0
        for (obj in listOfOutput1DataClassObj.iterator()) {
            totalOutput = totalOutput + obj.output.toDouble()
            if (highestOutput < obj.output.toDouble()) {
                highestOutput = obj.output.toDouble()
            }
        }
        Log.i(TAG, "setActionUi: total output " + totalOutput)
        return dec.format(highestOutput).toDouble()
    }

    companion object {
        private const val TAG = "UploadDataActivity"
    }
}