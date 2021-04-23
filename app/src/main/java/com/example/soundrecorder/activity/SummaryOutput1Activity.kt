package com.example.soundrecorder.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.soundrecorder.R
import com.example.soundrecorder.adapters.SummaryOutput1Adapter
import com.example.soundrecorder.databinding.ActivitySummaryOutput1Binding
import com.example.soundrecorder.models.SumarryOutput1DataClass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.text.DecimalFormat


class SummaryOutput1Activity : AppCompatActivity() , View.OnClickListener{
    lateinit var binding: ActivitySummaryOutput1Binding
    val dec = DecimalFormat("####.0000")
    var pSharedPref: SharedPreferences? = null
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryOutput1Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        var adapter = SummaryOutput1Adapter(this)
        binding.recyclerview.adapter = adapter

        setActionUi()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setActionUi() {
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(resources.getColor(R.color.secondaryLightColor))

        binding.backBtn.setOnClickListener(this)
        binding.summaryOutput1DataAnalysisBtn.setOnClickListener(this)

        if (pSharedPref == null){
            pSharedPref = getSharedPreferences("MyOutputs", Context.MODE_PRIVATE)
        }

        var pSharedPref =  getSharedPreferences("MyOutputs", Context.MODE_PRIVATE)
        var jsHashSetOutput1String = pSharedPref.getString("jsHashSetOutput1String", "jsHashSetOutput1StringNotExist")
        binding.companyNameText.text = pSharedPref?.getString("companyNameString","companyNameStringNotExist").toString().replace("\"", "");
        var gson = Gson()
        var type: Type? = object : TypeToken<HashSet<SumarryOutput1DataClass>>() {}.getType()
        var SumarryOutput1DataClassHashSet: HashSet<SumarryOutput1DataClass> = gson.fromJson(jsHashSetOutput1String, type)
        var list: ArrayList<SumarryOutput1DataClass> = ArrayList<SumarryOutput1DataClass>(SumarryOutput1DataClassHashSet)
        getPercentageOfFrequency(list)
        getPercentageOfOrientation(list)
        getHeighestAndAverageOutput(list)
    }

    fun getPercentageOfOrientation(list: ArrayList<SumarryOutput1DataClass>) {
        var a = 0
        var b = 0
        var c = 0
        var d = 0
        var e = 0
        for (obj in list.iterator()) {
            if (obj.orientation.toString().equals("A")) {
                a++
            }
            if (obj.orientation.toString().equals("B")) {
                b++
            }
            if (obj.orientation.toString().equals("C")) {
                c++
            }
            if (obj.orientation.toString().equals("D")) {
                d++
            }
            if (obj.orientation.toString().equals("E")) {
                e++
            }
        }

        if ((a + b + c + d + e) != 0) {
            var aPercent = ((a * 100) / (a + b + c + d + e))
            var bPercent = ((b * 100) / (a + b + c + d + e))
            var cPercent = ((c * 100) / (a + b + c + d + e))
            var dPercent = ((d * 100) / (a + b + c + d + e))
            var ePercent = ((e * 100) / (a + b + c + d + e))
            binding.aPercent.text = aPercent.toString()
            binding.bPercent.text = bPercent.toString()
            binding.cPercent.text = cPercent.toString()
            binding.dPercent.text = dPercent.toString()
            binding.ePercent.text = ePercent.toString()
        } else {
             binding.aPercent.text = "0.00"
             binding.bPercent.text = "0.00"
             binding.cPercent.text = "0.00"
             binding.dPercent.text = "0.00"
             binding.ePercent.text = "0.00"
        }
    }


    private fun getHeighestAndAverageOutput(list: ArrayList<SumarryOutput1DataClass>) {
        var highestOutput = list[0].output.toDouble()
        var totalOutput = 0.0
        for (obj in list.iterator()) {
            totalOutput = totalOutput + obj.output.toDouble()
            if (highestOutput < obj.output.toDouble()) {
                highestOutput = obj.output.toDouble()
            }
        }
        Log.i(TAG, "setActionUi: total output " + totalOutput)
        binding.summary1HighestOutput.text = dec.format(highestOutput).toString()
        binding.summary1AverageOutput.text = dec.format((totalOutput / list.size)).toString()
    }




     fun getPercentageOfFrequency(list: ArrayList<SumarryOutput1DataClass>) {
        var f = 0
        var g = 0
        var s = 0
        for (obj in list.iterator()) {
            if (obj.frequency.toString().equals("F")) {
                f++
            }
            if (obj.frequency.toString().equals("G")) {
                g++
            }
            if (obj.frequency.toString().equals("S")) {
                s++
            }
        }

        if ((f + g + s) != 0) {
            var fPercent = ((f * 100) / (f + g + s))
            var gPercent = ((g * 100) / (f + g + s))
            var sPercent = ((s * 100) / (f + g + s))
            binding.fPercent.text = fPercent.toString()
            binding.gPercent.text = gPercent.toString()
            binding.sPercent.text = sPercent.toString()
        } else {
            binding.fPercent.text = "0.00"
            binding.gPercent.text = "0.00"
            binding.sPercent.text = "0.00"
        }
    }


    override fun onClick(v: View?) {
        when(v) {
            binding.backBtn -> {
                super.onBackPressed();
            }

            binding.summaryOutput1DataAnalysisBtn -> {
                startActivity(Intent(this, SummaryOutput1DataAnalysis::class.java))
                finish()
            }
        }
    }

    companion object {
        private const val TAG = "SummaryOutput1Activity"
    }
}