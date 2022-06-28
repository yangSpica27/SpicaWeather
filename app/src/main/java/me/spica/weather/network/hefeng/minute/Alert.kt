package me.spica.weather.network.hefeng.minute


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Alert(
    @Json(name = "adcodes")
    val adcodes: List<Adcode>,
    @Json(name = "content")
    val content: List<Any>,
    @Json(name = "status")
    val status: String
)