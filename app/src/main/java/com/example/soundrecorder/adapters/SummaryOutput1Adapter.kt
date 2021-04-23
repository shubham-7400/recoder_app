package com.example.soundrecorder.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.soundrecorder.R
import com.example.soundrecorder.models.SumarryOutput1DataClass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class SummaryOutput1Adapter(context: Context) : RecyclerView.Adapter<SummaryOutput1Adapter.ViewHolder>() {
    var myPref = context.getSharedPreferences("MyOutputs", Context.MODE_PRIVATE)
    var jsHashSetOutput1String = myPref.getString("jsHashSetOutput1String", "jsHashSetOutput1StringNotExist")
    var gson = Gson()
    var type: Type? = object : TypeToken<HashSet<SumarryOutput1DataClass>>() {}.getType()
    var SumarryOutput1DataClassHashSet: HashSet<SumarryOutput1DataClass> = gson.fromJson(jsHashSetOutput1String, type)
    var list: ArrayList<SumarryOutput1DataClass> = ArrayList<SumarryOutput1DataClass>(SumarryOutput1DataClassHashSet)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  ViewHolder {
        return  ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.summary_output1_recyclerview_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var sumarryOutput1DataClassObj = list.get(position)
        holder.bind(sumarryOutput1DataClassObj)
    }

    override fun getItemCount(): Int {
        Log.i(Companion.TAG, "getItemCount: array length " + list.size)
         return list.size
    }

    class ViewHolder(view: View) :RecyclerView.ViewHolder(view)
    {
        var output = view.findViewById<TextView>(R.id.output)
        var orientation = view.findViewById<TextView>(R.id.orientation)
        var frequency = view.findViewById<TextView>(R.id.frequncy)
        fun bind(obj: SumarryOutput1DataClass) {
            output.text = obj.output
            orientation.text = obj.orientation
            frequency.text = obj.frequency
        }

    }

    companion object {
        private const val TAG = "SummaryOutput1Adapter"
    }
}