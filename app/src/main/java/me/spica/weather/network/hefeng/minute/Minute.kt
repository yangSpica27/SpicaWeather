package me.spica.weather.network.hefeng.minute


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Minute(
    @Json(name = "api_status")
    val apiStatus: String,
    @Json(name = "api_version")
    val apiVersion: String,
    @Json(name = "lang")
    val lang: String,
    @Json(name = "location")
    val location: List<Double>,
    @Json(name = "result")
    val result: Result,
    @Json(name = "server_time")
    val serverTime: Int,
    @Json(name = "status")
    val status: String,
    @Json(name = "timezone")
    val timezone: String,
    @Json(name = "tzshift")
    val tzshift: Int,
    @Json(name = "unit")
    val unit: String
)