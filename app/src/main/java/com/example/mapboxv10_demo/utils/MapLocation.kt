package com.example.mapboxv10_demo.utils


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapLocation(var placeLatitude:Double, var placeLongitude:Double): Parcelable
