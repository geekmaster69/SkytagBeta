package com.example.skytagbeta.presentation.locationhistory

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skytagbeta.R
import com.example.skytagbeta.databinding.ActivityRecordBinding
import com.example.skytagbeta.presentation.locationhistory.adapter.StatusListAdapter
import com.example.skytagbeta.presentation.locationhistory.datePicker.DatePickerFragment
import com.example.skytagbeta.presentation.locationhistory.inter.OnClickListener
import com.example.skytagbeta.presentation.locationhistory.entity.StatusListEntity
import com.example.skytagbeta.presentation.locationhistory.viewmodel.LocationViewModel
import com.example.skytagbeta.presentation.main.viewmodel.BleServiceViewModel
import com.example.skytagbeta.presentation.mapFragment.MapFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LocationHistory : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityRecordBinding
    private val mViewModel: LocationViewModel by viewModels()
    private lateinit var mAdapter: StatusListAdapter
    private lateinit var mLinearLayout: LinearLayoutManager
    private var startDate: String? = null
    private var finishDate: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.title = getString(R.string.location_history)

        setupRecyclerView()

        binding.etDate1.setOnClickListener { startDatePicker() }

        binding.etDate2.setOnClickListener { finishDatePicker() }

    }
    private fun startDatePicker() {
        val datePicker = DatePickerFragment { day, month, year -> startDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun finishDatePicker() {
        val datePicker = DatePickerFragment { day, month, year -> finishDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun startDateSelected(day: Int, month: Int, year: Int){
        startDate = "$year-${(month + 1).twoDigits()}-${day.twoDigits()}"
        binding.etDate1.setText(startDate)
        binding.etDate2.isEnabled = true
    }

    private fun finishDateSelected(day: Int, month: Int, year: Int){
        finishDate = "$year-${(month + 1).twoDigits()}-${day.twoDigits()}"
        binding.etDate2.setText(finishDate)
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
            }else if (startDate.isNullOrEmpty() && finishDate.isNullOrEmpty()){
                mAdapter = StatusListAdapter(statusList.reversed() as MutableList<StatusListEntity>, this)
                mLinearLayout = LinearLayoutManager(this)
                binding.rvStatus.apply {
                    layoutManager = mLinearLayout
                    adapter = mAdapter }

            }else{
                mAdapter = StatusListAdapter(statusList.reversed().filter { it.date >= "$startDate 00:00:00" && it.date <= "$finishDate 23:59:59"  } as MutableList<StatusListEntity>, this)
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
                MaterialAlertDialogBuilder(this)
                    .setTitle("Eliminar?")
                    .setMessage("Eliminar todo el historial de ubicaciones?")
                    .setIcon(R.drawable.ic_delete_forever)
                    .setPositiveButton("Eliminar"){_,_ ->
                        mViewModel.deleteAllStatus()
                    }
                    .setNegativeButton("Cancelar"){_,_, ->
                        null
                    }.show()

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

    private fun Int.twoDigits() =
        if (this <= 9) "0$this" else this.toString()

}