package me.spica.weather.repository

import com.skydoves.sandwich.message
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import me.spica.weather.network.hefeng.HeClient
import me.spica.weather.network.hefeng.mapper.SuccessAirMapper
import me.spica.weather.network.hefeng.mapper.SuccessDailyWeatherMapper
import me.spica.weather.network.hefeng.mapper.SuccessHourlyWeatherMapper
import me.spica.weather.network.hefeng.mapper.SuccessLifeIndexWeatherMapper
import me.spica.weather.network.hefeng.mapper.SuccessNowWeatherMapper
import timber.log.Timber

/**
 * 和风天气源的Repository封装
 */
class HeRepository(private val heClient: HeClient) : Repository {

    override fun fetchNowWeather(
        lon: String,
        lat: String,
        onError: (String?) -> Unit
    ) = flow {
        val response = heClient.getNowWeather(
            lon, lat
        )
        response.suspendOnSuccess(SuccessNowWeatherMapper) {
            emit(this)
        }.suspendOnFailure {
            emit(null)
            onError(this)
        }.suspendOnError {
            emit(null)
            onError(message())
        }
    }.flowOn(Dispatchers.IO)


    override fun fetchHourlyWeather(
        lon: String,
        lat: String,
        onError: (String?) -> Unit,
    ) = flow {

        val response = heClient.get24HWeather(
            lon, lat
        )
        response.suspendOnSuccess(SuccessHourlyWeatherMapper) {
            emit(this)
        }.suspendOnFailure {
            emit(null)
            onError(this)
        }.suspendOnError {
            emit(null)
            onError(message())
        }
    }.flowOn(Dispatchers.IO)

    override fun fetchDailyWeather(
        lon: String,
        lat: String,
        onError: (String?) -> Unit,
    ) = flow {

        val response = heClient.get7DWeather(
            lon, lat
        )
        response.suspendOnSuccess(SuccessDailyWeatherMapper) {
            emit(this)
        }.suspendOnFailure {
            emit(null)
            onError(this)
        }.suspendOnError {
            emit(null)
            onError(message())
        }
    }.flowOn(Dispatchers.IO)

    override fun fetchTodayLifeIndex(
        lon: String,
        lat: String,
        onError: (String?) -> Unit,
    ) = flow {

        val response = heClient.getLifeIndex(lon, lat)
        response.suspendOnSuccess(SuccessLifeIndexWeatherMapper) {
            emit(this)
        }.suspendOnFailure {
            emit(null)
            Timber.e(this)
            onError(this)
        }.suspendOnError {
            emit(null)
            Timber.e(message())
            onError(message())
        }
    }.flowOn(Dispatchers.IO)


    override fun fetchNowAir(lon: String, lat: String, onError: (String?) -> Unit) =
        flow {
            val response = heClient.getAirNow(lon, lat)
            response.suspendOnSuccess(SuccessAirMapper) {
                emit(this)
            }.suspendOnFailure {
                emit(null)
                Timber.e(this)
                onError(this)
            }.suspendOnError {
                emit(null)
                Timber.e(message())
                onError(message())
            }
        }.flowOn(Dispatchers.IO)
}
