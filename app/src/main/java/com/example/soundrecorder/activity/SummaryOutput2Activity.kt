package com.example.soundrecorder.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import com.example.soundrecorder.R
import com.example.soundrecorder.adapters.SummaryOutput2Adapter
import com.example.soundrecorder.databinding.ActivitySummaryOutput1Binding
import com.example.soundrecorder.databinding.ActivitySummaryOutput2Binding
import com.example.soundrecorder.kotlinclasses.NothingSelectedSpinnerAdapter
import com.example.soundrecorder.models.SumarryOutput1DataClass
import com.example.soundrecorder.models.SumarryOutput2DataClass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class SummaryOutput2Activity : AppCompatActivity(), AdapterView.OnItemSelectedListener , View.OnClickListener {
    lateinit var binding: ActivitySummaryOutput2Binding
    var pSharedPref: SharedPreferences? = null
    private var optionBox9 = ArrayList<String>()
    var list: ArrayList<SumarryOutput2DataClass>? = null
    var isSelectedItemExist = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryOutput2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        var adapter = SummaryOutput2Adapter(this)
        binding.recyclerview.adapter = adapter

        setActionUi()
    }

    private fun getAllOptionForOptionBox9() {
        if (pSharedPref == null){
            pSharedPref = getSharedPreferences("MyOutputs", Context.MODE_PRIVATE)
        }
        var optionBox6ElementsArrayString =  pSharedPref?.getString("hashSetToHoldOptionBox6Title", "hashSetToHoldOptionBox6TitleNotExist")
        var gson = Gson()
        var type: Type? = object : TypeToken<HashSet<String>>() {}.getType()
        var SumarryOutput1DataClassHashSet: HashSet<String> = gson.fromJson(optionBox6ElementsArrayString, type)
        optionBox9 = ArrayList<String>(SumarryOutput1DataClassHashSet)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setActionUi() {
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(resources.getColor(R.color.secondaryLightColor))

        var pSharedPref =  getSharedPreferences("MyOutputs", Context.MODE_PRIVATE)
        var jsHashSetOutput2String = pSharedPref.getString("jsHashSetOutput2String", "jsHashSetOutput2StringgNotExist")
        binding.companyNameText.text = pSharedPref?.getString("companyNameString","companyNameStringNotExist").toString().replace("\"", "");
        var gson = Gson()
        var type: Type? = object : TypeToken<HashSet<SumarryOutput2DataClass>>() {}.getType()
        var SumarryOutput2DataClassHashSet: HashSet<SumarryOutput2DataClass> = gson.fromJson(jsHashSetOutput2String, type)
        list  = ArrayList<SumarryOutput2DataClass>(SumarryOutput2DataClassHashSet)

        getAllOptionForOptionBox9()

        binding.backBtn.setOnClickListener(this)
        binding.output2Btn.setOnClickListener(this)
        binding.output4Btn.setOnClickListener(this)
        binding.option9.onItemSelectedListener = this

        var optionBox9Adapter = ArrayAdapter(this, R.layout.spinner_item, optionBox9)
        optionBox9Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.option9.adapter = NothingSelectedSpinnerAdapter(
            optionBox9Adapter,
            R.layout.on_nothing_selection,
            this
        )

        binding.option9.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) { showHighestOutputAccordingToSelectedType() }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun showHighestOutputAccordingToSelectedType() {
        if (binding.option9.selectedItem != null){
            var highestOutput = 0.0
            for (obj in list!!.iterator()){
                if (obj.type.equals(binding.option9.selectedItem.toString()))
                {
                        highestOutput = obj.output2.toDouble()
                }
            }
            for (obj in list!!.iterator()){
                if (obj.type.equals(binding.option9.selectedItem.toString()))
                {
                    isSelectedItemExist = true
                    if (highestOutput < obj.output2.toDouble()){
                        highestOutput = obj.output2.toDouble()
                    }
                }
            }
            if (isSelectedItemExist == true)
            {
                binding.highestOutputForSelectedType.text = highestOutput.toString()
                binding.selectedType.text = binding.option9.selectedItem.toString()
                isSelectedItemExist = false
            }else{
                binding.highestOutputForSelectedType.text = "0.00"
                binding.selectedType.text = "High Value"
            }
        }else{
            binding.highestOutputForSelectedType.text = "0.00"
            binding.selectedType.text = "High Value"
        }
    }

    override fun onBackPressed() {
        super.onBackPressed();
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.backBtn -> {
                super.onBackPressed();
            }

            binding.output2Btn -> {
                var selectedItem = ""
                if (binding.option9.selectedItem == null){
                    selectedItem = ""
                }else{
                    selectedItem = binding.option9.selectedItem.toString()
                }
                var intent = Intent(this, SummaryOutput2DataAnalysis::class.java)
                intent.putExtra("selectedMaterialType", selectedItem )
                startActivity(intent)
            }

            binding.output4Btn -> {
                var intent = Intent(this, SummaryOutput4DataAnalysisActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}
    override fun onNothingSelected(parent: AdapterView<*>?) {}

    companion object {
        private const val TAG = "SummaryOutput2Activity"
    }

}