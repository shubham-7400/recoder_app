package com.example.soundrecorder.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.clabs.caddyapp.network.ApiClient
import com.clabs.caddyapp.network.ApiInterface
import com.example.soundrecorder.R
import com.example.soundrecorder.config.AppPreferences
import com.example.soundrecorder.databinding.ActivityHomeBinding
import com.example.soundrecorder.kotlinclasses.NothingSelectedSpinnerAdapter
import com.example.soundrecorder.models.*
import com.example.soundrecorder.utils.GlobalOperation
import com.example.soundrecorder.viewmodels.HomeViewModel
import com.example.soundrecorder.viewmodels.HomeViewModelFactory
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet


class HomeActivity : AppCompatActivity(){
    lateinit var binding: ActivityHomeBinding
    lateinit var appPreferences: AppPreferences
    var hashSetToHoldOptionBox6Obj: HashSet<OptionBox6DataClass>? = null
    var pSharedPref: SharedPreferences? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        appPreferences = AppPreferences()
        appPreferences.init(this)

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(resources.getColor(R.color.secondaryLightColor))

        val viewModelFactory = HomeViewModelFactory(this)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)


        pSharedPref = getSharedPreferences("MyOutputs", Context.MODE_PRIVATE)

        if (GlobalOperation.isNetworkConnected(this)){
            viewModel.setUiAction()
        }else{
            GlobalOperation.showDialog(
                this,
                "Please make sure that you are connected with network."
            )
        }

        viewModel.onOptionId2OptionTitleChange.observe(this, androidx.lifecycle.Observer {
            viewModel.changeOptionId1DataInSpinner()
        })
        if (hashSetToHoldOptionBox6Obj == null){
            hashSetToHoldOptionBox6Obj = HashSet<OptionBox6DataClass>()
        }

        binding.btnGo.setOnClickListener { goToStartRecording() }
        binding.imageViewBtnLogout.setOnClickListener { logout() }
        binding.btnViewClub.setOnClickListener {
            val intent = Intent(this,ClubStatiscsActivity::class.java)
            startActivity(intent)
        }
    }




    private fun logout() {
        val dialog = Dialog(this!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.alert_dialog_for_yes_and_no)
        val text = dialog.findViewById<TextView>(R.id.cp_title) as TextView
        text.text = "do you want ot logout?"
        val dialogButtonOk: Button = dialog.findViewById<Button>(R.id.alert_ok_btn)
        dialogButtonOk.setOnClickListener  {
            appPreferences.email = ""
            appPreferences.token = ""
            appPreferences.uuid = ""
            appPreferences.isLogin = false
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
            dialog.dismiss()
        }
        val dialogButtonNo: Button = dialog.findViewById<Button>(R.id.alert_no_btn)
        dialogButtonNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun goToStartRecording() {
         if (binding.option1.selectedItem == null) {
             GlobalOperation.showDialog(this, "Please select option 1 ")
         }else if (binding.option2.selectedItem  == null){
             GlobalOperation.showDialog(this, "Please select option 2 ")
         }else if (binding.option3.selectedItem  ==  null){
             GlobalOperation.showDialog(this, "Please select option 3 ")
         }else if (binding.option4.selectedItem  == null){
             GlobalOperation.showDialog(this, "Please select option 4 ")
         }else if (binding.option5.selectedItem  == null){
             GlobalOperation.showDialog(this, "Please select option 5 ")
         }else{
             val intent = Intent(this, StartActivity::class.java)
             intent.putExtra("OptionId1SelectedTitle", binding.option1.selectedItem.toString())
             intent.putExtra("OptionId2SelectedTitle", binding.option2.selectedItem.toString())
             intent.putExtra("OptionId3SelectedTitle", binding.option3.selectedItem.toString())
             intent.putExtra("OptionId4SelectedTitle", binding.option4.selectedItem.toString())
             intent.putExtra("OptionId5SelectedTitle", binding.option5.selectedItem.toString())
             startActivity(intent)
         }
    }

    companion object {
        private const val TAG = "HomeActivity"
    }
}