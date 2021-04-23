package com.example.soundrecorder.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import com.example.soundrecorder.R
import com.example.soundrecorder.adapters.SummaryOutput3Adapter
import com.example.soundrecorder.databinding.ActivitySummaryOutput3Binding
import com.example.soundrecorder.kotlinclasses.NothingSelectedSpinnerAdapter
import com.example.soundrecorder.models.SumarryOutput3DataClass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class SummaryOutput3Activity : AppCompatActivity(), AdapterView.OnItemSelectedListener  , View.OnClickListener{
    lateinit var binding: ActivitySummaryOutput3Binding
    var list: ArrayList<SumarryOutput3DataClass>? = null
    var pSharedPref: SharedPreferences? = null
    var isSelectedItemExist = false
    private var optionBox10 = ArrayList<String>()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryOutput3Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        var adapter = SummaryOutput3Adapter(this)
        binding.recyclerview.adapter = adapter

        setActionUi()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setActionUi() {
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(resources.getColor(R.color.secondaryLightColor))

        var myPref =  getSharedPreferences("MyOutputs", Context.MODE_PRIVATE)
        var jsHashSetOutput3String = myPref.getString("jsHashSetOutput3String", "jsHashSetOutput3StringNotExist")
        binding.companyNameText.text = pSharedPref?.getString("companyNameString","companyNameStringNotExist").toString().replace("\"", "");
        var gson = Gson()
        var type: Type? = object : TypeToken<HashSet<SumarryOutput3DataClass>>() {}.getType()
        var SumarryOutput3DataClassHashSet: HashSet<SumarryOutput3DataClass> = gson.fromJson(jsHashSetOutput3String, type)
        list  = ArrayList<SumarryOutput3DataClass>(SumarryOutput3DataClassHashSet)

        getAllOptionForOptionBox10()

        binding.backBtn.setOnClickListener(this)
        binding.option10.onItemSelectedListener = this

        var optionBox10Adapter = ArrayAdapter(this, R.layout.spinner_item, optionBox10)
        optionBox10Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.option10.adapter = NothingSelectedSpinnerAdapter(
            optionBox10Adapter,
            R.layout.on_nothing_selection,
            this
        )

        binding.option10.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) { showHighestOutputAccordingToSelectedType() }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun showHighestOutputAccordingToSelectedType() {
        if (binding.option10.selectedItem != null){
            var highestOutput = 0.0
            for (obj in list!!.iterator()){
                if (obj.type.equals(binding.option10.selectedItem.toString()))
                {
                    isSelectedItemExist = true
                    if (highestOutput < obj.output3.toDouble()){
                        highestOutput = obj.output3.toDouble()
                    }
                }
            }
            if (isSelectedItemExist == true)
            {
                binding.highestOutput3ForSelectedType.text = highestOutput.toString()
                binding.selectedType.text = binding.option10.selectedItem.toString()
                isSelectedItemExist = false
            }else{
                binding.highestOutput3ForSelectedType.text = "0.00"
                binding.selectedType.text = "High Value"
            }
        }else{
            binding.highestOutput3ForSelectedType.text = "0.00"
            binding.selectedType.text = "High Value"
        }

    }

    private fun getAllOptionForOptionBox10() {
        if (pSharedPref == null){
            pSharedPref = getSharedPreferences("MyOutputs", Context.MODE_PRIVATE)
        }
        var optionBox6ElementsArrayString =  pSharedPref?.getString("hashSetToHoldOptionBox6Title", "hashSetToHoldOptionBox6TitleNotExist")
        var gson = Gson()
        var type: Type? = object : TypeToken<HashSet<String>>() {}.getType()
        var SumarryOutput1DataClassHashSet: HashSet<String> = gson.fromJson(optionBox6ElementsArrayString, type)
        optionBox10 = ArrayList<String>(SumarryOutput1DataClassHashSet)
    }

    override fun onClick(v: View?) {
         when(v){
             binding.backBtn -> {
                 super.onBackPressed();
             }
         }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}