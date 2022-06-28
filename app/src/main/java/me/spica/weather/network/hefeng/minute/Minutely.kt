package me.spica.weather.network.hefeng.minute


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Minutely(
    @Json(name = "datasource")
    val datasource: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "precipitation")
    val precipitation: List<Double>,
    @Json(name = "precipitation_2h")
    val precipitation2h: List<Double>,
    @Json(name = "probability")
    val probability: List<Double>,
    @Json(name = "status")
    val status: String
)