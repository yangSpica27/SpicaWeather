package me.spica.weather.network.caiyun


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Adcode(
    @Json(name = "adcode")
    val adcode: Int,
    @Json(name = "name")
    val name: String
)