package me.spica.weather.network.hefeng

import com.skydoves.sandwich.ApiResponse
import me.spica.weather.model.BaseResponse
import me.spica.weather.model.weather.Weather
import me.spica.weather.network.caiyun.CaiyunBean
import me.spica.weather.network.hefeng.air.Air
import me.spica.weather.network.hefeng.daily.DailyWeather
import me.spica.weather.network.hefeng.hourly.HourlyWeather
import me.spica.weather.network.hefeng.index.LifeIndex
import me.spica.weather.network.hefeng.now.NowWeather
import javax.inject.Inject

@Suppress("unused")
class HeClient @Inject constructor(private val heService: HeService) {

  // 获取当前的天气信息
  suspend fun getNowWeather(
    lon: String,
    lat: String
  ): ApiResponse<NowWeather> = heService.getNowWeather(
    location = "$lon,$lat"
  )

  // 获取近7天的天气信息
  suspend fun get7DWeather(
    lon: String,
    lat: String
  ): ApiResponse<DailyWeather> = heService.get7DayWeather(
    location = "$lon,$lat"
  )

  // 获取24小时的天气信息
  suspend fun get24HWeather(
    lon: String,
    lat: String
  ): ApiResponse<HourlyWeather> = heService.get24HWeather(
    location = "$lon,$lat"
  )

  // 获取生活指数
  suspend fun getLifeIndex(
    lon: String,
    lat: String
  ): ApiResponse<LifeIndex> = heService.get1dIndex(
    location = "$lon,$lat"
  )

  // 获取空气质量信息
  suspend fun getAirNow(
    lon: String,
    lat: String
  ): ApiResponse<Air> = heService.getNowAir(
    location = "$lon,$lat"
  )


  // 聚合接口
  suspend fun getAllWeather(
    lon: String,
    lat: String
  ): ApiResponse<BaseResponse<Weather>> = heService.getAllWeather(
    location = "$lon,$lat"
  )

  // 获取分钟级别天气和预警信息
  suspend fun getMinute(
    lon: String,
    lat: String
  ): ApiResponse<CaiyunBean> = heService.getMinutely(location = "$lon,$lat")
}
