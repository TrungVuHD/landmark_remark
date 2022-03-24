package com.android.landmarks.domain.model

import android.location.Location
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coordinates(
    val lat: Double = 0.0,
    val lng: Double = 0.0
) : Parcelable {
    fun toLatLng() = LatLng(lat, lng)

    override fun toString(): String = formatLatLng(lat, lng)
}

// transform google maps location object to my Coordinates class
fun Location.toCoordinates(): Coordinates = Coordinates(this.latitude, this.longitude)

// get a nicely formatted location to string
fun Location.toDisplayString(): String = formatLatLng(latitude, longitude)

// format a latitude / longitude pair for display
fun formatLatLng(latitude: Double, longitude: Double): String {
    return "(${"%.2f".format(latitude)}, ${"%.2f".format(longitude)})"
}
