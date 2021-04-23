package com.example.soundrecorder.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.clabs.caddyapp.network.ApiClient
import com.clabs.caddyapp.network.ApiInterface
import com.example.soundrecorder.config.AppPreferences
import com.example.soundrecorder.databinding.ActivitySplashBinding
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    lateinit var user: AppPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        user = AppPreferences()
        user.init(this)

        isLogedIn()

    }



    private fun isLogedIn() {
         if (user.isLogin)
         {
             val intent = Intent(this,HomeActivity::class.java)
             startActivity(intent)
             finish()
         }else{
             val intent = Intent(this,WelcomeActivity::class.java)
             startActivity(intent)
             finish()
         }
    }

    companion object {
        private const val TAG = "SplashActivity"
    }
}