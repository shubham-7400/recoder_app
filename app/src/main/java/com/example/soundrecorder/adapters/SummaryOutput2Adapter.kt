package com.example.soundrecorder.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.soundrecorder.R
import com.example.soundrecorder.models.SumarryOutput2DataClass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class SummaryOutput2Adapter(context: Context) : RecyclerView.Adapter<SummaryOutput2Adapter.ViewHolder>() {
    var myPref = context.getSharedPreferences("MyOutputs", Context.MODE_PRIVATE)
    var jsHashSetOutput2String = myPref.getString("jsHashSetOutput2String", "jsHashSetOutput2StringNotExist")
    var gson = Gson()
    var type: Type? = object : TypeToken<HashSet<SumarryOutput2DataClass>>() {}.getType()
    var SumarryOutput2DataClassHashSet: HashSet<SumarryOutput2DataClass> = gson.fromJson(jsHashSetOutput2String, type)
    var list: ArrayList<SumarryOutput2DataClass> = ArrayList<SumarryOutput2DataClass>(SumarryOutput2DataClassHashSet)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  ViewHolder {
        return  ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.summary_output2_recyclerview_layout,parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var sumarryOutput2DataClassObj = list.get(position)
        holder.bind(sumarryOutput2DataClassObj)
    }

    override fun getItemCount(): Int {
        Log.i(Companion.TAG, "getItemCount: array length " + list.size)
        return list.size
    }

    class ViewHolder(view: View) :RecyclerView.ViewHolder(view)
    {
        var output2 = view.findViewById<TextView>(R.id.output2)
        var type = view.findViewById<TextView>(R.id.type)
        var output4 = view.findViewById<TextView>(R.id.output4)
        fun bind(obj: SumarryOutput2DataClass) {
            output2.text = obj.output2
            type.text = obj.type
            output4.text = obj.output4
        }

    }

    companion object {
        private const val TAG = "SummaryOutput1Adapter"
    }
}