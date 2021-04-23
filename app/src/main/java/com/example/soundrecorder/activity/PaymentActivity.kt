package com.example.soundrecorder.activity


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.clabs.caddyapp.network.ApiClient
import com.clabs.caddyapp.network.ApiInterface
import com.example.soundrecorder.R
import com.example.soundrecorder.config.AppPreferences
import com.example.soundrecorder.databinding.ActivityPaymentBinding
import com.google.gson.Gson
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class PaymentActivity : AppCompatActivity() {

    lateinit var binding: ActivityPaymentBinding
    lateinit var appPreferences: AppPreferences
    var pSharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //   setContentView(R.layout.activity_payment)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        appPreferences = AppPreferences()
        appPreferences.init(this)
        pSharedPref = getSharedPreferences("MyOutputs", Context.MODE_PRIVATE)
        binding.btnPay.text = "Pay "+pSharedPref?.getInt("registrationAmount", 0)

        initialize()
    }

    fun initialize() {

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.btnPay.setOnClickListener {
            if (binding.cardInput.text.toString().length == 0) {
                binding.cardInput.setError("Enter card number!")
                binding.cardInput.requestFocus()
                return@setOnClickListener
            } else if (binding.monthEt.text.toString().length == 0) {
                binding.monthEt.setError("Enter month!")
                binding.monthEt.requestFocus()
                return@setOnClickListener
            } else if (binding.yearEt.text.toString().length == 0) {
                binding.yearEt.setError("Enter year!")
                binding.yearEt.requestFocus()
                return@setOnClickListener
            } else if (binding.cvvEt.text.toString().length == 0) {
                binding.cvvEt.setError("Enter cvv!")
                binding.cvvEt.requestFocus()
                return@setOnClickListener
            } else{
                val cardNumber: String = binding.cardInput.text.toString()
                val cvvNumber: String = binding.cvvEt.text.toString()
                val expMonth: Int = Integer.parseInt(binding.monthEt.text.toString())
                val expYear: Int = Integer.parseInt(binding.yearEt.text.toString())
                validateCard(cardNumber,cvvNumber,expMonth,expYear)
            }
        }

    }


    private fun validateCard(cardNumber:String,cvvNumber:String,expMonth:Int,expYear:Int) {
        binding.cpCardview.visibility = View.VISIBLE
        val card: Card = Card.create(cardNumber, expMonth, expYear, cvvNumber)
        val stripe = Stripe(this, getString(R.string.stripe_key))
        stripe.createCardToken(
            card,
            callback = object : ApiResultCallback<Token> {
                override fun onError(e: Exception) {
                    binding.cpCardview.visibility = View.GONE
                    Toast.makeText(this@PaymentActivity,"Some thing went wrong",Toast.LENGTH_LONG).show()
                    System.out.println("MY_TOKEN_IS error " + e.toString())
                }

                override fun onSuccess(result: Token) {
                    System.out.println("MY_TOKEN_IS result " + result.id)
                    sendPayment(result.id)
                }
            }
        )
    }




    @RequiresApi(Build.VERSION_CODES.M)
    private fun sendPayment(stripeToken: String) {
        binding.cpCardview.visibility = View.VISIBLE
        val map = HashMap<String,Any>()
        map.put("userId",appPreferences.uuid)
        map.put("amount",100)
        map.put("serviceToken",stripeToken)
        System.out.println("payment_map_is "+map.toString())
        val call = ApiClient().getClient(this)!!.create(ApiInterface::class.java)
        call.sendPayment(map,appPreferences.token).enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                try {
                    if (response.body() == null) {
                        binding.cpCardview.visibility = View.GONE
                        var errorText = response.errorBody()
                        Toast.makeText(this@PaymentActivity, "" + errorText, Toast.LENGTH_SHORT)
                            .show()
                    }else{
                        val res = Gson().toJson(response.body())
                        val mainObject = JSONObject(res)
                        Log.d("Payment_RESPONSE", mainObject.toString())
                        if (mainObject.getBoolean("success")) {
                            Toast.makeText(
                                this@PaymentActivity,
                                "Payment Successful.",
                                Toast.LENGTH_LONG
                            ).show()
                              appPreferences.isLogin = true
                            appPreferences.uuid = mainObject.getJSONObject("data").optString("userId")
                            binding.cpCardview.visibility = View.GONE
                            val intent = Intent(this@PaymentActivity, HomeActivity::class.java)
                       //     intent.putExtra("user_id",mainObject.getJSONObject("data").optString("uuid"))
                            startActivity(intent)
                            finish()
                        } else {
                            binding.cpCardview.visibility = View.GONE
                            Toast.makeText(
                                this@PaymentActivity,
                                "problem is " + mainObject.getString("message"),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    binding.cpCardview.visibility = View.GONE
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

}