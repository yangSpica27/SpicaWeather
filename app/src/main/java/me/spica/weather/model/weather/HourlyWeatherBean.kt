package me.spica.weather.model.weather

import com.squareup.moshi.JsonClass
import me.spica.weather.common.WeatherCodeUtils
import me.spica.weather.common.WeatherType
import java.text.SimpleDateFormat
import java.util.*

private val sdf = SimpleDateFormat(
  "yyyy-MM-dd'T'mm:HH+08:00",
  Locale.CHINA
)

@JsonClass(generateAdapter = true)
data class HourlyWeatherBean(
  val fxTime: String = "", // 更新时间
  val temp: Int, // 当前的温度
  val iconId: Int, // 图标
  val windSpeed: Int, // 风速
  val water: Int, // 湿度
  val windPa: Int, // 气压
  val weatherName: String,
  val pop: Int, // 降雨概率
) {

  fun getWeatherType(): WeatherType {
    return WeatherCodeUtils.getWeatherCode(iconId)
  }

  fun fxTime(): Date {
    try {
      sdf.parse(fxTime) ?: Date()
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return Date()
  }
}

fun me.spica.weather.network.hefeng.hourly.Hourly.toHourlyWeatherBean(): HourlyWeatherBean {


  return HourlyWeatherBean(
    fxTime = this.fxTime,
    temp = temp.toIntOrNull() ?: 0,
    iconId = icon.toIntOrNull() ?: 0,
    windSpeed = windSpeed.toIntOrNull() ?: 0,
    water = humidity.toIntOrNull() ?: 0,
    windPa = pressure.toIntOrNull() ?: 0,
    weatherName = text,
    pop = pop.toIntOrNull() ?: 0
  )
}
