package me.spica.weather.ui.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import me.spica.weather.model.city.CityBean
import me.spica.weather.model.weather.DailyWeatherBean
import me.spica.weather.model.weather.HourlyWeatherBean
import me.spica.weather.model.weather.LifeIndexBean
import me.spica.weather.model.weather.NowWeatherBean
import me.spica.weather.repository.HeRepository
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: HeRepository
) : ViewModel() {

    // 错误信息
    private val errorMessage = MutableStateFlow("")

    // 经度 纬度
    val cityFlow: MutableStateFlow<CityBean?> = MutableStateFlow(null)

    // 即时天气
    val nowWeatherFlow: Flow<NowWeatherBean> =
        cityFlow
            .filterNotNull()
            .flatMapLatest {
                repository.fetchNowWeather(
                    lon = it.lon,
                    lat = it.lat,
                    onSuccess = {},
                    onStart = {},
                    onError = {},
                    onComplete = {}
                )
            }

    val dailyWeatherFlow: Flow<List<DailyWeatherBean>> =
        cityFlow.filterNotNull().flatMapLatest {
            repository.fetchDailyWeather(
                lon = it.lon,
                lat = it.lat,
                onSuccess = {},
                onStart = {},
                onError = {},
                onComplete = {}
            )
        }

    val hourlyWeatherFlow: Flow<List<HourlyWeatherBean>> =
        cityFlow
            .filterNotNull()
            .flatMapLatest {
                repository.fetchHourlyWeather(
                    lon = it.lon,
                    lat = it.lat,
                    onSuccess = {},
                    onStart = {},
                    onError = {},
                    onComplete = {}
                )
            }

    val currentIndices: Flow<List<LifeIndexBean>> =
        cityFlow
            .filterNotNull()
            .flatMapLatest {
                repository.fetchTodayLifeIndex(
                    lon = it.lon,
                    lat = it.lat,
                    onSuccess = {},
                    onStart = {},
                    onError = {},
                    onComplete = {}
                )
            }

    fun changedCity(cityBean: CityBean) {
        cityFlow.value = cityBean
    }
}
