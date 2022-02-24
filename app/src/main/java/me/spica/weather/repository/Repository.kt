package me.spica.weather.repository

import me.spica.weather.model.weather.DailyWeatherBean
import me.spica.weather.model.weather.HourlyWeatherBean
import me.spica.weather.model.weather.LifeIndexBean
import me.spica.weather.model.weather.NowWeatherBean

interface Repository {

    // 获取当日天气
    fun fetchNowWeather(
        lon: String,
        lat: String,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
        onSuccess: () -> Unit
    ): kotlinx.coroutines.flow.Flow<NowWeatherBean>

    // 获取近几小时的天气
    fun fetchHourlyWeather(
        lon: String,
        lat: String,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
        onSuccess: () -> Unit
    ): kotlinx.coroutines.flow.Flow<List<HourlyWeatherBean>>

    // 获取近几天的天气
    fun fetchDailyWeather(
        lon: String,
        lat: String,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
        onSuccess: () -> Unit
    ): kotlinx.coroutines.flow.Flow<List<DailyWeatherBean>>

    // 获取今天的生活指数
    fun fetchTodayLifeIndex(
        lon: String,
        lat: String,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
        onSuccess: () -> Unit
    ): kotlinx.coroutines.flow.Flow<List<LifeIndexBean>>
}
