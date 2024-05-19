package com.dicoding.mystoryapp.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ActivityMapBinding
import com.dicoding.mystoryapp.factory.ViewModelFactory
import com.dicoding.mystoryapp.ui.main.MainViewModel
import com.dicoding.mystoryapp.ui.welcome.WelcomeActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var mapBinding: ActivityMapBinding
    private lateinit var viewModel: MapViewModel
    private var token = ""

    private fun allPermissionGranted() =
        ContextCompat.checkSelfPermission(
            this, REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted)
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT)
            else
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT)

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapBinding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(mapBinding.root)


        //set viewModel
        viewModel = obtainViewModel(this)

        //call supportmapfragment to setup map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //setup map tools
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

//        val dicodingSpace = LatLng(-6.8957643, 107.6338462)
//        mMap.addMarker(
//            MarkerOptions()
//                .position(dicodingSpace)
//                .title("Dicoding Space")
//                .snippet("Batik Kumeli No.50")
//        )
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dicodingSpace, 15f))


        //setup session
        viewModel.getSession().observe(this) { user ->
            user?.let {
                if (it.isLoggedIn) {
                    token = it.token
                    Log.d("MainActivity", "Token: $token")
                    viewModel.getListStoriesWithLocation(token)
                } else {
                    val intent = Intent(this@MapActivity, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } ?: run {
                val intent = Intent(this@MapActivity, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


        //setup location permission
        if (!allPermissionGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        //setup location of stories
        viewModel.listStory.observe(this) { data ->
            if (data != null) {
                data.forEach { story ->
                    val latLng = LatLng(story.lat, story.lon)
                    Log.d("MapActivity", "Adding marker at: $latLng")
                    mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(story.name)
                            .snippet(story.description)
                    )
                }
                //setting camera for all stories so it can display all when map is loaded
                val boundsBuilder = LatLngBounds.Builder()
                data.forEach { boundsBuilder.include(LatLng(it.lat, it.lon)) }
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100))

            } else {
                Toast.makeText(this, "Error fetching story data", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun obtainViewModel(activity: AppCompatActivity): MapViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(MapViewModel::class.java)
    }


    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    }
}