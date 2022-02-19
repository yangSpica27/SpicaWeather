package me.spica.weather.repository

import android.content.Context
import com.qweather.sdk.bean.weather.WeatherDailyBean
import com.qweather.sdk.bean.weather.WeatherHourlyBean
import com.qweather.sdk.bean.weather.WeatherNowBean
import com.qweather.sdk.view.QWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import me.spica.weather.model.weather.toDailyWeatherBean
import me.spica.weather.model.weather.toHourlyWeatherBean
import me.spica.weather.model.weather.toNowWeatherBean


/**
 * 和风天气源的Repository封装
 */
class HeRepository(private val context: Context) : Repository {


    override fun fetchNowWeather(
        lon: String,
        lat: String,
        onStart: () -> Unit  ,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
        onSuccess: () -> Unit
    ) = callbackFlow {
        onStart()
        QWeather.getWeatherNow(
            context,
            "${lon},${lat}",
            object : QWeather.OnResultWeatherNowListener {
                override fun onError(error: Throwable) {
                    onError(error.message)
                    close(error)
                }

                override fun onSuccess(result: WeatherNowBean) {
                    trySend(result.now.toNowWeatherBean())
                    onSuccess()
                }
            }
        )
        awaitClose {  }
    }.onStart {
        onStart()
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.IO)

    override fun fetchHourlyWeather(
        lon: String,
        lat: String,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
        onSuccess: () -> Unit
    ) = callbackFlow {
        QWeather.getWeather24Hourly(context,
            "${lon},${lat}",
            object : QWeather.OnResultWeatherHourlyListener {
                override fun onError(error: Throwable) {
                    onError(error.message)
                    close(error)
                }

                override fun onSuccess(result: WeatherHourlyBean) {
                    trySendBlocking(result.hourly.map {
                        it.toHourlyWeatherBean()
                    })
                }

            })
        awaitClose {  }
    }.onStart {
        onStart()
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.IO)


    override fun fetchDailyWeather(
        lon: String,
        lat: String,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
        onSuccess: () -> Unit
    ) = callbackFlow {
        QWeather.getWeather7D(context, "${lon},${lat}",
            object : QWeather.OnResultWeatherDailyListener {
                override fun onError(error: Throwable) {
                    close(error)
                    onError(error.message)
                }

                override fun onSuccess(result: WeatherDailyBean) {
                    trySendBlocking(result.daily.map {
                        it.toDailyWeatherBean()
                    })
                }

            }
        )
        awaitClose {  }
    }.onStart {
        onStart()
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.IO)


}