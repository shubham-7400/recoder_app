package com.example.soundrecorder.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.clabs.caddyapp.network.ApiClient
import com.clabs.caddyapp.network.ApiInterface
import com.example.soundrecorder.R
import com.example.soundrecorder.config.AppPreferences
import com.example.soundrecorder.databinding.ActivitySignupBinding
import com.example.soundrecorder.kotlinclasses.NothingSelectedSpinnerAdapter
import com.example.soundrecorder.models.Country
import com.example.soundrecorder.utils.GlobalOperation
import com.google.gson.Gson
import com.hbb20.CountryCodePicker
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class SignupActivity : AppCompatActivity(), CountryCodePicker.OnCountryChangeListener {
    lateinit var binding: ActivitySignupBinding



    private var phoneNumber: String? = ""
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private var isTermsAndConditionChacked = false
    lateinit var appPreferences: AppPreferences
    var pSharedPref: SharedPreferences? = null
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        val view = binding.root
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(view)

        binding.countryCodePicker!!.setOnCountryChangeListener(this)
        binding.countryCodeText.text = "+"+binding.countryCodePicker.selectedCountryCode.toString()
        appPreferences = AppPreferences()
        appPreferences.init(this)

        binding.btnSignup.setOnClickListener {
            if (GlobalOperation.isNetworkConnected(this)){
                register()
            }else{
                GlobalOperation.showDialog(this,"Please make sure that you are connected with network.")
            }
        }
        binding.signupTextviewToGoLoginForm.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
        binding.termsAndConditon.setOnClickListener {
            if (isTermsAndConditionChacked == true){
                binding.termsAndConditon.setImageResource(R.drawable.checkblnk)
                isTermsAndConditionChacked = false
            }else{
                binding.termsAndConditon.setImageResource(R.drawable.check)
                isTermsAndConditionChacked = true
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        setUiAction()
    }

    private fun setUiAction() {

        binding.signupTextviewToGoLoginForm.setPaintFlags(binding.signupTextviewToGoLoginForm.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        binding.termsAndConditionText.setPaintFlags(binding.termsAndConditionText.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)

        if (pSharedPref == null){
            pSharedPref = getSharedPreferences("MyOutputs", Context.MODE_PRIVATE)
        }
        binding.companyNameText.text = pSharedPref?.getString("companyNameString","companyNameStringNotExist").toString().replace("\"", "");
        Log.i(TAG, "setUiAction: it is "+pSharedPref?.getString("termsAndConditionTitleString","termsAndConditionTitleStringNotExist").toString().replace("\"", ""))
        binding.termsAndConditionText.text = pSharedPref?.getString("termsAndConditionTitleString","termsAndConditionTitleStringNotExist").toString().replace("\"", "").replace("\\u0026","&")
        var termsAndConditonUrlString = pSharedPref?.getString("termsAndConditonUrlString","termsAndConditonUrlStringNotExist").toString().replace("\"", "")
        binding.termsAndConditionText.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra("url", termsAndConditonUrlString )
            startActivity(intent)
        }

    }










    @RequiresApi(Build.VERSION_CODES.M)
    private fun register() {
        phoneNumber =   binding.countryCodeText.text.toString()+""+binding.phoneUser.text.toString()

         if (binding.nameUser.text.isBlank()) {
             GlobalOperation.showDialog(this,"Name can not be empty")
             binding.nameUser.requestFocus()
         }else if (binding.emailUser.text.isBlank() || (!binding.emailUser.text.matches(emailPattern.toRegex()))) {
             if ((!binding.emailUser.text.matches(emailPattern.toRegex())) && (!binding.emailUser.text.isBlank())){
                 GlobalOperation.showDialog(this,"Invalid email pattern")
                 binding.emailUser.requestFocus()
             }else{
                 GlobalOperation.showDialog(this,"Email can not be empty")
                 binding.emailUser.requestFocus()
             }
         }else if (phoneNumber!!.isBlank() || phoneNumber!!.matches("[a-zA-Z]+".toRegex()) || phoneNumber!!.length > 14 || phoneNumber!!.length < 10){
             if (binding.phoneUser.text.isBlank()){
                 GlobalOperation.showDialog(this,"Phone number can not be empty")
                 binding.phoneUser.requestFocus()
             }else{
                 GlobalOperation.showDialog(this, "Invalid mobile number")
                 binding.phoneUser.requestFocus()
             }
         }else if ( binding.passwordUser.text.isBlank() || binding.passwordUser.text.length < 8){
             if (binding.passwordUser.text.isBlank()){
                 GlobalOperation.showDialog(this,"Please enter password")
                 binding.passwordUser.requestFocus()
             }else{
                 GlobalOperation.showDialog(this,"Passsword should be atleast 8 digit")
                 binding.passwordUser.requestFocus()
             }
         }else if ( binding.confirmPasswordUser.text.isBlank()){
             GlobalOperation.showDialog(this,"Please enter confirm password")
             binding.confirmPasswordUser.requestFocus()
         }else if (binding.confirmPasswordUser.text.toString() != binding.passwordUser.text.toString()){
             GlobalOperation.showDialog(this,"Password and Confirm password should be same")
             binding.confirmPasswordUser.requestFocus()
         }else if (isTermsAndConditionChacked == false){
             GlobalOperation.showDialog(this,"Please check first terms and condition")
         }else{
          //   phoneNumber = "+"+phoneNumber
             /*Toast.makeText(this,"phone final phone number "+phoneNumber+" ,length "+ phoneNumber!!.length,Toast.LENGTH_SHORT).show()*/
             getRegister()
         }


    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getRegister() {
        binding.cpCardview.visibility = View.VISIBLE
        val map = HashMap<String,Any>()
        map.put("name",binding.nameUser.text.toString())
        map.put("email",binding.emailUser.text.toString())
        map.put("phoneNumber",phoneNumber.toString())
        map.put("password",binding.passwordUser.text.toString())
//        map.put("clubOrSchoolName",binding.clubOrSchoolEdt.text.toString())
//        map.put("competition",binding.competitionEdt.text.toString())
//        map.put("ageGroup",binding.ageSpinner.selectedItem.toString())
//        map.put("gender",binding.genderSpinner.selectedItem.toString())
//        map.put("country",binding.selectCountry.selectedItem.toString())
//        map.put("state",binding.state.selectedItem.toString())
//        map.put("stateType",binding.stateType.selectedItem.toString())
        map.put("isTermsAccepted",isTermsAndConditionChacked)
        System.out.println("signup_map_is "+map.toString())
        val call = ApiClient().getClient(this)!!.create(ApiInterface::class.java)
        call.userSignup(map).enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                try {
                    if (response.body() == null) {
                        binding.cpCardview.visibility = View.GONE
                        var errorText = response.errorBody()
                        Toast.makeText(this@SignupActivity, "" + errorText, Toast.LENGTH_SHORT)
                            .show()
                        Log.i(TAG, "onResponse: error body text "+errorText)
                    }else{
                        val res = Gson().toJson(response.body())
                        val mainObject = JSONObject(res)
                        Log.d("SIGN_UP_RESPONSE", mainObject.toString())
                        if (mainObject.getBoolean("success")) {
                            Toast.makeText(
                                this@SignupActivity,
                                "user successfully has been registered.",
                                Toast.LENGTH_LONG
                            ).show()
                          //  appPreferences.isLogin = true
                            appPreferences.email = mainObject.getJSONObject("data").optString("email")
                            appPreferences.uuid = mainObject.getJSONObject("data").optString("uuid")
                            appPreferences.token = mainObject.getJSONObject("data").optString("token")
                            binding.cpCardview.visibility = View.GONE
                            val intent = Intent(this@SignupActivity, SignupTwoActivity::class.java)
                            intent.putExtra("user_id",mainObject.getJSONObject("data").optString("uuid"))
                            startActivity(intent)
                        } else {
                            binding.cpCardview.visibility = View.GONE
                            Toast.makeText(
                                this@SignupActivity,
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

//    private fun getSelectedCountryCode(): String {
//        var countryCode = ""
//        for (obj in allCountryObjArrayList) {
//            if (obj.countryName.toString().equals(binding.selectCountry.selectedItem.toString())){
//                countryCode =  obj.shortName.toString()
//                break
//            }
//        }
//        return countryCode
//    }

    companion object {
        private const val TAG = "SignupActivity"
    }

    override fun onCountrySelected() { binding.countryCodeText.text = "+"+""+binding.countryCodePicker.selectedCountryCode.toString()}

}


