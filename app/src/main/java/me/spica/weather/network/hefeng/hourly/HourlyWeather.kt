package me.spica.weather.network.hefeng.hourly

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HourlyWeather(
    @Json(name = "code")
    val code: String,
    @Json(name = "fxLink")
    val fxLink: String,
    @Json(name = "hourly")
    val hourly: List<Hourly>,
    @Json(name = "refer")
    val refer: Refer,
    @Json(name = "updateTime")
    val updateTime: String
)
