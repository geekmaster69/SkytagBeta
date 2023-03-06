package com.example.skytagbeta.presentation.mapFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.skytagbeta.R
import com.example.skytagbeta.databinding.FragmentMapBinding
import com.example.skytagbeta.presentation.locationhistory.LocationHistory
import com.example.skytagbeta.presentation.locationhistory.entity.StatusListEntity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*


class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentMapBinding
    private lateinit var map: GoogleMap
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var date: String = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as? LocationHistory

        val args = activity?.intent?.getBundleExtra("Bundle")
        val status = args?.getSerializable("status") as StatusListEntity

        setHasOptionsMenu(true)

        lat = status.lat
        lng = status.lng
        date = status.date

        val mapFragment = childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googlemap: GoogleMap) {
        map = googlemap
        map.clear()
        addMarker()

        map.isMyLocationEnabled = true
    }

    private fun addMarker() {

        val snippet = String.format(
            Locale.getDefault(),
            "Date: $date")

        val coordinates = LatLng(lat, lng)
        val marker = MarkerOptions()
            .position(coordinates)
            .title("My Location")
            .snippet(snippet)

        map.addMarker(marker)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId){
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> false
    }

    override fun onDestroy() {
        setHasOptionsMenu(false)
        super.onDestroy()
    }

}