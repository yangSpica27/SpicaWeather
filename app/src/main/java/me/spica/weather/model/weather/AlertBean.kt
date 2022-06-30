package me.spica.weather.model.weather

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class AlertBean(
  val title: String,
  val description: String,
  val status: String,
  val code: String,
  val source: String
)