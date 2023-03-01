package com.example.skytagbeta.presentation.recordactivity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.window.OnBackInvokedCallback
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skytagbeta.R
import com.example.skytagbeta.databinding.ActivityRecordBinding
import com.example.skytagbeta.presentation.main.adapter.StatusListAdapter
import com.example.skytagbeta.presentation.main.model.entity.StatusListEntity
import com.example.skytagbeta.presentation.main.viewmodel.BleServiceViewModel

class RecordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordBinding
    private val mViewModel: BleServiceViewModel by viewModels()
    private lateinit var mAdapter: StatusListAdapter
    private lateinit var mLinearLayout: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.title = getString(R.string.location_history)



        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        mViewModel.getStatusList()
        mViewModel.statusInfo.observe(this){statusList ->
            if (statusList.isEmpty()){
                mAdapter = StatusListAdapter(mutableListOf())
                mLinearLayout = LinearLayoutManager(this)
                binding.rvStatus.apply {
                    layoutManager = mLinearLayout
                    adapter = mAdapter
                }
            }else{
                mAdapter = StatusListAdapter(statusList.reversed() as MutableList<StatusListEntity>)
                mLinearLayout = LinearLayoutManager(this)
                binding.rvStatus.apply {
                    layoutManager = mLinearLayout
                    adapter = mAdapter
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            io.paperdb.R.id.home ->{
                onBackPressedDispatcher.addCallback(this, onBackInvokedCallback)


            }
        }
        return super.onOptionsItemSelected(item)
    }
    @SuppressLint("NewApi")
    private val onBackInvokedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {

        }


    }

}