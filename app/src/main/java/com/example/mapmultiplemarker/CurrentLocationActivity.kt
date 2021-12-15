package com.example.mapmultiplemarker

import android.Manifest
import android.R.attr
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import android.view.View
import android.widget.ImageView

import android.widget.TextView
import android.R.attr.radius


class CurrentLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var mGoogleMap: GoogleMap
    var mapFrag: SupportMapFragment? = null
    lateinit var mLocationRequest: LocationRequest
    var mLastLocation: Location? = null
    internal var mCurrLocationMarker: Marker? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private val markerList = ArrayList<MarkerData>()

    var context: Context = this@CurrentLocationActivity

    private var circle: Circle? = null

    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                Log.i(
                    "MapsActivity",
                    "Location: " + location.getLatitude() + " " + location.getLongitude()
                )
                mLastLocation = location
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker?.remove()
                }

                //Place current location marker
                val latLng = LatLng(location.latitude, location.longitude)

                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title("Current Position")
                markerOptions.snippet("You Are Here")
                //markerOptions.icon(bitmapDescriptorFromVector(context, R.drawable.ic_map_marker_gray))
                markerOptions.icon(
                    BitmapDescriptorFactory.fromBitmap(
                        createCustomMarker(
                            "You Are Here",
                            R.layout.marker_layout_2,
                            R.drawable.ic_map_marker_gray
                        )
                    )
                )

                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions)

                drawCircle(latLng, 4.0)
                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11.0F))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_location)

        supportActionBar?.title = "Map Location Activity"

        addMarkersData()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mapFrag = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFrag?.getMapAsync(this)
    }

    public override fun onPause() {
        super.onPause()
        //stop location updates when Activity is no longer active
        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        mGoogleMap.uiSettings.isZoomControlsEnabled = true

        //mGoogleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        mGoogleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 120000 // two minute interval
        mLocationRequest.fastestInterval = 120000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Location Permission already granted
                placeMarkers()
            } else {
                //Request Location Permission
                checkLocationPermission()
            }
        } else {
            placeMarkers()
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            this@CurrentLocationActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    }
                    .create()
                    .show()


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        placeMarkers()
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    companion object {
        val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }

    private fun addMarkersData() {
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
                19.2296376,
                73.0883814,
                "Metro Junction",
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

    private fun placeMarkers() {
        Looper.myLooper()?.let {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            mFusedLocationClient?.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                it
            )
        }
        mGoogleMap.isMyLocationEnabled = true

        markerList.forEach { markerData ->
            mGoogleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(markerData.latitutde, markerData.longitude))
                    .anchor(0.5f, 0.5f)
                    .title(markerData.title)
                    .snippet(markerData.snippets)
                    //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .icon(
                        BitmapDescriptorFactory.fromBitmap(
                            createCustomMarker(
                                markerData.title,
                                R.layout.marker_layout,
                                R.drawable.ic_map_marker_red
                            )
                        )
                    )
            )
        }
        mGoogleMap.moveCamera(
            CameraUpdateFactory.newLatLng(
                LatLng(
                    markerList[0].latitutde,
                    markerList[0].longitude
                )
            )
        )
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

    private fun createCustomMarker(title: String, layout: Int, drawable: Int): Bitmap {
        val markerLayout = layoutInflater.inflate(layout, null)
        val markerImage = markerLayout.findViewById(R.id.marker_image) as ImageView
        val markerRating = markerLayout.findViewById(R.id.marker_text) as TextView

        markerImage.setImageResource(drawable)
        markerRating.text = title
        markerLayout.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        markerLayout.layout(0, 0, markerLayout.measuredWidth, markerLayout.measuredHeight)
        val bitmap = Bitmap.createBitmap(
            markerLayout.measuredWidth,
            markerLayout.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        markerLayout.draw(canvas)
        return bitmap
    }

    private fun drawCircle(latLng: LatLng, radius: Double) {
        val circleOptions = CircleOptions()
            .center(latLng)
            .radius(radius)
            .strokeWidth(1.0f)
            .strokeColor(ContextCompat.getColor(context!!, R.color.gray))
            .fillColor(ContextCompat.getColor(context!!, R.color.gray))

        circle?.remove() // Remove old circle.
        circle = mGoogleMap?.addCircle(circleOptions) // Draw new circle.
    }

}