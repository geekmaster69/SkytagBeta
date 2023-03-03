package com.example.skytagbeta.presentation.locationhistory.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skytagbeta.R
import com.example.skytagbeta.databinding.ItemStatusListBinding
import com.example.skytagbeta.presentation.locationhistory.inter.OnClickListener
import com.example.skytagbeta.presentation.main.model.entity.StatusListEntity

class StatusListAdapter(private var status: MutableList<StatusListEntity>, private var listener: OnClickListener) : RecyclerView.Adapter<StatusListAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_status_list, parent,false )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = status[position]

        with(holder){
            setListener(list)
            binding.tvBaterry.text = list.battery
            binding.tvID.text = list.id.toString()
            binding.tvBluetooth.text = list.ble
            binding.tvGps.text = list.gps
            binding.tvNetwork.text = list.network
            binding.tvTime.text = list.date
            binding.tvSatelite.text = list.accuracy
        }
    }

    override fun getItemCount(): Int = status.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val binding = ItemStatusListBinding.bind(view)

        fun setListener(statusListEntity: StatusListEntity){
            binding.root.setOnClickListener {
                listener.onClick(statusListEntity)
            }
        }
    }
}