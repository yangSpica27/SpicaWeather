package me.spica.weather.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import me.spica.weather.persistence.dao.CityDao
import me.spica.weather.persistence.dao.WeatherDao
import me.spica.weather.repository.HeRepository

/**
 * 天气信息同步任务
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val weatherDao: WeatherDao,
    private val heRepository: HeRepository,
    private val cityDao: CityDao
) : CoroutineWorker(appContext, workerParams) {


    override suspend fun doWork(): Result {

        cityDao.getAllList().forEach {
            val weatherFlow = heRepository.fetchWeather(it.lon, it.lat, {}).collect()

            val caiyunExt = heRepository.fetchCaiyunExtend(it.lon, it.lat, {}).collect()




        }

        return Result.success()
    }



}