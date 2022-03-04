package me.spica.weather.model.weather

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

private val sdf = SimpleDateFormat("yyyy-MM-dd'T'mm:HH+08:00", Locale.CHINA)

@JsonClass(generateAdapter = true)
@Parcelize
data class NowWeatherBean(
    val obsTime: Date = Date(), // 更新时间
    val temp: Int, // 当前的温度
    val feelTemp: Int, // 体感温度
    val iconId: Int, // 图标
    val windSpeed: Int, // 风速
    val water: Int, // 湿度
    val windPa: Int, // 气压
    val weatherName: String,
) : Parcelable

fun me.spica.weather.network.hefeng.now.Now.toNowWeatherBean(): NowWeatherBean {
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
