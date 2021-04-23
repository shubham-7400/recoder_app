package com.example.soundrecorder.activity

import android.Manifest
import android.R.id.button1
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.*
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.clabs.caddyapp.network.ApiClient
import com.clabs.caddyapp.network.ApiInterface
import com.example.soundrecorder.R
import com.example.soundrecorder.config.AppPreferences
import com.example.soundrecorder.databinding.ActivityStartBinding
import com.example.soundrecorder.javacode.JavaOperations
import com.example.soundrecorder.kotlinclasses.NothingSelectedSpinnerAdapter
import com.example.soundrecorder.models.*
import com.example.soundrecorder.utils.GlobalOperation
import com.example.soundrecorder.viewmodels.StartViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.*
import java.lang.reflect.Type
import java.text.DecimalFormat
import kotlin.properties.Delegates


class  StartActivity : AppCompatActivity()  , View.OnClickListener{
    lateinit var binding: ActivityStartBinding
    lateinit var viewModel: StartViewModel

    var minDB: Double? = null
    var maxDB: Double? = null
    var minFrequency: Double? = null
    var maxFrequency: Double? = null
    var durationBetweenTwoSound: Double? = null

    private val RECORDER_SAMPLERATE = 8000
    private val RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO
    private val RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT
    private var recorder: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var isRecording = false
    private var permissionToRecordAccepted = false
    private val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    var filePath: String? = null
    var isRecordingStart: Boolean = false
    var javaOperations = JavaOperations()

    var milliseconds by Delegates.notNull<Long>();
    var option1 by Delegates.notNull<Int>()
    var option2 by Delegates.notNull<Double>()
    var option3 by Delegates.notNull<Double>()
    var option4 by Delegates.notNull<Int>()
    var option5 by Delegates.notNull<Double>()


    private var doNotGoInFirst = false
    private var doNotGoInSecond = true
    private var i = 0
    private var j = 0
    private var time: Double = 0.0
    private var firstSoundEndTime: Double = 0.0
    private var secondSoundStartTime: Double = 0.0
    private var mapSound1 = HashMap<Double, Double>()
    private var mapSound2 = HashMap<Double, Double>()
    var output1 = 0.0
    var output2 = 0.0
    var output3 = 0.0
    var output4 = 0.0
    val dec = DecimalFormat("####.0000")

    var pSharedPref: SharedPreferences? = null

    var gson: Gson? = null

    var outputs1 = HashSet<SumarryOutput1DataClass>()
    var outputs2 = HashSet<SumarryOutput2DataClass>()
    var outputs3 = HashSet<SumarryOutput3DataClass>()
    lateinit var appPreferences: AppPreferences
    private var optionBox6 = ArrayList<String>()
    private lateinit var optionBox6Adapter: ArrayAdapter<String>
    var arrayListToHoldOptionBox6Obj: ArrayList<OptionBox6DataClass>? = null
    var isAnySoundRecorded = false
    var isAutoStart = false


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        pSharedPref = getSharedPreferences("MyOutputs", Context.MODE_PRIVATE)

        if (GlobalOperation.isNetworkConnected(this)){
            setUiAction()
        }else{
            GlobalOperation.showDialog(
                this,
                "Please make sure that you are connected with network."
            )
        }

        viewModel = ViewModelProvider(this).get(StartViewModel::class.java)

        setAllOptionsValue()
//        binding.companyNameText.text = pSharedPref?.getString(
//            "companyNameString",
//            "companyNameStringNotExist"
//        ).toString().replace("\"", "");
        milliseconds = (option4 * 1000).toLong();
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        setUiAction()
        binding.backBtn.setOnClickListener(this)
        binding.switchBtnYesAndNo.setOnClickListener(this)
        binding.recoderStartAndStop.setOnClickListener(this)
        binding.summary1.setOnClickListener(this)
        binding.summary2.setOnClickListener(this)
        binding.summary3.setOnClickListener(this)
        binding.btnUploadData.setOnClickListener(this)
        binding.summary4.visibility = View.INVISIBLE
        binding.manualTv.setOnClickListener(this)
        binding.autoTv.setOnClickListener(this)

