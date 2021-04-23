package com.example.soundrecorder.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.soundrecorder.R
import com.example.soundrecorder.activity.UploadDataActivity
import com.example.soundrecorder.models.UserDataClass

class UploadDataAdapter(
    val listOfUser: ArrayList<UserDataClass>,
    val context: UploadDataActivity,
    val listOfSelectedUser: ArrayList<UserDataClass>,
    val selectedUserToUploadDataAdapter: SelectedUserToUploadDataAdapter,
    val listOfSelectedUserUUID: ArrayList<String>
) : RecyclerView.Adapter<UploadDataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_list_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = listOfUser[position]
        holder.name.text = user.name
        holder.phoneNumber.text = user.phoneNumber
        holder.itemView.setOnClickListener {
            listOfSelectedUser.add(user)
            selectedUserToUploadDataAdapter.notifyDataSetChanged()
            context.binding.horizontalRecyclerviewOfUserToShareData.visibility = View.VISIBLE
            context.binding.verticalRecyclerviewOfUserToShareData.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
         return listOfUser.size
    }

    class ViewHolder(view: View)  : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.user_name_textview)
        var phoneNumber: TextView = view.findViewById(R.id.user_phone_number_textview)
    }

}