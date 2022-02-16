package me.spica.weather.model.city


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class City(
    @Json(name = "lat")// 经度
    val lat: String,
    @Json(name = "log")// 纬度
    val log: String,
    @Json(name = "name")// 名称
    val name: String
)