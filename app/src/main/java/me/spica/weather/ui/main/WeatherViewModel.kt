package me.spica.weather.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import me.spica.weather.model.city.CityBean
import me.spica.weather.model.weather.DailyWeatherBean
import me.spica.weather.model.weather.HourlyWeatherBean
import me.spica.weather.model.weather.LifeIndexBean
import me.spica.weather.model.weather.NowWeatherBean
import me.spica.weather.model.weather.Weather
import me.spica.weather.persistence.repository.CityRepository
import me.spica.weather.repository.HeRepository
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: HeRepository,
    private val cityRepository: CityRepository
) : ViewModel() {

    // 错误信息
    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)

    private val _isLoading = MutableStateFlow(false)

    val isLoading: Flow<Boolean> = _isLoading

    val errorMessage: Flow<String?> = _errorMessage.filterNotNull()



    // 经度 纬度
    val cityFlow: Flow<CityBean?> =
        cityRepository
            .selectedCityFlow()


    // 即时天气
    val nowWeatherFlow: Flow<NowWeatherBean?> =
        cityFlow
            .filterNotNull()
            .flatMapLatest {
                repository.fetchNowWeather(
                    lon = it.lon,
                    lat = it.lat,
                    onError = { message ->
                        _errorMessage.value = message
                    }
                )
            }


    val dailyWeatherFlow: Flow<List<DailyWeatherBean>?> =
        cityFlow.filterNotNull().flatMapLatest { it ->
            repository.fetchDailyWeather(
                lon = it.lon,
                lat = it.lat,
                onError = { message ->
                    _errorMessage.value = message
                }
            )
        }

    val hourlyWeatherFlow: Flow<List<HourlyWeatherBean>?> =
        cityFlow
            .filterNotNull()
            .flatMapLatest {
                repository.fetchHourlyWeather(
                    lon = it.lon,
                    lat = it.lat,
                    onError = { message ->
                        _errorMessage.value = message
                    }
                )
            }

    val currentIndices: Flow<List<LifeIndexBean>?> =
        cityFlow
            .filterNotNull()
            .flatMapLatest {
                repository.fetchTodayLifeIndex(
                    lon = it.lon,
                    lat = it.lat,
                    onError = { message ->
                        _errorMessage.value = message
                    }
                )
            }


    val weatherFlow = combine(
        nowWeatherFlow,
        dailyWeatherFlow,
        hourlyWeatherFlow,
        currentIndices,
    ) { nowWeather, dailyWeather, hourWeather, lifeIndexes ->
        if (nowWeather != null
            && dailyWeather != null
            && hourWeather != null &&
            lifeIndexes != null
        )
            return@combine Weather(
                nowWeather,
                dailyWeather,
                hourWeather,
                lifeIndexes
            )

        if (nowWeather==null){
            Timber.e("now==nu;;")
        }
        if (dailyWeather==null){
            Timber.e("daily==null")
        }
        if (hourWeather==null){
            Timber.e("hourWeather==null")
        }
        if (lifeIndexes==null){
            Timber.e("lifeIndex==null")
        }
        return@combine null
    }.onStart {
        _isLoading.value = true
    }.onCompletion {
        _isLoading.value = false
    }

    fun changedCity(cityBean: CityBean) {

        viewModelScope.launch(Dispatchers.IO) {
            cityRepository.selected(cityBean)
        }
    }
}
