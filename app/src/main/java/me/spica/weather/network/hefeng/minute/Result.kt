package me.spica.weather.network.hefeng.minute


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Result(
    @Json(name = "alert")
    val alert: Alert,
    @Json(name = "forecast_keypoint")
    val forecastKeypoint: String,
    @Json(name = "minutely")
    val minutely: Minutely,
    @Json(name = "primary")
    val primary: Int
)