package me.spica.weather.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.spica.weather.model.city.CityBean
import me.spica.weather.model.weather.*
import me.spica.weather.persistence.dao.WeatherDao
import me.spica.weather.repository.HeRepository
import timber.log.Timber
import javax.inject.Inject


@Suppress("unused")
@HiltViewModel
class WeatherViewModel @Inject constructor(
  private val repository: HeRepository,
  private val weatherDao: WeatherDao
) : ViewModel() {

  private val cityFlow: MutableStateFlow<CityBean?> = MutableStateFlow(null)

  // 设置城市


  fun changeCity(cityBean: CityBean) {
    viewModelScope.launch {
      cityFlow.value = CityBean(
        cityName = cityBean.cityName,
        sortName = cityBean.sortName,
        lon = cityBean.lon,
        lat = cityBean.lat,
        isSelected = cityBean.isSelected
      )
    }
  }

  private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)

  private val _isLoading = MutableStateFlow(false)

  // 即时天气
  private val nowWeatherFlow: Flow<NowWeatherBean?> =
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


  private val dailyWeatherFlow: Flow<List<DailyWeatherBean>?> =
    cityFlow.filterNotNull().flatMapLatest {
      repository.fetchDailyWeather(
        lon = it.lon,
        lat = it.lat,
        onError = { message ->
          _errorMessage.value = message
        }
      )
    }

  private val hourlyWeatherFlow: Flow<List<HourlyWeatherBean>?> =
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

  private val currentIndices: Flow<List<LifeIndexBean>?> =
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

  private val nowAir: Flow<AirBean?> =
    cityFlow
      .filterNotNull()
      .flatMapLatest {
        repository.fetchNowAir(
          lon = it.lon,
          lat = it.lat,
          onError = { message ->
            _errorMessage.value = message
          }
        )
      }

  private val alert: Flow<AlertBean?> = cityFlow
    .filterNotNull()
    .flatMapLatest {
      Timber.e("触发获取Alert")
      return@flatMapLatest repository.fetchMinute(
        lon = it.lon,
        lat = it.lat,
        onError = { message ->
          _errorMessage.value = message
        }
      )
    }

  val weatherCacheFlow = cityFlow.filterNotNull().flatMapLatest {
    weatherDao.getWeatherFlowDistinctUntilChanged(it.cityName)
  }

  // 和风系 接口
  val weatherFlow = combine(
    nowWeatherFlow,// 请求获取当前天气
    dailyWeatherFlow,// 请求获取日级别天气
    hourlyWeatherFlow,// 请求获取小时级别天气
    currentIndices,// 获取天气指数
    nowAir,// 获取空气信息
  ) { nowWeather, dailyWeather, hourWeather, lifeIndexes, nowAir ->
    if (nowWeather != null
      && dailyWeather != null
      && hourWeather != null &&
      lifeIndexes != null &&
      nowAir != null
    ) {
      // 所有请求结束后拼接 结果合并
      return@combine Weather(
        nowWeather,
        dailyWeather,
        hourWeather,
        lifeIndexes,
        nowAir,
        cityFlow.value?.cityName ?: ""
      )
    }
    // 请求错误给null 给前端显示是否重试
    return@combine null
  }
    .onStart {
      _isLoading.value = true
    }.onCompletion {
      _isLoading.value = false
    }


  init {
    viewModelScope.launch(Dispatchers.IO) {
      // 合并和风系接口和彩云系列接口的数据
      combine(weatherFlow, alert) { weather, alertBean ->
        kotlin.run {
          // 和风天气的天气数据+彩云的天气预警贴士(彩云接口请求失败不影响显示)
          weather?.descriptionForToday = alertBean?.description ?: ""
          weather
        }
      }.collectLatest {
        viewModelScope.launch(Dispatchers.IO) {
          if (it != null) {
            // 将最后的数据载入缓存
            weatherDao.insertWeather(it)
          }
        }
      }
    }
  }

}