package com.example.mapboxv10_demo

import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.core.graphics.alpha
import com.example.mapboxv10_demo.utils.showToast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.plugin.annotation.Annotation
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*

class PolygonActivity:BaseActivity() {
    private var polylineAnnotationManager: PolygonAnnotationManager? = null
    private var index: Int = 0

    /* val points = listOf(
         Point.fromLngLat(77.317790, 28.599153),
         Point.fromLngLat(82.012069, 21.658342),
         Point.fromLngLat(74.243079, 25.073782),
     )*/
    //val points = arrayListOf<Point>()

    //val pointtt= arrayListOf<List<Point>>()
    private var mPointsList = ArrayList<ArrayList<Point>>()

    private lateinit var annotationPlugin: AnnotationPlugin
    var polylineAnnotationOptions: PolygonAnnotationOptions? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line)
        val mapView = findViewById<MapView>(R.id.mapView)
        val fabAddCurrentLocation = findViewById<FloatingActionButton>(R.id.fabAddCurrentLocation)
        val fabSave = findViewById<FloatingActionButton>(R.id.fabSave)
        mPointsList.add(ArrayList())

        fabAddCurrentLocation.setOnClickListener {

            mPointsList.first().add(Point.fromLngLat(centerLong, centerLat))
            drawPolygon(mPointsList)

/*            points.add(Point.fromLngLat(centerLong, centerLat))
            pointtt.add(points)
            drawPolygon(pointtt)*/

/*
            polylineAnnotationOptions =
                PolygonAnnotationOptions()
                    .withPoints(pointtt)
                    .withDraggable(true)
                    .withFillColor("#e6e6ff")
                    .withFillOutlineColor(Color.RED)
            polylineAnnotationManager?.create(polylineAnnotationOptions!!)
*/

        }
        fabSave.setOnClickListener {
            //points.clear()
        }


        mapView.getMapboxMap().loadStyleUri(Style.LIGHT) {
            markingAndTracking(mapView)
            annotationPlugin = mapView.annotations
            polylineAnnotationManager = annotationPlugin.createPolygonAnnotationManager(
                annotationConfig = AnnotationConfig(PITCH_OUTLINE, LAYER_ID, SOURCE_ID)
            ).apply {
                it.getLayer(LAYER_ID)?.let { layer ->
                    Toast.makeText(this@PolygonActivity, layer.layerId, Toast.LENGTH_LONG).show()
                }
                addClickListener(
                    OnPolygonAnnotationClickListener {
                        this@PolygonActivity.showToast("click ${it.id}")
                        false
                    }
                )
                addInteractionListener(object : OnPolygonAnnotationInteractionListener {
                    override fun onDeselectAnnotation(annotation: PolygonAnnotation) {
                        this@PolygonActivity.showToast("onDeselectAnnotation ${annotation.id}")
                    }

                    override fun onSelectAnnotation(annotation: PolygonAnnotation) {
                        this@PolygonActivity.showToast("onSelectAnnotation ${annotation.id}")
                    }
                })
                addDragListener(object : OnPolygonAnnotationDragListener {
                    override fun onAnnotationDrag(annotation: Annotation<*>) {

                    }

                    override fun onAnnotationDragFinished(annotation: Annotation<*>) {
                    }

                    override fun onAnnotationDragStarted(annotation: Annotation<*>) {
                    }
                })

            }
        }

    }
    private fun drawPolygon(points: List<List<Point>>) {
        polylineAnnotationManager?.deleteAll()
        // Set options for the resulting fill layer.
        val polygonAnnotationOptions: PolygonAnnotationOptions = PolygonAnnotationOptions()
            .withPoints(points)
            .withDraggable(true)
            .withFillColor("#ee4e8b")
            .withFillOpacity(0.4)
        polylineAnnotationManager?.create(polygonAnnotationOptions)
    }

    private fun markingAndTracking(mapView: MapView) {
        initTracking(mapView)
        showStaticMarkar()
    }

    companion object {
        private const val LAYER_ID = "line_layer"
        private const val SOURCE_ID = "line_source"
        private const val PITCH_OUTLINE = "pitch-outline"
    }
}