package me.spica.weather.model.weather

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters


@Entity
@TypeConverters(WeatherBeanConverter::class)
data class Weather(
    val todayWeather: NowWeatherBean,
    val dailyWeather: List<DailyWeatherBean>,
    val hourlyWeather: List<HourlyWeatherBean>,
    val lifeIndexes: List<LifeIndexBean>,
    val air: AirBean,
    @PrimaryKey(autoGenerate = false)
    val id: Long = 1L
)