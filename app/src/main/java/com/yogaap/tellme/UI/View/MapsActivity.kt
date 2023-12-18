package com.yogaap.tellme.UI.View

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yogaap.tellme.Data.di.ViewModelFactory
import com.yogaap.tellme.R
import com.yogaap.tellme.databinding.ActivityMapsBinding
import com.yogaap.tellme.UI.viewModel.MainViewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var factory: ViewModelFactory
    private val mainViewModel: MainViewModel by viewModels { factory }
    private var token = ""

    companion object {
        private const val TAG = "MapsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupMap()
        setupViewModel()
        setupUser()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }

        setupSession()
        setupList()
        getMyLocation()

    }

    private fun setupView() {
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
    }

    private fun setupSession() {
        mainViewModel.getSession().observe(this@MapsActivity) {
            token = it.token
            Log.d(TAG, "setupSession: $token")
            getStories(token)
        }
    }

    private fun setupUser() {
        mainViewModel.getSession().observe(this@MapsActivity) { session ->
            token = session.token
            if (session.isLogin) {
                mainViewModel.getListStoriesWithLocation(token)
            }
        }
    }

    private fun setupList() {
        mainViewModel.list.observe(this@MapsActivity) { listResponse ->
            listResponse?.listStory?.forEach { list ->
                list.lat?.let { lat ->
                    list.lon?.let { lon ->
                        mMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(lat, lon))
                                .title("Story from : ${list.name}")
                                .snippet("ID : ${list.id}")
                        )
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }

            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }

            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }

            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun getStories(token : String) {
        mainViewModel.getListStoriesWithLocation(token)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}