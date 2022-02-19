package me.spica.weather.model.weather

import com.qweather.sdk.bean.weather.WeatherHourlyBean
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date


private val sdf = SimpleDateFormat(
    "yyyy-MM-dd'T'mm:HH+08:00",
    Locale.CHINA
)

data class HourlyWeatherBean(
    val fxTime: Date = Date(),// 更新时间
    val temp: Int,// 当前的温度
    val iconId: Int,// 图标
    val windSpeed: Int, // 风速
    val water: Int, // 湿度
    val windPa: Int, // 气压
    val weatherName: String
)

fun WeatherHourlyBean.HourlyBean.toHourlyWeatherBean(): HourlyWeatherBean {
    return HourlyWeatherBean(
        fxTime = sdf.parse(fxTime) ?: Date(),
        temp = temp.toIntOrNull() ?: 0,
        iconId = icon.toIntOrNull() ?: 0,
        windSpeed = windSpeed.toIntOrNull() ?: 0,
        water = humidity.toIntOrNull() ?: 0,
        windPa = pressure.toIntOrNull() ?: 0,
        weatherName = text
    )
}
