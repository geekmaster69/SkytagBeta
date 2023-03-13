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
import com.example.skytagbeta.presentation.locationhistory.entity.StatusListEntity

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
            binding.tvBaterry.text ="${list.battery}%"
            binding.tvID.text = list.id.toString()
            binding.tvBluetooth.text = list.ble
            binding.tvGps.text = list.gps
            binding.tvNetwork.text = list.network
            binding.tvTime.text = list.date
            binding.tvSatelite.text = list.accuracy


            if (list.code == "20"){ binding.root.setCardBackgroundColor(Color.parseColor("#CB3234")) }

            if (list.network == "false"){binding.tvNetwork.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_network_off, 0, 0, 0)}
            else {binding.tvNetwork.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_network, 0, 0, 0)}

            if (list.gps == "false"){binding.tvGps.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_gps_off, 0, 0, 0)}
            else{binding.tvGps.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_gps, 0, 0, 0)}

            if (list.ble == "false"){binding.tvBluetooth.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bluetooth_off, 0, 0, 0)}
            else {binding.tvBluetooth.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bluetooth, 0, 0, 0)}



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