package com.example.skytagbeta.presentation.locationhistory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skytagbeta.R
import com.example.skytagbeta.base.utils.showToast
import com.example.skytagbeta.databinding.ActivityRecordBinding
import com.example.skytagbeta.presentation.locationhistory.inter.OnClickListener
import com.example.skytagbeta.presentation.locationhistory.adapter.StatusListAdapter
import com.example.skytagbeta.presentation.main.model.entity.StatusListEntity
import com.example.skytagbeta.presentation.main.viewmodel.BleServiceViewModel
import com.example.skytagbeta.presentation.mapFragment.MapFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class LocationHistory : AppCompatActivity(), OnClickListener {
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
                mAdapter = StatusListAdapter(mutableListOf(), this)
                mLinearLayout = LinearLayoutManager(this)
                binding.rvStatus.apply {
                    layoutManager = mLinearLayout
                    adapter = mAdapter
                }
            }else{
                mAdapter = StatusListAdapter(statusList.reversed() as MutableList<StatusListEntity>, this)
                mLinearLayout = LinearLayoutManager(this)
                binding.rvStatus.apply {
                    layoutManager = mLinearLayout
                    adapter = mAdapter
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_location_history, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_delete ->{
                mViewModel.deleteAllStatus()
                setupRecyclerView()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(statusListEntity: StatusListEntity) {
        val args = Bundle()
        args.putSerializable("status", statusListEntity)
        intent.putExtra("Bundle", args)
        launchMapFragment()

    }

    private fun launchMapFragment() {
        val fragment = MapFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(null)

        fragmentTransaction.add(R.id.locationHistory, fragment)
        fragmentTransaction.commit()

    }
}