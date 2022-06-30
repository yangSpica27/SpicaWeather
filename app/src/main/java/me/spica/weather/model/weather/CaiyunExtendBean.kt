package me.spica.weather.model.weather

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CaiyunExtendBean(
  val alerts: List<AlertBean>,
  val description: String,
  val forecastKeypoint: String
)