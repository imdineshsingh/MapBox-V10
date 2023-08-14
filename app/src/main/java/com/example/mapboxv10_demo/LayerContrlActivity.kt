package com.example.mapboxv10_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.maps.MapView
import com.mapbox.maps.Style

class LayerContrlActivity : BaseActivity() {
    val STYLES =
        arrayOf(Style.MAPBOX_STREETS, Style.OUTDOORS, Style.LIGHT, Style.DARK, Style.SATELLITE_STREETS)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layer_control)

        initTracking(findViewById(R.id.mapView))
        findViewById<Button>(R.id.changeStyle).setOnClickListener {
            findViewById<MapView>(R.id.mapView).getMapboxMap().loadStyleUri(STYLES.random())
        }
    }
}