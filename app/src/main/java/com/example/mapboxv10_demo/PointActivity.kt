package com.example.mapboxv10_demo

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.mapboxv10_demo.utils.Utils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import java.lang.ref.WeakReference

class PointActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private var latitude = 0.0
    private var longitude = 0.0
    var mapBoxMap: MapboxMap? = null
    var centerLat=0.0
    var centerLong=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point)
        mapView = findViewById<MapView>(R.id.mapView)
        val fabAddCurrentLocation = findViewById<Button>(R.id.fabAddCurrentLocation)
        val fabAddCustomLocation = findViewById<Button>(R.id.fabAddCustomLocation)
        fabAddCurrentLocation.setOnClickListener {
            addAnnotationToMap(latitude, longitude)
        }
        fabAddCustomLocation.setOnClickListener {
            addAnnotationToMap(centerLat, centerLong)
        }

        //mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
        /*mapView.getMapboxMap().loadStyle(
            Style.MAPBOX_STREETS
        ) {
            +geoJsonSource(BOUNDS_ID) {
                featureCollection(FeatureCollection.fromFeatures(listOf()))
            }
            // addAnnotationToMap()
        }*/

        mapView.getMapboxMap().loadStyle(
            style(Style.MAPBOX_STREETS) {
                +geoJsonSource("BOUNDS_ID") {
                    featureCollection(FeatureCollection.fromFeatures(listOf()))
                }
            }
        ) { /*setupBounds(SAN_FRANCISCO_BOUND)*/ }

        locationTracking()
        showStaticMarkar()
        mapBoxMap = mapView.getMapboxMap()
    }
    ////////////Location Tracking/////////////////////

    private fun locationTracking() {
        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            onMapReady()
        }
    }

    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }
    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        latitude = it.latitude()
        longitude = it.longitude()
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }
    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            centerLat=mapBoxMap?.cameraState?.center?.latitude()?:0.0
            centerLong=mapBoxMap?.cameraState?.center?.longitude()?:0.0
            Log.d(
                "TAG",
                "onMove: LAT: ${mapBoxMap?.cameraState?.center?.latitude()},Long: ${mapBoxMap?.cameraState?.center?.longitude()}"
            )
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    private fun onMapReady() {
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            initLocationComponent()

            setupGesturesListener()
        }
    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
             this.locationPuck = LocationPuck2D(
                 bearingImage = AppCompatResources.getDrawable(
                     this@PointActivity,
                     R.drawable.mapbox_user_puck_icon,
                 ),
                 shadowImage = AppCompatResources.getDrawable(
                     this@PointActivity,
                     R.drawable.mapbox_user_icon_shadow,
                 ),
                 scaleExpression = interpolate {
                     linear()
                     zoom()
                     stop {
                         literal(0.0)
                         literal(0.6)
                     }
                     stop {
                         literal(20.0)
                         literal(1.0)
                     }
                 }.toJson()
             )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        locationComponentPlugin.addOnIndicatorBearingChangedListener(
            onIndicatorBearingChangedListener
        )
    }

    private fun onCameraTrackingDismissed() {
        Toast.makeText(this, "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        //mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    ////////////////////////////////default-point-annotation////////////////////////

    // random add circles across the globe
    //val pointAnnotationOptionsList: MutableList<PointAnnotationOptions> = ArrayList()
    private fun addAnnotationToMap(latitude: Double, longitude: Double) {
        bitmapFromDrawableRes(
            this@PointActivity,
            R.drawable.ic_square
        )?.let {
            val annotationApi = mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager(mapView)
            //for (i in 0..10) {
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(longitude, latitude))
                .withIconImage(it)
                .withDraggable(true)


            //pointAnnotationOptionsList.add(pointAnnotationOptions)
            // }
            pointAnnotationManager.create(pointAnnotationOptions/*pointAnnotationOptionsList*/)
            pointAnnotationManager.addClickListener(OnPointAnnotationClickListener { annotation ->
                Toast.makeText(
                    this,
                    "${annotation.point.latitude()}:::${annotation.point.longitude()}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(
                    "TAG",
                    "onAnnotationClick: ,${annotation.point.latitude()} ,${annotation.point.longitude()}"
                )
                true
            })
        }
    }


    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        Utils.convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))


    private fun showStaticMarkar() {
        val crosshair = View(this)
        crosshair.layoutParams = FrameLayout.LayoutParams(20, 20, Gravity.CENTER)
        crosshair.setBackgroundColor(Color.BLUE)
        mapView.addView(crosshair)

    }

    //////////////////////////////////////////////////LINE/////////////////////////////////////////////////////////////////////////




}