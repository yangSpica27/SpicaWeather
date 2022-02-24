package me.spica.weather.network.hefeng

import com.skydoves.sandwich.ApiResponse
import me.spica.weather.network.hefeng.daily.DailyWeather
import me.spica.weather.network.hefeng.hourly.HourlyWeather
import me.spica.weather.network.hefeng.index.LifeIndex
import me.spica.weather.network.hefeng.now.NowWeather
import javax.inject.Inject

@Suppress("unused")
class HeClient @Inject constructor(private val heService: HeService) {


    suspend fun getNowWeather(
        lon: String,
        lat: String
    ): ApiResponse<NowWeather> = heService.getNowWeather(
        location = "${lon},${lat}"
    )

    suspend fun get7DWeather(
        lon: String,
        lat: String
    ): ApiResponse<DailyWeather> = heService.get7DayWeather(
        location = "${lon},${lat}"
    )

    suspend fun get24HWeather(
        lon: String,
        lat: String
    ): ApiResponse<HourlyWeather> = heService.get24HWeather(
        location = "${lon},${lat}"
    )

    suspend fun getLifeIndex(
        lon: String,
        lat: String
    ): ApiResponse<LifeIndex> = heService.get1dIndex(
        location = "${lon},${lat}"
    )

}