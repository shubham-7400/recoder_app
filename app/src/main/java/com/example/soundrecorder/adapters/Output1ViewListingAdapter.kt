package com.example.soundrecorder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.soundrecorder.databinding.Output1ViewListingBinding
import com.example.soundrecorder.models.Output1ViewListingDataClass

class Output1ViewListingAdapter(context: Context) : ListAdapter<Output1ViewListingDataClass, Output1ViewListingAdapter.ViewHolder>(MainDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Output1ViewListingAdapter.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: Output1ViewListingAdapter.ViewHolder, position: Int) {
        val Output1ViewListingDataClassObj = getItem(position)
        holder.bind(Output1ViewListingDataClassObj)
    }

    class ViewHolder(val binding: Output1ViewListingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(obj: Output1ViewListingDataClass) {
            binding.outputDate.text = obj.outputDate
            binding.output1.text = obj.output1
            binding.factorPercentage.text = obj.factorPercentage
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = Output1ViewListingBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}


class MainDiffCallback : DiffUtil.ItemCallback<Output1ViewListingDataClass>() {

    override fun areItemsTheSame(oldItem: Output1ViewListingDataClass, newItem: Output1ViewListingDataClass): Boolean {
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(oldItem: Output1ViewListingDataClass, newItem: Output1ViewListingDataClass): Boolean {
        return oldItem == newItem
    }

}