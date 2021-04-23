package com.example.soundrecorder.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.soundrecorder.R
import com.example.soundrecorder.activity.HomeActivity
import com.example.soundrecorder.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class GlobalOperation {
    var isLogout = false



    companion object {
        fun showDialog(activity: Activity?, msg: String?) {
            val dialog = Dialog(activity!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.alert_dialog)
            val text = dialog.findViewById<TextView>(R.id.cp_title) as TextView
            text.text = msg
            val dialogButton: Button = dialog.findViewById<Button>(R.id.alert_ok_btn) as Button
            dialogButton.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        fun showDataUploadedDialog(context: Context?) {
            val dialog = Dialog(context!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.uploaded_data_dialog_box)
            val dialogButton: Button = dialog.findViewById<Button>(R.id.data_uploaded_dialog_ok_btn) as Button
            dialogButton.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun showYesAndNoDialog(activity: Activity?, msg: String?) {
            val dialog = Dialog(activity!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.alert_dialog_for_yes_and_no)
            val text = dialog.findViewById<TextView>(R.id.cp_title) as TextView
            text.text = msg
            val dialogButtonOk: Button = dialog.findViewById<Button>(R.id.alert_ok_btn)
            dialogButtonOk.setOnClickListener {
                activity.startActivity(Intent(activity,HomeActivity::class.java))
                activity.finish()
            }
            val dialogButtonNo: Button = dialog.findViewById<Button>(R.id.alert_no_btn)
            dialogButtonNo.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }



        fun getFactor2ArrayList(list: ArrayList<SumarryOutput1DataClass>): Any {
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
                return arrayListOf(aPercent,bPercent,cPercent,dPercent,ePercent)
            } else {
                return arrayListOf(0.0,0.0,0.0,0.0,0.0)
            }
        }

        fun getFactor1ArrayList(list: ArrayList<SumarryOutput1DataClass>): Any {
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
                return arrayListOf(sPercent,gPercent,fPercent)

            } else {
                return arrayListOf(0.0,0.0,0.0)
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun isNetworkConnected(activity: Activity? ): Boolean {
            val cm = activity?.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetwork != null
        }



        private const val TAG = "GlobalOperation"
    }
}