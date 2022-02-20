package me.spica.weather.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.qweather.sdk.bean.IndicesBean
import com.qweather.sdk.bean.base.IndicesType
import com.qweather.sdk.bean.base.Lang
import com.qweather.sdk.bean.weather.WeatherDailyBean
import com.qweather.sdk.bean.weather.WeatherHourlyBean
import com.qweather.sdk.bean.weather.WeatherNowBean
import com.qweather.sdk.view.QWeather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import me.spica.weather.model.weather.DailyWeatherBean
import me.spica.weather.model.weather.HourlyWeatherBean
import me.spica.weather.model.weather.LifeIndexBean
import me.spica.weather.model.weather.NowWeatherBean
import me.spica.weather.repository.HeRepository
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: HeRepository
) : ViewModel() {

    // 错误信息
    private val errorMessage = MutableStateFlow("")


    private val locationKeys = MutableStateFlow(
        Pair(
            "118.78",
            "32.04"
        )
    )


    // 即时天气
    val nowWeatherFlow: Flow<NowWeatherBean> =
        locationKeys.flatMapLatest {
            repository.fetchNowWeather(
                lon = it.first,
                lat = it.second,
                onSuccess = {},
                onStart = {},
                onError = {},
                onComplete = {}
            )
        }


    val dailyWeatherFlow: Flow<List<DailyWeatherBean>> = locationKeys.flatMapLatest {
        repository.fetchDailyWeather(
            lon = it.first,
            lat = it.second,
            onSuccess = {},
            onStart = {},
            onError = {},
            onComplete = {}
        )
    }

    val hourlyWeatherFlow: Flow<List<HourlyWeatherBean>> =
        locationKeys.flatMapLatest {
            repository.fetchHourlyWeather(
                lon = it.first,
                lat = it.second,
                onSuccess = {},
                onStart = {},
                onError = {},
                onComplete = {}
            )
        }


    val currentIndices: Flow<List<LifeIndexBean>> =
        locationKeys.flatMapLatest {
            repository.fetchTodayLifeIndex(
                lon = it.first,
                lat = it.second,
                onSuccess = {},
                onStart = {},
                onError = {},
                onComplete = {}
            )
        }


    fun changedCity(lon: String, lat: String) {
        locationKeys.value = Pair(lon, lat)
    }


}