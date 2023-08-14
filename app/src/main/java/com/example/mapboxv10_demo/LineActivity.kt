package com.example.mapboxv10_demo

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.mapboxv10_demo.utils.ScreenUtils.getScreenWidth
import com.example.mapboxv10_demo.utils.showToast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import java.util.*

class LineActivity : BaseActivity() { //PolylineAnnotationActivity
    private var polylineAnnotationManager: PolylineAnnotationManager? = null
    private var index: Int = 0

    /* val points = listOf(
         Point.fromLngLat(77.317790, 28.599153),
         Point.fromLngLat(82.012069, 21.658342),
         Point.fromLngLat(74.243079, 25.073782),
     )*/
    val points = arrayListOf<Point>()

    private lateinit var annotationPlugin: AnnotationPlugin
    var polylineAnnotationOptions: PolylineAnnotationOptions? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line)
        val mapView = findViewById<MapView>(R.id.mapView)
        val fabAddCurrentLocation = findViewById<FloatingActionButton>(R.id.fabAddCurrentLocation)
        val fabSave = findViewById<FloatingActionButton>(R.id.fabSave)



        fabAddCurrentLocation.setOnClickListener {
            points.add(Point.fromLngLat(centerLong, centerLat))
            // annotationPlugin.removeAnnotationManager(polylineAnnotationManager!!)

            drawLine(points)
/*            polylineAnnotationOptions =
                PolylineAnnotationOptions()
                    .withPoints(points)
                    .withDraggable(true)
                    .withLineColor(Color.RED)
                    .withLineWidth(8.0)

            polylineAnnotationManager?.create(polylineAnnotationOptions!!)*/
           /* try {
                polylineAnnotationManager?.update(polylineAnnotationManager?.annotations?.get(0)!!)
            }catch (e:Exception){

            }*/
        }
        fabSave.setOnClickListener {

            //  points.clear()
        }


        mapView.getMapboxMap().loadStyleUri(Style.LIGHT) {
            markingAndTracking(mapView)
            annotationPlugin = mapView.annotations
            polylineAnnotationManager = annotationPlugin.createPolylineAnnotationManager(
                annotationConfig = AnnotationConfig(PITCH_OUTLINE, LAYER_ID, SOURCE_ID)
            ).apply {
                it.getLayer(LAYER_ID)?.let { layer ->
                    Toast.makeText(this@LineActivity, layer.layerId, Toast.LENGTH_LONG).show()
                }
                addClickListener(
                    OnPolylineAnnotationClickListener {
                        polylineAnnotationManager?.delete(it)
                        this@LineActivity.showToast("click ${it.id}")
                        false
                    }
                )
                addInteractionListener(object : OnPolylineAnnotationInteractionListener {
                    override fun onSelectAnnotation(annotation: PolylineAnnotation) {

                        this@LineActivity.showToast("onSelectAnnotation ${annotation.id}")
                    }
                    override fun onDeselectAnnotation(annotation: PolylineAnnotation) {
                        this@LineActivity.showToast("onDeselectAnnotation ${annotation.id}")
                    }
                })


                /* val polylineAnnotationOptions: PolylineAnnotationOptions =
                     PolylineAnnotationOptions()
                         .withPoints(points)
                         .withDraggable(true)
                         .withLineColor(Color.RED)
                         .withLineWidth(8.0)
                 create(polylineAnnotationOptions)*/

                // random add lines across the globe
                /*val lists: MutableList<List<Point>> = ArrayList<List<Point>>()
                for (i in 0..99) {
                    lists.add(AnnotationUtils.createRandomPoints())
                }
                val lineOptionsList = lists.map {
                    val color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
                    PolylineAnnotationOptions()
                        .withPoints(it)
                        .withLineColor(color)
                }

                create(lineOptionsList)*/

                /*AnnotationUtils.loadStringFromAssets(
                    this@LineActivity,
                    "annotations.json"
                )?.let {
                    create(FeatureCollection.fromJson(it))
                }*/
            }
        }

    }

    private fun drawLine(points: List<Point>) {
        polylineAnnotationManager?.deleteAll()
        // Set options for the resulting fill layer.
        val polygonAnnotationOptions: PolylineAnnotationOptions = PolylineAnnotationOptions()
            .withPoints(points)
            .withDraggable(true)
            .withLineColor(Color.RED)
            .withLineWidth(4.0)
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