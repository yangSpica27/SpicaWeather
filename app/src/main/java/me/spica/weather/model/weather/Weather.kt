package me.spica.weather.model.weather

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.squareup.moshi.JsonClass
import me.spica.weather.R
import me.spica.weather.common.WeatherType


@Entity
@TypeConverters(WeatherBeanConverter::class)
@JsonClass(generateAdapter = true)
data class Weather(
  val todayWeather: NowWeatherBean,
  val dailyWeather: List<DailyWeatherBean>,
  val hourlyWeather: List<HourlyWeatherBean>,
  val lifeIndexes: List<LifeIndexBean>,
  val air: AirBean,
  @PrimaryKey(autoGenerate = false)
  var cityName: String = "",
  var descriptionForToday: String? = "", // 今天的气象描述
  var descriptionForToWeek: String? = "", // 一周的气象描述
  var alerts: List<AlertBean> = listOf()
) {


  fun getWeatherType(): WeatherType {
    when (todayWeather.iconId.toString()) {
      "100" -> {
        return WeatherType.WEATHER_CLEAR
      }
      "101", "151", "153", "103" -> {
        return WeatherType.WEATHER_CLOUDY
      }
      "102", "104", "152", "154" -> {
        return WeatherType.WEATHER_CLOUD
      }

      "300", "301", "303", "305", "306",
      "307", "308", "309", "310", "311", "312",
      "314", "315", "316", "317", "318", "350", "351",
      "399"
      -> {
        return WeatherType.WEATHER_RAINY
      }

      "400", "401", "402", "403", "408", "409", "410",
      -> {
        return WeatherType.WEATHER_SNOW
      }
      "404", "405", "406", "456", "457", "499" -> {
        return WeatherType.WEATHER_SLEET
      }
      "500", "501", "502" -> {
        return WeatherType.WEATHER_FOG
      }
      "503", "504", "505", "506", "507", "508", "509", "510",

      "511", "512", "513", "514", "515" -> {
        WeatherType.WEATHER_HAZE
      }
      "313" -> {
        WeatherType.WEATHER_HAIL
      }
      "" -> {
        WeatherType.WEATHER_THUNDER
      }

      "302", "304" -> {
        WeatherType.WEATHER_THUNDERSTORM
      }
    }
    return WeatherType.WEATHER_CLEAR
  }

  @DrawableRes
  fun getWeatherIcon(): Int {
    return when (getWeatherType()) {
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

}