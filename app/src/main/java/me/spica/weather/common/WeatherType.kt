package me.spica.weather.common

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import me.spica.weather.R

enum class WeatherType {
    WEATHER_CLEAR,
    WEATHER_CLOUDY,
    WEATHER_CLOUD,
    WEATHER_RAINY,
    WEATHER_SNOW,
    WEATHER_SLEET,
    WEATHER_FOG,
    WEATHER_HAZE,
    WEATHER_HAIL,
    WEATHER_THUNDER,
    WEATHER_THUNDERSTORM
}

// 拓展方法 用于获取对应类型的图标
@DrawableRes
fun WeatherType.getIconRes(): Int {

    return when (this) {
        WeatherType.WEATHER_CLEAR -> R.drawable.ic_sunny
        WeatherType.WEATHER_CLOUDY -> R.drawable.ic_cloudly
        WeatherType.WEATHER_CLOUD -> R.drawable.ic_cloudly
        WeatherType.WEATHER_RAINY -> R.drawable.ic_rain
        WeatherType.WEATHER_SNOW -> R.drawable.ic_snow
        WeatherType.WEATHER_SLEET -> R.drawable.ic_rain
        WeatherType.WEATHER_FOG -> R.drawable.ic_fog
        WeatherType.WEATHER_HAZE -> R.drawable.ic_fog
        WeatherType.WEATHER_HAIL -> R.drawable.ic_rain
        WeatherType.WEATHER_THUNDER -> R.drawable.ic_thumb
        WeatherType.WEATHER_THUNDERSTORM -> R.drawable.ic_rain_thumb
    }
}

@ColorInt
fun WeatherType.getThemeColor(): Int {
    return when (this) {
        WeatherType.WEATHER_CLOUDY
        -> Color.parseColor("#7986CB")
        WeatherType.WEATHER_CLOUD -> Color.parseColor("#2196F3")
        WeatherType.WEATHER_THUNDER -> Color.parseColor("#3F51B5")
        WeatherType.WEATHER_FOG
        -> Color.parseColor("#5A5A5A")
        WeatherType.WEATHER_HAZE -> Color.parseColor("#424242")
        else -> {
            Color.parseColor("#7986CB")
        }
    }
}
