package me.spica.weather.model.city


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class City(
    @Json(name = "lat")
    val lat: String,
    @Json(name = "log")
    val log: String,
    @Json(name = "name")
    val name: String
)