package com.example.soundrecorder.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.clabs.caddyapp.network.ApiClient
import com.clabs.caddyapp.network.ApiInterface
import com.example.soundrecorder.adapters.WellcomeAdapter
import com.example.soundrecorder.databinding.ActivityWelcomeBinding
import com.example.soundrecorder.utils.GlobalOperation
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.util.ArrayList


class WelcomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityWelcomeBinding
    var pSharedPref: SharedPreferences? = null
    var dataList: ArrayList<String> = ArrayList()
    lateinit var welcomeAdapter: WellcomeAdapter
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        val view = binding.root
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(view)

        dataList = ArrayList()

        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        welcomeAdapter = WellcomeAdapter(dataList,this)
        binding.recyclerview.adapter = welcomeAdapter



        binding.btnLogin.setOnClickListener { goToLoginActivity() }
        binding.btnSignup.setOnClickListener { goToSignupActivity() }

        setUiAction()
    }

    private fun goToSignupActivity() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUiAction() {
        if (GlobalOperation.isNetworkConnected(this)){
            getBullets()
        }else{
            GlobalOperation.showDialog(
                this,
                "Please make sure that you are connected with network."
            )
        }
        if (pSharedPref == null){
            pSharedPref = getSharedPreferences("MyOutputs", Context.MODE_PRIVATE)
        }
    }



    @RequiresApi(Build.VERSION_CODES.M)
    private fun getBullets() {
        binding.cpCardview.visibility = View.VISIBLE
        Log.i(Companion.TAG, "getBullets: calling")
        val call = ApiClient().getClient(this)!!.create(ApiInterface::class.java)
        call.getBullets().enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                try {
                    val res = Gson().toJson(response.body())
                    val mainObject = JSONObject(res)
                    Log.d("GET_BULLETS_RESPONSE", mainObject.toString())
                    dataList.clear()
                    if (mainObject.getBoolean("success")) {
                        var bulletList = mainObject.getJSONObject("data").getJSONArray("bulletList")
                        for (i in 0..bulletList.length()-1){
                            val jsonObj = bulletList.getJSONObject(i)
                            dataList.add(jsonObj.getString("title"))
                        }
                        welcomeAdapter = WellcomeAdapter(dataList, this@WelcomeActivity)
                        binding.recyclerview.adapter = welcomeAdapter

                        binding.websiteLink.text =
                            mainObject.getJSONObject("data").getJSONObject("website").opt(
                                "title"
                            ).toString()
                        binding.websiteLink.setPaintFlags(binding.websiteLink.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)

                        binding.websiteLink.setOnClickListener {
                            val intent = Intent(this@WelcomeActivity, WebViewActivity::class.java)
                            intent.putExtra(
                                "url",
                                mainObject.getJSONObject("data").getJSONObject("website").opt(
                                    "url"
                                ).toString()
                            )
                            startActivity(intent)


                        }

                        var termsAndConditonUrl =
                            mainObject.getJSONObject("data").getJSONObject("termsAndCondition")
                                .opt("url").toString()
                        var termsAndConditionTitle =
                            mainObject.getJSONObject("data").getJSONObject("termsAndCondition")
                                .opt("title").toString()

                        var companyName =
                            mainObject.getJSONObject("data").opt("companyName").toString()
                        binding.companyNameText.text = companyName.toString()

                        var howToUseAppUrl =
                            mainObject.getJSONObject("data").getJSONObject("howToUseApp").opt(
                                "url"
                            ).toString()
                        var howToUseAppUrlText =
                            mainObject.getJSONObject("data").getJSONObject("howToUseApp").opt(
                                "title"
                            ).toString()
                        var registrationAmount = mainObject.getJSONObject("data").getInt("registrationAmount")

                        var gson = Gson()
                        var termsAndConditonUrlString = gson.toJson(termsAndConditonUrl)
                        var termsAndConditionTitleString = gson.toJson(termsAndConditionTitle)
                        var companyNameString = gson.toJson(companyName)
                        var howToUseAppUrlString = gson.toJson(howToUseAppUrl)
                        var howToUseAppUrlTextString = gson.toJson(howToUseAppUrlText)
                        if (pSharedPref == null) {
                            pSharedPref = getSharedPreferences("MyOutputs", Context.MODE_PRIVATE)
                        }
                        pSharedPref!!.edit()
                            .putString("termsAndConditonUrlString", termsAndConditonUrlString)
                            .apply()
                        pSharedPref!!.edit()
                            .putString(
                                "termsAndConditionTitleString",
                                termsAndConditionTitleString
                            )
                            .apply()
                        pSharedPref!!.edit()
                            .putString("companyNameString", companyNameString)
                            .apply()
                        pSharedPref!!.edit()
                            .putString("howToUseAppUrlString", howToUseAppUrlString)
                            .apply()
                        pSharedPref!!.edit()
                            .putString("howToUseAppUrlTextString", howToUseAppUrlTextString)
                            .apply()
                        pSharedPref!!.edit()
                            .putInt("registrationAmount", registrationAmount)
                            .apply()

                        binding.cpCardview.visibility = View.GONE
                    }
                    binding.cpCardview.visibility = View.GONE
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

    companion object {
        private const val TAG = "WelcomeActivity"
    }

}