package com.example.skytagbeta.presentation.mapFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.skytagbeta.R
import com.example.skytagbeta.base.utils.showToast
import com.example.skytagbeta.databinding.FragmentMapBinding
import com.example.skytagbeta.presentation.locationhistory.LocationHistory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentMapBinding
    private lateinit var map: GoogleMap
    private var mActivity: LocationHistory? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong("args_id", 0)
        Log.d("id", id.toString())


        val mapFragment = childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment

        mapFragment.getMapAsync(this)



    }

    override fun onMapReady(googlemap: GoogleMap) {
        map = googlemap

    }


}