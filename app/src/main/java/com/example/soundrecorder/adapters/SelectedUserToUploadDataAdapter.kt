package com.example.soundrecorder.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.soundrecorder.R
import com.example.soundrecorder.activity.UploadDataActivity
import com.example.soundrecorder.models.UserDataClass

class SelectedUserToUploadDataAdapter(
    val listOfSelectedUser: ArrayList<UserDataClass>,
    val context: UploadDataActivity,
    val listOfSelectedUserUUID: ArrayList<String>
) : RecyclerView.Adapter<SelectedUserToUploadDataAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.horizontal_list_of_selected_user, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = listOfSelectedUser[position]
        holder.userName.text= user.name
        holder.imageToDeselectUser.setOnClickListener {
            listOfSelectedUser.removeAt(position)
            notifyDataSetChanged()
            Toast.makeText(context,holder.userName.text.toString()+" deselected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return listOfSelectedUser.size
    }

    class ViewHolder(view: View)  : RecyclerView.ViewHolder(view) {
        var userName: TextView = view.findViewById(R.id.user_name_textview)
        var imageToDeselectUser: ImageView = view.findViewById(R.id.deselect_user_imageview)
    }

    companion object {
        private const val TAG = "SelectedUserToUploadDat"
    }
}