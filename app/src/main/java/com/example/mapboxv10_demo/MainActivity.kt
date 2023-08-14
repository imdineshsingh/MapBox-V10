package com.example.mapboxv10_demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnPoint).setOnClickListener {
            startActivity(Intent(this@MainActivity,PointActivity::class.java))
        }
        findViewById<Button>(R.id.btnLine).setOnClickListener {
            startActivity(Intent(this@MainActivity,LineActivity::class.java))
        }
        findViewById<Button>(R.id.btnPolygon).setOnClickListener {
            startActivity(Intent(this@MainActivity,PolygonActivity::class.java))
        }
        findViewById<Button>(R.id.btnLayer).setOnClickListener {
            startActivity(Intent(this@MainActivity,LayerContrlActivity::class.java))
        }
        findViewById<Button>(R.id.btnTracking).setOnClickListener {
            startActivity(Intent(this@MainActivity,TrackingActivity::class.java))
        }
        findViewById<Button>(R.id.btnNavigation).setOnClickListener {
            startActivity(Intent(this@MainActivity,NavigationActivity::class.java))
        }
    }
}