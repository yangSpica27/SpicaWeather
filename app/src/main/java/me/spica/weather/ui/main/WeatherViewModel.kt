package me.spica.weather.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import me.spica.weather.model.city.CityBean
import me.spica.weather.model.weather.DailyWeatherBean
import me.spica.weather.model.weather.HourlyWeatherBean
import me.spica.weather.model.weather.LifeIndexBean
import me.spica.weather.model.weather.NowWeatherBean
import me.spica.weather.persistence.repository.CityRepository
import me.spica.weather.repository.HeRepository
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


    private var resultNumer = 0
        set(value) {
            if (value == 4) {
                _isLoading.value = true
            }
            field = value
        }

    // 经度 纬度
    val cityFlow: Flow<CityBean?> =
        cityRepository
            .selectedCityFlow()

    // 即时天气
    val nowWeatherFlow: Flow<NowWeatherBean> =
        cityFlow
            .filterNotNull()
            .flatMapLatest {
                repository.fetchNowWeather(
                    lon = it.lon,
                    lat = it.lat,
                    onStart = {
                        _isLoading.value = true
                    },
                    onError = {
                        _errorMessage.value = it
                    },
                    onComplete = {
                        resultNumer++
                    }
                )
            }

    val dailyWeatherFlow: Flow<List<DailyWeatherBean>> =
        cityFlow.filterNotNull().flatMapLatest { it ->
            repository.fetchDailyWeather(
                lon = it.lon,
                lat = it.lat,
                onStart = {
                    _isLoading.value = true
                },
                onError = { message ->
                    _errorMessage.value = message
                },
                onComplete = {
                    resultNumer++
                }
            )
        }

    val hourlyWeatherFlow: Flow<List<HourlyWeatherBean>> =
        cityFlow
            .filterNotNull()
            .flatMapLatest {
                repository.fetchHourlyWeather(
                    lon = it.lon,
                    lat = it.lat,
                    onStart = {
                        _isLoading.value = true
                    },
                    onError = { message ->
                        _errorMessage.value = message
                    },
                    onComplete = {
                        resultNumer++
                    }
                )
            }

    val currentIndices: Flow<List<LifeIndexBean>> =
        cityFlow
            .filterNotNull()
            .flatMapLatest {
                repository.fetchTodayLifeIndex(
                    lon = it.lon,
                    lat = it.lat,
                    onStart = {
                        _isLoading.value = true
                    },
                    onError = { message ->
                        _errorMessage.value = message
                    },
                    onComplete = {
                        resultNumer++
                    }
                )
            }

    fun changedCity(cityBean: CityBean) {
        resultNumer = 0
        viewModelScope.launch(Dispatchers.IO) {
            cityRepository.selected(cityBean)
        }
    }
}
