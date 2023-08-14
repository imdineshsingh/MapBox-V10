package com.example.mapboxv10_demo.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.Toast
import com.mapbox.geojson.Point
import java.util.*

fun Activity.showToast(msg:String){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        .show()
}

object Utils {

     fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    fun createRandomPoint(): Point {
        val random = Random()
        return Point.fromLngLat(
            random.nextDouble() * -360.0 + 180.0,
            random.nextDouble() * -180.0 + 90.0
        )
    }
}