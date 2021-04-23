package com.example.soundrecorder.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.soundrecorder.R
import com.example.soundrecorder.models.SumarryOutput3DataClass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class SummaryOutput3Adapter(context: Context) : RecyclerView.Adapter<SummaryOutput3Adapter.ViewHolder>() {
    var myPref = context.getSharedPreferences("MyOutputs", Context.MODE_PRIVATE)
    var jsHashSetOutput3String = myPref.getString("jsHashSetOutput3String", "jsHashSetOutput2StringNotExist")
    var gson = Gson()
    var type: Type? = object : TypeToken<HashSet<SumarryOutput3DataClass>>() {}.getType()
    var SumarryOutput3DataClassHashSet: HashSet<SumarryOutput3DataClass> = gson.fromJson(jsHashSetOutput3String, type)
    var list: ArrayList<SumarryOutput3DataClass> = ArrayList<SumarryOutput3DataClass>(SumarryOutput3DataClassHashSet)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  ViewHolder {
        return  ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.summary_output3_recyclerview_layout,parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var SumarryOutput3DataClassObj = list.get(position)
        holder.bind(SumarryOutput3DataClassObj)
    }

    override fun getItemCount(): Int {
        Log.i(Companion.TAG, "getItemCount: array length " + list.size)
        return list.size
    }

    class ViewHolder(view: View) :RecyclerView.ViewHolder(view)
    {
        var output3 = view.findViewById<TextView>(R.id.output3)
        var type = view.findViewById<TextView>(R.id.type)
        fun bind(obj: SumarryOutput3DataClass) {
            output3.text = obj.output3
            type.text = obj.type
        }

    }

    companion object {
        private const val TAG = "SummaryOutput3Adapter"
    }
}