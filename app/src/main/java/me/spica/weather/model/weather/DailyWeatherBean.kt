package me.spica.weather.model.weather

import com.qweather.sdk.bean.weather.WeatherDailyBean
import java.text.SimpleDateFormat
import java.util.*


private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)


data class DailyWeatherBean(
    val fxTime: Date = Date(),// 更新时间
    val maxTemp: Int,// 当日最高的温度
    val minTemp: Int,// 当日最低的温度
    val iconId: Int,// 图标
    val windSpeed: Int, // 风速
    val water: Int, // 湿度
    val windPa: Int, // 气压
    val weatherName: String,
    val precip: Int,// 降水量
)


fun WeatherDailyBean.DailyBean.toDailyWeatherBean(): DailyWeatherBean {
    return DailyWeatherBean(
        fxTime = sdf.parse(fxDate) ?: Date(),
        maxTemp = tempMax.toIntOrNull() ?: 0,
        minTemp = tempMin.toIntOrNull() ?: 0,
        iconId = iconDay.toIntOrNull() ?: 0,
        windSpeed = windSpeedDay.toIntOrNull() ?: 0,
        water = humidity.toIntOrNull() ?: 0,
        windPa = pressure.toIntOrNull() ?: 0,
        weatherName = textDay,
        precip = precip.toIntOrNull() ?: 0
    )
}