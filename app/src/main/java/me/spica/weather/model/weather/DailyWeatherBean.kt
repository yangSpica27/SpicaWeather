package me.spica.weather.model.weather

import java.text.SimpleDateFormat
import java.util.*

private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
private val sdf2 = SimpleDateFormat("HH:mm", Locale.CHINA)
data class DailyWeatherBean(
    val fxTime: Date = Date(), // 更新时间
    val maxTemp: Int, // 当日最高的温度
    val minTemp: Int, // 当日最低的温度
    val iconId: Int, // 图标
    val windSpeed: Int, // 风速
    val water: Int, // 湿度
    val windPa: Int, // 气压
    val weatherName: String,
    val precip: Int, // 降水量
    val sunriseDate: Date,
    val sunsetDate: Date,
    val moonParse: String
)

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
        weatherName = textDay,
        precip = precip.toIntOrNull() ?: 0,
        sunriseDate = sdf2.parse(sunrise) ?: sdf2.parse("06:00"),
        sunsetDate = sdf2.parse(sunset) ?: sdf2.parse("18:00"),
        moonParse = moonPhase
    )
}
