package me.spica.weather.model.weather

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import me.spica.weather.common.WeatherCodeUtils
import me.spica.weather.common.WeatherType
import java.text.SimpleDateFormat
import java.util.*

private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
private val sdf2 = SimpleDateFormat("HH:mm", Locale.CHINA)

@Parcelize
@JsonClass(generateAdapter = true)
data class DailyWeatherBean(
  val fxTime: Date = Date(), // 更新时间
  val maxTemp: Int, // 当日最高的温度
  val minTemp: Int, // 当日最低的温度
  val iconId: Int, // 图标
  val windSpeed: Int, // 风速
  val water: Int, // 湿度
  val windPa: Int, // 气压
  val weatherNameDay: String,
  val precip: Float, // 降水量
  val sunriseDate: Date,
  val sunsetDate: Date,
  val moonParse: String,
  val dayWindDir: String,
  val dayWindSpeed: String,
  val nightWindSpeed: String,
  val nightWindDir: String,
  val weatherNameNight: String,
  val pressure: String,
  val uv: String,
  val vis: Int,
  val cloud: Int
) : Parcelable {

  fun getWeatherType(): WeatherType {
    return WeatherCodeUtils.getWeatherCode(iconId)
  }
}

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
fun me.spica.weather.network.hefeng.daily.Daily.toDailyWeatherBean(): DailyWeatherBean {
  return DailyWeatherBean(
    fxTime = sdf.parse(fxDate) ?: Date(),
    maxTemp = tempMax.toIntOrNull() ?: 0,
    minTemp = tempMin.toIntOrNull() ?: 0,
    iconId = iconDay.toIntOrNull() ?: 0,
    windSpeed = windSpeedDay.toIntOrNull() ?: 0,
    water = humidity.toIntOrNull() ?: 0,
    windPa = pressure.toIntOrNull() ?: 0,
    weatherNameDay = textDay,
    precip = precip.toFloatOrNull() ?: 0f,
    sunriseDate = sdf2.parse(sunrise) ?: sdf2.parse("06:00"),
    sunsetDate = sdf2.parse(sunset) ?: sdf2.parse("18:00"),
    moonParse = moonPhase,
    dayWindSpeed = windSpeedDay,
    dayWindDir = windDirDay,
    nightWindDir = windDirNight,
    nightWindSpeed = windSpeedNight,
    weatherNameNight = textNight,
    pressure = pressure,
    uv = uvIndex,
    vis = vis.toIntOrNull() ?: 0,
    cloud = cloud?.toIntOrNull() ?: 0
  )


}
