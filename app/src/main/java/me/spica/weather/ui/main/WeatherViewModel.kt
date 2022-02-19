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

    private val gson: Gson = Gson()

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



    private val _7dayWeatherFlow: MutableStateFlow<WeatherDailyBean?> =
        MutableStateFlow(null)

    private val _currentDayHourWeather: MutableStateFlow<WeatherHourlyBean?> =
        MutableStateFlow(null)

    val currentDayHourWeather: StateFlow<WeatherHourlyBean?>
        get() = _currentDayHourWeather

    val dailyWeatherFlow: MutableStateFlow<WeatherDailyBean?>
        get() = _7dayWeatherFlow

    private val _currentIndices: MutableStateFlow<List<IndicesBean.DailyBean?>> = MutableStateFlow(listOf())

    val currentIndices: MutableStateFlow<List<IndicesBean.DailyBean?>>
        get() = _currentIndices


    // 获取现在的天气
    fun syncNowWeather(context: Context, entry: Pair<String, String>) {

        repository.fetchNowWeather(
            entry.first,
            entry.second,
            onComplete = {},
            onError = {},
            onStart = {},
            onSuccess = {}
        )

    }

    // 获取一天内的生活指数
    fun sync1DIndices(context: Context, entry: Pair<String, String>) {
        viewModelScope.launch {
            QWeather.getIndices1D(
                context,
                "${entry.first},${entry.second}",
                Lang.ZH_HANS,
                listOf(IndicesType.ALL),
                object : QWeather.OnResultIndicesListener {
                    override fun onError(error: Throwable) {
                        errorMessage.value = error.message ?: ""
                        Timber.e(error)
                    }

                    override fun onSuccess(result: IndicesBean) {
                        Timber.e(gson.toJson(result))
                        _currentIndices.value = result.dailyList
                    }
                }
            )
        }
    }


    fun sync7DayWeather(context: Context, entry: Pair<String, String>) {
        viewModelScope.launch {
            QWeather.getWeather7D(context, "${entry.first},${entry.second}",
                object : QWeather.OnResultWeatherDailyListener {
                    override fun onError(error: Throwable) {
                        errorMessage.value = error.message ?: ""
                    }

                    override fun onSuccess(result: WeatherDailyBean) {
                        Timber.e(gson.toJson(result))
                        _7dayWeatherFlow.value = result
                    }

                }
            )
        }
    }


    fun sync24hWeather(context: Context, entry: Pair<String, String>) {
        viewModelScope.launch {
            QWeather.getWeather24Hourly(context, "${entry.first},${entry.second}",
                object : QWeather.OnResultWeatherHourlyListener {
                    override fun onError(error: Throwable) {
                        errorMessage.value = error.message ?: ""
                    }

                    override fun onSuccess(result: WeatherHourlyBean) {
                        Timber.e(gson.toJson(result))
                        _currentDayHourWeather.value = result
                    }

                })
        }
    }


}