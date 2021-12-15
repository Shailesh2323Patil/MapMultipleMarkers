package com.example.mapmultiplemarker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.mapmultiplemarker.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    var context: Context = this@MapsActivity

    private val markerList = ArrayList<MarkerData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addMarkersData()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        markerList.forEach { markerData ->
            mMap.addMarker(MarkerOptions()
                .position(LatLng(markerData.latitutde,markerData.longitude))
                .anchor(0.5f,0.5f)
                .title(markerData.title)
                .snippet(markerData.snippets)
                .icon(bitmapDescriptorFromVector(context,R.drawable.ic_map_marker_gray)
            ))
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(markerList[0].latitutde,markerList[0].longitude)))
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    fun addMarkersData() {
        markerList.add(
            MarkerData(
                19.1869057,
                73.0891625,
                "Manpadeshwar Temple",
                "Hello Dombivli"
            )
        )
        markerList.add(
            MarkerData(
                19.1866125,
                73.089909,
                "Petrol Pump",
                "Hello Dombivli"
            )
        )
        markerList.add(
            MarkerData(
                19.1654799,
                73.0728797,
                "Xperia Mall",
                "Hello Dombivli"
            )
        )
    }
}