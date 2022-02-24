package me.spica.weather.network.hefeng.daily


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DailyWeather(
    @Json(name = "code")
    val code: String,
    @Json(name = "daily")
    val daily: List<Daily>,
    @Json(name = "fxLink")
    val fxLink: String,
    @Json(name = "refer")
    val refer: Refer,
    @Json(name = "updateTime")
    val updateTime: String
)