        if (savedInstanceState != null)
        {
            setAllDataAfterConfigurationChang()
        }
    }


    private fun setAllDataAfterConfigurationChang() {
        outputs1 = viewModel.getOutputs1Data()
        outputs2 = viewModel.getOutputs2Data()
        outputs3 = viewModel.getOutputs3Data()
        output1 = viewModel.getOutput1()
        output2 = viewModel.getOutput2()
        output3 = viewModel.getOutput3()
        output4 = viewModel.getOutput4()
        binding.allSummaryScrollview.visibility = View.VISIBLE
        binding.output1DbTextview.text = output1.toString()
        binding.output2DbTextview.text = output2.toString()
        binding.output3DbTextview.text = output3.toString()
        binding.output4DbTextview.text = output4.toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("isConfigurationChanged", "true");
        viewModel.saveAllOutputsArrayInViewModel(outputs1, outputs2, outputs3)
        viewModel.setAllCurrentOutput(
            dec.format(output1).toDouble(), dec.format(output2).toDouble(), dec.format(
                output3
            ).toDouble()
        )
        super.onSaveInstanceState(outState)
    }

    private fun setAllOptionsValue() {
        var optionId1DataClassObjString = pSharedPref?.getString("optionId1DataClassObjString", "optionId1DataClassObjStringNotExist")
        var optionId1DataClassObj: OptionId1DataClass = Gson().fromJson(optionId1DataClassObjString, object : TypeToken<OptionId1DataClass>() {}.getType())
        var optionId1OptionObjHashSet = optionId1DataClassObj.hashSetOfOption
        var optionId1MeterObjHashSet = optionId1DataClassObj.hashSetOfMeter
        var optionId1YardObjHashSet = optionId1DataClassObj.hashSetOfYard
        for (obj in optionId1OptionObjHashSet.iterator()){
            if (obj.title.toString().equals(intent.getStringExtra("OptionId1SelectedTitle"))) {
                option1 = obj.value.toInt()
                break
            }
        }
        for (obj in optionId1MeterObjHashSet.iterator()){
            if (obj.title.toString().equals(intent.getStringExtra("OptionId1SelectedTitle"))) {
                option1 = obj.value.toInt()
                break
            }
        }
        for (obj in optionId1YardObjHashSet.iterator()){
            if (obj.title.toString().equals(intent.getStringExtra("OptionId1SelectedTitle"))) {
                option1 = obj.value.toInt()
                break
            }
        }


        var optionId2DataClassObjString = pSharedPref?.getString("optionId2DataClassObjString", "optionId2DataClassObjStringNotExist")
        var optionId2DataClassObj: OptionId2DataClass = Gson()!!.fromJson(optionId2DataClassObjString, object : TypeToken<OptionId2DataClass>() {}.getType())
        var optionId2OptionObjHashSet = optionId2DataClassObj.hashSetOfOption
        for (obj in optionId2OptionObjHashSet.iterator()) {
            if (obj.title.toString().equals(intent.getStringExtra("OptionId2SelectedTitle"))) {
                option2 = obj.value.toDouble()
                break
            }
        }

        var OptionId3DataClassObjString = pSharedPref?.getString("OptionId3DataClassObjString", "OptionId3DataClassObjStringNotExist")
        var optionId3DataClassObj: OptionId3DataClass = Gson()!!.fromJson(OptionId3DataClassObjString, object : TypeToken<OptionId3DataClass>() {}.getType())
        var optionId3OptionObjHashSet = optionId3DataClassObj.hashSetOfOption
        for (obj in optionId3OptionObjHashSet.iterator()) {
            if (obj.title.toString().equals(intent.getStringExtra("OptionId3SelectedTitle"))) {
                option3 = obj.value.toDouble()
                break
            }
        }

        var OptionId4DataClassObjString = pSharedPref?.getString("OptionId4DataClassObjString", "OptionId4DataClassObjStringNotExist")
        var OptionId4DataClassObj: OptionId4DataClass = Gson()!!.fromJson(OptionId4DataClassObjString, object : TypeToken<OptionId4DataClass>() {}.getType())
        var optionId4OptionObjHashSet = OptionId4DataClassObj.hashSetOfOption
        for (obj in optionId4OptionObjHashSet.iterator()) {
            if (obj.title.toString().equals(intent.getStringExtra("OptionId4SelectedTitle"))) {
                option4 = obj.value.toInt()
                break
            }
        }

        var OptionId5DataClassObjString = pSharedPref?.getString("OptionId5DataClassObjString", "OptionId5DataClassObjStringNotExist")
        var OptionId5DataClassObj: OptionId5DataClass = Gson()!!.fromJson(OptionId5DataClassObjString, object : TypeToken<OptionId5DataClass>() {}.getType())
        var optionId5OptionObjHashSet = OptionId5DataClassObj.hashSetOfOption
        for (obj in optionId5OptionObjHashSet.iterator()) {
            if (obj.title.toString().equals(intent.getStringExtra("OptionId5SelectedTitle"))) {
                option5 = obj.value.toDouble()
                break
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_RECORD_AUDIO_PERMISSION -> {
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //resume tasks needing this permission
                    Log.i(TAG, "onRequestPermissionsResult: permision  granted")
                } else {
                    Log.i(TAG, "onRequestPermissionsResult: permision not granted")
                }
            }
        }
        if(!permissionToRecordAccepted){ finish() }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setUiAction() {
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(resources.getColor(R.color.secondaryLightColor))


        getHasSetToCheckCondtionOfAudio()

        if (isAnySoundRecorded == false){
            binding.allSummaryScrollview.visibility = View.GONE
        }else{
            binding.allSummaryScrollview.visibility = View.VISIBLE
        }
        binding.option6.isEnabled = false
        binding.option6.isClickable = false
     //   binding.switchBtnYesAndNo.isChecked = true
        appPreferences = AppPreferences()
        appPreferences.init(this)
        setOption6InSpinner()
        getAllOptionForOptionBox6()
        binding.option6.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0)
                    binding.optionId6OptionHeading.visibility = View.GONE
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
    }

    private fun getHasSetToCheckCondtionOfAudio() {
        maxDB = pSharedPref?.getString("maxDB", 0.0.toString())?.toDouble()
        minDB  = pSharedPref?.getString("minDB", 0.0.toString())?.toDouble()
        minFrequency = pSharedPref?.getString("minFrequency", 0.0.toString())?.toDouble()
        maxFrequency = pSharedPref?.getString("maxFrequency", 0.0.toString())?.toDouble()
        durationBetweenTwoSound = pSharedPref?.getString("durationBetweenTwoSound", 0.0.toString())?.toDouble()
    }

    private fun setOption6InSpinner() {
        optionBox6Adapter = ArrayAdapter(this, R.layout.spinner_item, optionBox6)
        optionBox6Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.option6.adapter = NothingSelectedSpinnerAdapter(optionBox6Adapter, R.layout.on_nothing_selection, this)
    }

    private fun getAllOptionForOptionBox6() {
        var optionId6DataClassObjString = pSharedPref?.getString("optionId6DataClassObjString", "optionId6DataClassObjStringNotExist")
        var type: Type? = object : TypeToken<OptionId6DataClass>() {}.getType()
        var optionId6DataClassObj:OptionId6DataClass = Gson().fromJson(optionId6DataClassObjString, type)
        binding.optionId6OptionHeading.text = optionId6DataClassObj.optionHeading
        binding.optionId6OptionHeading.visibility = View.VISIBLE
        System.out.println("SESSSION_DATA optionId6DataClassObjString "+optionId6DataClassObj.hashSetOfOption)
        var optionId6OptionHashSet = optionId6DataClassObj.hashSetOfOption

        for (obj in optionId6OptionHashSet!!.iterator())
        {
            var str = ""
            str = str+""+obj.title
            str = str+" - factor1 = "+obj.factor1
            str = str+" -" + ""  + " factor2 = "+obj.factor2
            optionBox6.add(str)
        }
        optionBox6Adapter.notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View?) {
        var timeDurationInSecond = option4
        when (v){
            binding.recoderStartAndStop -> {
                if (isRecordingStart == false) {
                    binding.recoderStartAndStop.setImageResource(R.drawable.stop)
                    isRecordingStart = true
                    startRecording();
                    object : CountDownTimer(milliseconds, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            binding.textviewStartAndTimer.text =
                                timeDurationInSecond.toString() + " sec."
                            timeDurationInSecond = --timeDurationInSecond
                            if (isRecordingStart == false) {
                                cancel()
                                binding.textviewStartAndTimer.text = "Start"
                            }
                        }

                        override fun onFinish() {
                            if (isRecordingStart == true) {
                                binding.textviewStartAndTimer.text = "Start"
                                binding.recoderStartAndStop.setImageResource(R.drawable.start)
                                isRecordingStart = false
                                stopRecording()
                                if (isAutoStart == true && isRecordingStart == false) {
                                    Handler().postDelayed(Runnable { binding.recoderStartAndStop.performClick() }, 2000)
                                }
                            }
                        }
                    }.start()
                } else {
                    binding.textviewStartAndTimer.text = "Start"
                    binding.recoderStartAndStop.setImageResource(R.drawable.start)
                    isRecordingStart = false
                    stopRecording()
                }
            }

            binding.backBtn -> {
                GlobalOperation.showYesAndNoDialog(this, "Do you want to end the session?")
            }

            binding.summary1 -> {
                startActivity(Intent(this, SummaryOutput1Activity::class.java))
            }

            binding.summary2 -> {
                startActivity(Intent(this, SummaryOutput2Activity::class.java))
            }

            binding.summary3 -> {
                startActivity(Intent(this, SummaryOutput3Activity::class.java))
            }

            binding.switchBtnYesAndNo -> {
                if (binding.switchBtnYesAndNo.isChecked == true) {
                    binding.summary3View.visibility = View.VISIBLE
                } else {
                    binding.summary3View.visibility = View.GONE
                }
            }

            binding.btnUploadData -> {
                startActivity(Intent(this, UploadDataActivity::class.java))
            }
            binding.autoTv -> {
                isAutoStart = true
                val  sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    binding.autoTv.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.start_button_back) )
                    binding.manualTv.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.start_blank_back) )
                } else {
                    binding.autoTv.setBackground(ContextCompat.getDrawable(this, R.drawable.start_button_back))
                    binding.manualTv.setBackground(ContextCompat.getDrawable(this, R.drawable.start_blank_back))
                }
            }
            binding.manualTv -> {
                isAutoStart = false
                val  sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    binding.manualTv.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.start_button_back) )
                    binding.autoTv.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.start_blank_back) )
                } else {
                    binding.manualTv.setBackground(ContextCompat.getDrawable(this, R.drawable.start_button_back))
                    binding.autoTv.setBackground(ContextCompat.getDrawable(this, R.drawable.start_blank_back))
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun stopRecording() {
        isRecording = false
        recorder!!.stop()
        recorder!!.release()
        recorder = null
        recordingThread = null
        Log.i(TAG, "stopRecording: ababab")
        binding.option6.isEnabled = false
        binding.option6.isClickable = false
        var totalDecibelSound1: Double = 0.0
        var averageDecibelforsound1 = 0.0
        var totalDecibelSound2: Double = 0.0
        var averageDecibelForSound2 = 0.0
        var timeDifferenceBetweenTwoSound = 0.0
        for (dbAsKey in mapSound1.keys)
        {
            totalDecibelSound1 = totalDecibelSound1 + dbAsKey
        }

        averageDecibelforsound1 = (totalDecibelSound1 / mapSound1.size)
        System.out.println("RECORDING_MAP totalDecibelSound1 "+totalDecibelSound1)
        System.out.println("RECORDING_MAP mapSound1.size "+mapSound1.size)
        System.out.println("RECORDING_MAP averageDecibelforsound1 "+averageDecibelforsound1)

        for (dbAsKey in mapSound2.keys)
        {
            totalDecibelSound2 = totalDecibelSound2 + dbAsKey
        }
        averageDecibelForSound2 = (totalDecibelSound2 / (mapSound2.size))
        System.out.println("RECORDING_MAP totalDecibelSound2 "+totalDecibelSound2)
        System.out.println("RECORDING_MAP mapSound2.size "+mapSound2.size)
        System.out.println("RECORDING_MAP averageDecibelForSound2 "+averageDecibelForSound2)

        timeDifferenceBetweenTwoSound = secondSoundStartTime - firstSoundEndTime
        if (timeDifferenceBetweenTwoSound > durationBetweenTwoSound!!){
            averageDecibelForSound2 = 0.0
        }
        if (averageDecibelforsound1 != 0.0){
            callApiForCalculation(averageDecibelforsound1, averageDecibelForSound2)
        }else{
            mapSound1 = HashMap<Double, Double>()
            mapSound2 = HashMap<Double, Double>()
            firstSoundEndTime = 0.0
            secondSoundStartTime = 0.0
            i = 0
            j = 0
            milliseconds = (option4 * 1000).toLong();
            doNotGoInFirst = false
            doNotGoInSecond = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun callApiForCalculation(averageDecibelforsound1: Double, averageDecibelForSound2: Double) {
        binding.cpCardview.visibility = View.VISIBLE
        val map = HashMap<String, Double>()
       // Log.i(TAG, "callApiForCalculation: sseer option1 "+option1+" optoin2 "+option2+" , option3 "+option3+" , option5 "+option5+" , avedb1 "+averageDecibelforsound1+" ,aedb2"+averageDecibelForSound2)
        map.put("option1", option1.toDouble())
        map.put("option2", option2.toDouble())
        map.put("option3", option3.toDouble())
        map.put("option5", option5.toDouble())
        map.put("avgDB1", averageDecibelforsound1.toDouble()) //averageDecibelforsound1.toDouble()
        map.put("avgDB2", averageDecibelForSound2.toDouble())  //averageDecibelForSound2.toDouble()
        System.out.println("RECORDING_MAP "+map.toString())
        System.out.println("RECORDING_MAP token "+appPreferences.token)


        val call = ApiClient().getClient(this)!!.create(ApiInterface::class.java)
        call.getCalculation(map, appPreferences.token).enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                try {
                    var errorBody = response.errorBody()
                    if (response.body() == null) {
                        val errorText = errorBody?.string()!!
                        val errorJsonObj = JSONObject(errorText)
                        binding.cpCardview.visibility = View.GONE
                        Toast.makeText(
                            this@StartActivity,
                            errorJsonObj.getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val res = Gson().toJson(response.body())
                        Log.i(TAG, "onResponse: res is " + res)
                        val mainObject = JSONObject(res)
                        Log.d("GET_CALCULATION_RESPONS", mainObject.toString())
                        if (mainObject.getBoolean("success")) {
                            binding.cpCardview.visibility = View.GONE
                            var arrayOfOutputs = mainObject.getJSONArray("data")
                            setAllOutputs(
                                arrayOfOutputs.get(0).toString().toDouble(),
                                arrayOfOutputs.get(
                                    1
                                ).toString().toDouble(),
                                arrayOfOutputs.get(2).toString().toDouble(),
                                arrayOfOutputs.get(
                                    3
                                ).toString().toDouble()
                            )
                        } else {
                            binding.cpCardview.visibility = View.GONE
                            Toast.makeText(
                                this@StartActivity,
                                mainObject.getString("message"),
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

    private fun setAllOutputs(output1: Double, output2: Double, output3: Double, output4: Double) {
//        if (null != recorder) {
//            isRecording = false
//            recorder!!.stop()
//            recorder!!.release()
//            recorder = null
//            recordingThread = null

            setOutput(
                dec.format(output1), dec.format(output2), dec.format(output3), dec.format(
                    output4
                )
            )
        System.out.println("DATA_COME 1")
            if (arrayListToHoldOptionBox6Obj == null) {
                arrayListToHoldOptionBox6Obj = ArrayList<OptionBox6DataClass>()
            }
        System.out.println("DATA_COME 2")

        if (arrayListToHoldOptionBox6Obj != null) {
            System.out.println("DATA_COME 3")

            var hashSetToHoldOptionBox6ObjString = pSharedPref?.getString(
                    "hashSetToHoldOptionBox6ObjJsonString",
                    "hashSetToHoldOptionBox6ObjJsonStringNotExist"
                )
                var gson = Gson()
                var type: Type? = object : TypeToken<HashSet<OptionBox6DataClass>>() {}.getType()
                var hashSetToHoldOptionBox6Obj: HashSet<OptionBox6DataClass> =
                    gson.fromJson(hashSetToHoldOptionBox6ObjString, type)
                arrayListToHoldOptionBox6Obj =
                    ArrayList<OptionBox6DataClass>(hashSetToHoldOptionBox6Obj)
            }
            if (binding.option6.selectedItem != null) {
                System.out.println("DATA_COME 4")

                for (option in arrayListToHoldOptionBox6Obj!!.iterator()) {
                    System.out.println("DATA_COME 5")

                    if (binding.option6.selectedItem.toString().contains(option.title)) {
                        var SumarryOutput1DataClassObj = SumarryOutput1DataClass(
                            dec.format(output1).toString(),
                            option.factor1.toString(),
                            option.factor2.toString()
                        )
                        System.out.println("DATA_COME 6")

                        outputs1?.add(SumarryOutput1DataClassObj)
                    }
                }
            } else {
                var SumarryOutput1DataClassObj =
                    SumarryOutput1DataClass(dec.format(output1).toString(), "--", "--")
                outputs1?.add(SumarryOutput1DataClassObj)
            }
            if (binding.option6.selectedItem != null) {
                for (option in arrayListToHoldOptionBox6Obj!!.iterator()) {
                    if (binding.option6.selectedItem.toString().contains(option.title)) {
                        var SumarryOutput2DataClassObj = SumarryOutput2DataClass(
                            dec.format(output2).toString(),
                            option.title,
                            dec.format(output4).toString()
                        )
                        outputs2?.add(SumarryOutput2DataClassObj)
                    }
                }
            } else {
                var SumarryOutput2DataClassObj = SumarryOutput2DataClass(
                    dec.format(output2).toString(),
                    "--",
                    dec.format(output4).toString()
                )
                outputs2?.add(SumarryOutput2DataClassObj)
            }

            if (binding.option6.selectedItem != null) {
                for (option in arrayListToHoldOptionBox6Obj!!.iterator()) {
                    if (binding.option6.selectedItem.toString().contains(option.title)) {
                        var SumarryOutput3DataClassObj =
                            SumarryOutput3DataClass(dec.format(output3).toString(), option.title)
                        outputs3?.add(SumarryOutput3DataClassObj)
                    }
                }
            } else {
                var SumarryOutput3DataClassObj =
                    SumarryOutput3DataClass(dec.format(output3).toString(), "--")
                outputs3?.add(SumarryOutput3DataClassObj)
            }

            setOutputsResultToSharedPref()
       // }
    }



    private fun setOutputsResultToSharedPref() {
        if (pSharedPref == null){
            pSharedPref = getSharedPreferences("MyOutputs", Context.MODE_PRIVATE)
        }
        if (gson == null) {
            gson = Gson()
        }


        var jsHashSetOutput1String = gson!!.toJson(outputs1)
        Log.i(TAG, "stopRecording: string is " + jsHashSetOutput1String)
        var jsHashSetOutput2String = gson!!.toJson(outputs2)
        var jsHashSetOutput3String = gson!!.toJson(outputs3)
        pSharedPref!!.edit().putString("jsHashSetOutput1String", jsHashSetOutput1String).apply()
        pSharedPref!!.edit().putString("jsHashSetOutput2String", jsHashSetOutput2String).apply()
        pSharedPref!!.edit().putString("jsHashSetOutput3String", jsHashSetOutput3String).apply()

        binding.option6.adapter = NothingSelectedSpinnerAdapter(
            optionBox6Adapter,
            R.layout.on_nothing_selection,
            applicationContext
        )
        optionBox6Adapter.notifyDataSetChanged()
    }

    private fun getDisplayOutputsHashMap(): HashMap<String, String> {
        if (pSharedPref == null) {
            pSharedPref = getSharedPreferences("MyOutputs", MODE_PRIVATE)
        }
        var hashSetDisplayOutputsJsonString = pSharedPref?.getString(
            "hashSetDisplayOutputsJsonString",
            "hashSetDisplayOutputsJsonStringNotExist"
        )
        var gson = Gson()
        var type: Type? = object : TypeToken<HashMap<String, String>>() {}.getType()
        var hashSetDisplayOutputs: HashMap<String, String> = gson.fromJson(
            hashSetDisplayOutputsJsonString,
            type
        )
        return hashSetDisplayOutputs
    }

    private fun setOutput(
        output1_db: String,
        output2_db: String,
        output3_db: String,
        output4_db: String
    ) {
        binding.output1DbTextview.text = output1_db
        binding.output2DbTextview.text = output2_db
        binding.output3DbTextview.text = output3_db
        binding.output4DbTextview.text = output4_db
        binding.allSummaryScrollview.visibility = View.VISIBLE
        mapSound1 = HashMap<Double, Double>()
        mapSound2 = HashMap<Double, Double>()
        firstSoundEndTime = 0.0
        secondSoundStartTime = 0.0
        i = 0
        j = 0
        milliseconds = (option4 * 1000).toLong();
        doNotGoInFirst = false
        doNotGoInSecond = true
        Log.i(TAG, "setOutput: comming ")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBackPressed() {
        GlobalOperation.showYesAndNoDialog(this, "Do you really want to reset session?")
    }


    var BufferElements2Rec = 1024 // want to play 2048 (2K) since 2 bytes we use only 1024

    var BytesPerElement = 2 // 2 bytes in 16bit format

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startRecording() {
        binding.option6.isEnabled = true
        binding.option6.isClickable = true
        binding.allSummaryScrollview.visibility = View.GONE
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            RECORDER_SAMPLERATE,
            RECORDER_CHANNELS,
            RECORDER_AUDIO_ENCODING,
            BufferElements2Rec * BytesPerElement
        )
        recorder!!.startRecording()
        isRecording = true
        recordingThread = Thread({ writeAudioDataToFile() }, "AudioRecorder Thread")
        recordingThread!!.start()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun writeAudioDataToFile() {
        // Write the output audio in byte
        /*String filePath = "/sdcard/voice8K16bitmono.pcm";*/
        filePath = externalCacheDir!!.absolutePath
        filePath += "/audiorecordtest.3gp"
        val sData = ShortArray(BufferElements2Rec)
        var os: FileOutputStream? = null
        try {
            os = FileOutputStream(filePath)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        while (isRecording) {
            // gets the voice output from microphone to byte format
            recorder!!.read(sData, 0, BufferElements2Rec)
            Log.i(TAG, "writeAudioDataToFile: sData $sData")
            Log.i(TAG, "writeAudioDataToFile: output Stream $os")
            Log.i(TAG, "writeAudioDataToFile: filepath $filePath")
            println("Short writing to file$sData")
            try {
                Log.i(
                    TAG, "writeAudioDataToFile: output is " + calculate(
                        RECORDER_SAMPLERATE,
                        sData
                    )
                )
                Log.i(TAG, "writeAudioDataToFile: output data size " + sData.size)
                val bData: ByteArray = javaOperations.short2byte(sData)
                os!!.write(bData, 0, BufferElements2Rec * BytesPerElement)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        try {
            os!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun calculate(sampleRate: Int, audioData: ShortArray): Int {

        /* i++;
        Log.i(TAG, "calculate: i "+i);*/
        val numSamples = audioData.size
        var numCrossing = 0
        for (p in 0 until numSamples - 1) {
            //Log.i(TAG, "calculate: sund 1ww "+audioData[p])
            /*Log.i(TAG, "calculate: amplitude ar "+audioData[p])*/
            if (audioData[p] > 0 && audioData[p + 1] <= 0 || audioData[p] < 0 && audioData[p + 1] >= 0) {
                numCrossing++
            }
        }

        val numSecondsRecorded = numSamples.toFloat() / sampleRate.toFloat()
        Log.i(TAG, "calculate: milis seconde is $numSecondsRecorded")
        val numCycles = (numCrossing / 2).toDouble()
        val frequency = numCycles / 0.23
        time =  time + numSecondsRecorded
        Log.i(TAG, "calculate: frequency are here " + frequency)

        for (index in audioData.indices) {
            if(doNotGoInFirst == false)
            {
                if((20 * Math.log10(audioData[index].toDouble())) < maxDB!! && (20 * Math.log10(
                        audioData[index].toDouble()
                    )) > minDB!! && i == 0 && j == 0 && frequency > minFrequency!! && frequency < maxFrequency!!)
                {
                    mapSound1.put((20 * Math.log10(audioData[index].toDouble())), frequency)
                    i++
                    j++
                }
                if (j == 1 ){
                    if((20 * Math.log10(audioData[index].toDouble())) < maxDB!! && (20 * Math.log10(
                            audioData[index].toDouble()
                        )) > minDB!! && i == 1 && frequency > minFrequency!! && frequency < maxFrequency!!)
                    {
                        mapSound1.put((20 * Math.log10(audioData[index].toDouble())), frequency)
                    }else{
                        i++
                        j++
                        firstSoundEndTime = time;
                    }
                }
                if(i == 2){
                    doNotGoInFirst = true
                    doNotGoInSecond = false
                }
            }

            if(doNotGoInSecond == false)
            {
                if((20 * Math.log10(audioData[index].toDouble())) < maxDB!! && (20 * Math.log10(
                        audioData[index].toDouble()
                    )) > minDB!! && i == 2 && j == 2 && frequency > minFrequency!! && frequency < maxFrequency!!)
                {
                     mapSound2.put((20 * Math.log10(audioData[index].toDouble())), frequency)
                    i++
                    j++
                }
                if (j == 3 ){
                    if((20 * Math.log10(audioData[index].toDouble())) < maxDB!! && (20 * Math.log10(
                            audioData[index].toDouble()
                        )) > minDB!! && i == 3 && frequency > minFrequency!! && frequency < maxFrequency!!)
                    {
                        mapSound2.put((20 * Math.log10(audioData[index].toDouble())), frequency)
                    }else{
                        i++
                        j++
                        secondSoundStartTime = time
                    }
                }
                if(i == 4){
                    doNotGoInFirst = true
                    doNotGoInSecond = true
                }
            }
        }

        return frequency.toInt()
    }


    companion object {
        private const val TAG = "StartActivity"
    }

}