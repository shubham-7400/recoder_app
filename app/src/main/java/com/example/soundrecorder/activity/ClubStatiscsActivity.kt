package com.example.soundrecorder.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.soundrecorder.R
import com.example.soundrecorder.databinding.ActivityClubStatiscsBinding
import com.example.soundrecorder.databinding.ActivityStartBinding

class ClubStatiscsActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityClubStatiscsBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_club_statiscs)
        binding = ActivityClubStatiscsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.backBtn.setOnClickListener(this)
        binding.bowlingSpeedLayout.setOnClickListener(this)
        binding.batSwingLayout.setOnClickListener(this)
        binding.aerialTravelLayout.setOnClickListener(this)
        binding.ballExitLayout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
       when(v){
           binding.backBtn->{
               finish()
           }
           binding.bowlingSpeedLayout->{
              val intent = Intent(this,BowlingSessionActivity::class.java)
               startActivity(intent)
           }
           binding.batSwingLayout->{
               val intent = Intent(this,BattingSessionActivity::class.java)
               intent.putExtra("call_for","batting")
               startActivity(intent)
           }
           binding.aerialTravelLayout->{
               val intent = Intent(this,BattingSessionActivity::class.java)
               intent.putExtra("call_for","aerial")
               startActivity(intent)
           }
           binding.ballExitLayout->{
               val intent = Intent(this,BattingSessionActivity::class.java)
               intent.putExtra("call_for","bowling")
               startActivity(intent)
           }
       }
    }
}