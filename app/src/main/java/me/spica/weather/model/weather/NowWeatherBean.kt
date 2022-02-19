package me.spica.weather.model.weather

import com.qweather.sdk.bean.weather.WeatherNowBean
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

private val sdf = SimpleDateFormat("yyyy-MM-dd'T'mm:HH+08:00", Locale.CHINA)


data class NowWeatherBean(
    val obsTime: Date = Date(),// 更新时间
    val temp: Int,// 当前的温度
    val feelTemp: Int,// 体感温度
    val iconId: Int,// 图标
    val windSpeed: Int, // 风速
    val water: Int, // 湿度
    val windPa: Int, // 气压
    val weatherName:String,
)

fun WeatherNowBean.NowBaseBean.toNowWeatherBean(): NowWeatherBean {
    val updateDate = sdf.parse(this.obsTime) ?: Date()
    return NowWeatherBean(
        obsTime = updateDate,
        temp = this.temp.toIntOrNull() ?: 0,
        feelTemp = feelsLike.toIntOrNull() ?: 0,
        iconId = icon.toIntOrNull() ?: 0,
        windSpeed = windSpeed.toIntOrNull() ?: 0,
        water = humidity.toIntOrNull() ?: 0,
        windPa = pressure.toIntOrNull() ?: 0,
        weatherName = text
    )
}