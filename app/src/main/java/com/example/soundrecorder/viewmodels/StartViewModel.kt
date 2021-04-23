package com.example.soundrecorder.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.soundrecorder.models.SumarryOutput1DataClass
import com.example.soundrecorder.models.SumarryOutput2DataClass
import com.example.soundrecorder.models.SumarryOutput3DataClass

class StartViewModel : ViewModel() {
    var outputs1 = HashSet<SumarryOutput1DataClass>()
    var outputs2 = HashSet<SumarryOutput2DataClass>()
    var outputs3 = HashSet<SumarryOutput3DataClass>()
    var output1 = 0.0
    var output2 = 0.0
    var output3 = 0.0
    var output4 = 0.0

    init {
        Log.i(Companion.TAG, " view model created ")
    }

    override fun onCleared() {
        Log.i(TAG, "onCleared: start view model destroyed")
        super.onCleared()
    }

    fun saveAllOutputsArrayInViewModel(outputs1: HashSet<SumarryOutput1DataClass>?, outputs2: HashSet<SumarryOutput2DataClass>?, outputs3: HashSet<SumarryOutput3DataClass>?) {
        if (outputs1 != null) {
            this.outputs1 = outputs1
        }
        if (outputs2 != null) {
            this.outputs2 = outputs2
        }
        if (outputs3 != null) {
            this.outputs3 = outputs3
        }
    }

    fun getOutputs1Data(): HashSet<SumarryOutput1DataClass> {
        return outputs1
    }

    fun getOutputs2Data(): HashSet<SumarryOutput2DataClass> {
        return outputs2
    }

    fun getOutputs3Data(): HashSet<SumarryOutput3DataClass> {
        return outputs3
    }

    fun setAllCurrentOutput(output1: Double, output2: Double, output3: Double) {
        this.output1 = output1
        this.output2 = output2
        this.output3 = output3
    }

    @JvmName("getOutput11")
    fun getOutput1(): Double {
        return output1
    }

    @JvmName("getOutput21")
    fun getOutput2(): Double {
        return  output2
    }

    @JvmName("getOutput31")
    fun getOutput3(): Double {
        return output3
    }

    @JvmName("getOutput41")
    fun getOutput4(): Double {
        return output4
    }

    companion object {
        private const val TAG = "StartViewModel"
    }
}