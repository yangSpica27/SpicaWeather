package me.spica.weather.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.qweather.sdk.bean.IndicesBean
import com.qweather.sdk.bean.base.IndicesType
import com.qweather.sdk.bean.base.Lang
import com.qweather.sdk.bean.weather.WeatherDailyBean
import com.qweather.sdk.bean.weather.WeatherNowBean
import com.qweather.sdk.view.QWeather
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber


class MainViewModel : ViewModel() {

    private val errorMessage = MutableStateFlow("")

    private val gson: Gson = Gson()

    private val _nowWeatherFlow: MutableStateFlow<WeatherNowBean?> =
        MutableStateFlow(null)

    val nowWeatherFlow: StateFlow<WeatherNowBean?>
        get() = _nowWeatherFlow

    private val _7dayWeatherFlow: MutableStateFlow<WeatherDailyBean?> =
        MutableStateFlow(null)

    val dailyWeatherFlow: MutableStateFlow<WeatherDailyBean?>
        get() = _7dayWeatherFlow

    private val _currentIndices: MutableStateFlow<List<IndicesBean.DailyBean?>> = MutableStateFlow(listOf())

    val currentIndices: MutableStateFlow<List<IndicesBean.DailyBean?>>
        get() = _currentIndices


    // 获取现在的天气
    fun syncNowWeather(context: Context, entry: Pair<String, String>) {
        viewModelScope.launch {
            QWeather.getWeatherNow(context, "${entry.first},${entry.second}",
                object : QWeather.OnResultWeatherNowListener {
                    override fun onError(error: Throwable) {
                        errorMessage.value = error.message ?: ""
                    }

                    override fun onSuccess(result: WeatherNowBean) {
                        Timber.e(gson.toJson(result))
                        _nowWeatherFlow.value = result
                    }
                })
        }
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


}