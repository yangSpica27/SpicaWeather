package me.spica.weather.repository

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


}