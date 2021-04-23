package com.example.soundrecorder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.soundrecorder.R
import com.example.soundrecorder.models.ViewListingMoldel
import java.util.ArrayList

class BowlingSessionAdapter(val bowlingDataList: ArrayList<ViewListingMoldel>, val context: Context): RecyclerView.Adapter<BowlingSessionAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val playerNameTv = itemView.findViewById<TextView>(R.id.player_name_tv)
        val bowlingSpeedTv = itemView.findViewById<TextView>(R.id.bowling_speed_tv)
        val straightTv = itemView.findViewById<TextView>(R.id.straight_tv)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val viewHolder = NoteViewHolder(LayoutInflater.from(context).inflate(R.layout.item_bowling_session,parent,false))

        return viewHolder
    }

    override fun getItemCount(): Int {
        return bowlingDataList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {

        holder.playerNameTv.text = bowlingDataList.get(position).playerName
        holder.bowlingSpeedTv.text = bowlingDataList.get(position).bowlingSpeed
        holder.straightTv.text = bowlingDataList.get(position).straight+" %"
    }

}