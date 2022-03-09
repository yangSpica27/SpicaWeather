package me.spica.weather.network.hefeng.air


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Refer(
    @Json(name = "license")
    val license: List<String>,
    @Json(name = "sources")
    val sources: List<String>
)