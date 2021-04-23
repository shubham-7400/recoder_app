package com.example.soundrecorder.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.clabs.caddyapp.network.ApiClient
import com.clabs.caddyapp.network.ApiInterface
import com.example.soundrecorder.R
import com.example.soundrecorder.config.AppPreferences
import com.example.soundrecorder.databinding.ActivityLoginBinding
import com.example.soundrecorder.utils.GlobalOperation
import com.google.gson.Gson
import com.hbb20.CountryCodePicker
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    var pSharedPref: SharedPreferences? = null

    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    lateinit var appPreferences: AppPreferences
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(view)

        appPreferences = AppPreferences()
        appPreferences.init(this)

        binding.textviewToGoSignupForm.setOnClickListener {
            startActivity(Intent(this,SignupActivity::class.java))
            finish()
        }
        binding.btnLogin.setOnClickListener {
            if (GlobalOperation.isNetworkConnected(this)){
                login()
            }else{
                GlobalOperation.showDialog(this,"Please make sure that you are connected with network.")
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        setUiAction()

        binding.forgotPasswordTv.setOnClickListener {
           showForgotPasswordDialog()
        }
    }

    private fun setUiAction() {
        if (pSharedPref == null){
            pSharedPref = getSharedPreferences("MyOutputs", Context.MODE_PRIVATE)
        }
        var companyName = pSharedPref?.getString("companyNameString", "companyNameStringNotExist").toString().replace(
            "\"",
            ""
        );
        binding.companyNameText.text = companyName
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun login() {
        if (binding.emailUser.text.isBlank() || (!binding.emailUser.text.matches(emailPattern.toRegex()))) {
            if ((!binding.emailUser.text.matches(emailPattern.toRegex())) && (!binding.emailUser.text.isBlank())){
                GlobalOperation.showDialog(this,"Invalid email pattern ")
                binding.emailUser.requestFocus()
            }else{
                GlobalOperation.showDialog(this,"Email can not be empty")
                binding.emailUser.requestFocus()
            }
        }else if ( binding.passwordUser.text.isBlank() || binding.passwordUser.text.length < 8){
            if (binding.passwordUser.text.isBlank()){
                GlobalOperation.showDialog(this,"Please enter password")
                binding.passwordUser.requestFocus()
            }else{
                GlobalOperation.showDialog(this,"Passsword should be atleast 8 digit")
                binding.passwordUser.requestFocus()
            }
        }else{
            getLogin(binding.emailUser.text.toString(), binding.passwordUser.text.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getLogin(email: String, password: String) {
        binding.cpCardview.visibility = View.VISIBLE
        val map = HashMap<String, String>()
        map.put("email", email)
        map.put("password", password)
        val call = ApiClient().getClient(this)!!.create(ApiInterface::class.java)
        call.userLogin(map).enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                try {
                    var errorBody = response.errorBody()
                    if (response.body() == null) {
                        val errorText = errorBody?.string()!!
                        val errorJsonObj = JSONObject(errorText)
                        binding.cpCardview.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, errorJsonObj.getString("message"), Toast.LENGTH_LONG).show()
                    } else {
                        val res = Gson().toJson(response.body())
                        Log.i(TAG, "onResponse: res is " + res)
                        val mainObject = JSONObject(res)
                        Log.d("CREATE_FEED_RESPONSE", mainObject.toString())
                        if (mainObject.getBoolean("success")) {
                            Toast.makeText(
                                this@LoginActivity,
                                "user successfully has been logedIn.",
                                Toast.LENGTH_LONG
                            ).show()
                            appPreferences.isLogin = true
                            appPreferences.email =
                                mainObject.getJSONObject("data").optString("email")
                            appPreferences.uuid = mainObject.getJSONObject("data").optString("uuid")
                            appPreferences.token =
                                mainObject.getJSONObject("data").optString("token")
                            binding.cpCardview.visibility = View.GONE
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            binding.cpCardview.visibility = View.GONE
                            Toast.makeText(
                                this@LoginActivity,
                                mainObject.getString("message"),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    binding.cpCardview.visibility = View.GONE
                    /*Toast.makeText(this@LoginActivity,e.message.toString(),Toast.LENGTH_LONG).show()*/
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

    private fun showForgotPasswordDialog() {
        val dialog = Dialog(this@LoginActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.forgot_password_layout)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        val resetTv = dialog.findViewById(R.id.reset_tv) as TextView
        val emailEt = dialog.findViewById(R.id.email_et) as EditText
        val cancelIv = dialog.findViewById(R.id.cancel_iv) as ImageView

        val cancelTv = dialog.findViewById(R.id.cancel_tv) as TextView

        cancelTv.setOnClickListener { dialog.dismiss() }
        cancelIv.setOnClickListener { dialog.dismiss() }

        resetTv.setOnClickListener {
            if (emailEt.text.isBlank()){
                GlobalOperation.showDialog(this,"Email can not be empty")
                binding.emailUser.requestFocus()
            }else if(!emailEt.text.matches(emailPattern.toRegex())){
                GlobalOperation.showDialog(this,"Invalid email pattern ")
                binding.emailUser.requestFocus()
            }else{
                dialog.dismiss()
               forgotPasswardAPI(emailEt.text.toString())
            }
        }
        dialog.show()

    }



    @RequiresApi(Build.VERSION_CODES.M)
    private fun forgotPasswardAPI(email: String) {
        binding.cpCardview.visibility = View.VISIBLE
        val map = HashMap<String, String>()
        map.put("email", email)
        val call = ApiClient().getClient(this)!!.create(ApiInterface::class.java)
        call.forgotPassword(map).enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                try {
                    binding.cpCardview.visibility = View.GONE
                    var errorBody = response.errorBody()
                    if (response.body() == null) {
                        val errorText = errorBody?.string()!!
                        val errorJsonObj = JSONObject(errorText)
                        GlobalOperation.showDialog(this@LoginActivity,errorJsonObj.getString("message"))
                    } else {
                        val res = Gson().toJson(response.body())
                        Log.i(TAG, "forgotPassword: res is " + res)
                        val mainObject = JSONObject(res)
                        Log.d("CREATE_FEED_RESPONSE", mainObject.toString())
                        if (mainObject.getBoolean("success")) {
                            GlobalOperation.showDialog(this@LoginActivity,mainObject.getString("message"))
                        } else {
                            GlobalOperation.showDialog(this@LoginActivity,mainObject.getString("message"))
                        }
                    }
                } catch (e: Exception) {
                    binding.cpCardview.visibility = View.GONE
                    /*Toast.makeText(this@LoginActivity,e.message.toString(),Toast.LENGTH_LONG).show()*/
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
        private const val TAG = "LoginActivity"
    }
}