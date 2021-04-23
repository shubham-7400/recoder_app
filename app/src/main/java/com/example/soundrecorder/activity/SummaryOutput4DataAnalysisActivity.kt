package com.example.soundrecorder.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.soundrecorder.R
import com.example.soundrecorder.databinding.ActivitySummaryOutput1Binding
import com.example.soundrecorder.databinding.ActivitySummaryOutput4DataAnalysisBinding

class SummaryOutput4DataAnalysisActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivitySummaryOutput4DataAnalysisBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryOutput4DataAnalysisBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setUiAction()

    }

    private fun setUiAction() {
        binding.backBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.backBtn -> {
                super.onBackPressed();
            }
        }
    }
}