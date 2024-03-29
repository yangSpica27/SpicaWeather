package me.spica.weather.common

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import me.spica.weather.R
import me.spica.weather.view.weather_bg.NowWeatherView

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

//// 拓展方法 用于获取对应类型的图标
//@DrawableRes
//fun WeatherType.getIconRes(): Int {
//
//    return when (this) {
//        WeatherType.WEATHER_CLEAR -> R.drawable.ic_sunny
//        WeatherType.WEATHER_CLOUDY -> R.drawable.ic_cloudly
//        WeatherType.WEATHER_CLOUD -> R.drawable.ic_cloudly
//        WeatherType.WEATHER_RAINY -> R.drawable.ic_rain
//        WeatherType.WEATHER_SNOW -> R.drawable.ic_snow
//        WeatherType.WEATHER_SLEET -> R.drawable.ic_rain
//        WeatherType.WEATHER_FOG -> R.drawable.ic_fog
//        WeatherType.WEATHER_HAZE -> R.drawable.ic_fog
//        WeatherType.WEATHER_HAIL -> R.drawable.ic_rain
//        WeatherType.WEATHER_THUNDER -> R.drawable.ic_thumb
//        WeatherType.WEATHER_THUNDERSTORM -> R.drawable.ic_rain_thumb
//    }
//}

@RawRes
fun WeatherType.getAnimRes(): Int {
    return when (this) {
        WeatherType.WEATHER_CLEAR -> R.raw.sunny
        WeatherType.WEATHER_CLOUDY -> R.raw.windy
        WeatherType.WEATHER_CLOUD -> R.raw.foggy
        WeatherType.WEATHER_RAINY -> R.raw.shower
        WeatherType.WEATHER_SNOW -> R.raw.snow
        WeatherType.WEATHER_SLEET -> R.raw.shower
        WeatherType.WEATHER_FOG -> R.raw.foggy
        WeatherType.WEATHER_HAZE -> R.raw.mist
        WeatherType.WEATHER_HAIL -> R.raw.shower
        WeatherType.WEATHER_THUNDER -> R.raw.storm
        WeatherType.WEATHER_THUNDERSTORM -> R.raw.storm
    }
}

@ColorInt
fun WeatherType.getThemeColor(): Int {
    return when (this) {
        WeatherType.WEATHER_CLEAR -> Color.parseColor("#FFC107")
        WeatherType.WEATHER_CLOUDY -> Color.parseColor("#03A9F4")
        WeatherType.WEATHER_CLOUD -> Color.parseColor("#2196F3")
        WeatherType.WEATHER_THUNDER -> Color.parseColor("#3F51B5")
        WeatherType.WEATHER_FOG -> Color.parseColor("#5A5A5A")
        WeatherType.WEATHER_HAZE -> Color.parseColor("#FF5722")
        else -> Color.parseColor("#00BCD4")
    }
}

fun WeatherType.getWeatherAnimType(): NowWeatherView.WeatherAnimType {
    return when (this) {
        WeatherType.WEATHER_CLEAR -> NowWeatherView.WeatherAnimType.SUNNY
        WeatherType.WEATHER_CLOUDY -> NowWeatherView.WeatherAnimType.CLOUDY
        WeatherType.WEATHER_CLOUD -> NowWeatherView.WeatherAnimType.CLOUDY
        WeatherType.WEATHER_RAINY -> NowWeatherView.WeatherAnimType.RAIN
        WeatherType.WEATHER_SNOW -> NowWeatherView.WeatherAnimType.SNOW
        WeatherType.WEATHER_SLEET -> NowWeatherView.WeatherAnimType.RAIN
        WeatherType.WEATHER_FOG -> NowWeatherView.WeatherAnimType.FOG
        WeatherType.WEATHER_HAZE -> NowWeatherView.WeatherAnimType.HAZE
        WeatherType.WEATHER_HAIL -> NowWeatherView.WeatherAnimType.RAIN
        WeatherType.WEATHER_THUNDER -> NowWeatherView.WeatherAnimType.RAIN
        WeatherType.WEATHER_THUNDERSTORM -> NowWeatherView.WeatherAnimType.RAIN
    }
}
