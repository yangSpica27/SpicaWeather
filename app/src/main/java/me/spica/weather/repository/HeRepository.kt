package me.spica.weather.repository

import android.content.Context
import com.qweather.sdk.bean.weather.WeatherNowBean
import com.qweather.sdk.view.HeContext
import com.qweather.sdk.view.QWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import me.spica.weather.model.weather.NowWeatherBean
import me.spica.weather.model.weather.toNowWeatherBean
import java.util.concurrent.Flow


/**
 * 和风天气源的Repository封装
 */
class HeRepository(private val context: Context) : Repository {


    override fun fetchNowWeather(
        lon: String,
        lat: String,
        onStart: () -> Unit,
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

    }.onStart {
        onStart()
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.IO)


}