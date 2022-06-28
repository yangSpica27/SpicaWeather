package me.spica.weather.model.weather

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlertBean(
  val description: String, // 描述
  val warning: String // 警告
)