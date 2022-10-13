package me.spica.weather.network.hefeng

import com.skydoves.sandwich.ApiResponse
import me.spica.weather.BuildConfig
import me.spica.weather.model.BaseResponse
import me.spica.weather.model.weather.Weather
import me.spica.weather.network.caiyun.CaiyunBean
import me.spica.weather.network.hefeng.air.Air
import me.spica.weather.network.hefeng.daily.DailyWeather
import me.spica.weather.network.hefeng.hourly.HourlyWeather
import me.spica.weather.network.hefeng.index.LifeIndex
import me.spica.weather.network.hefeng.now.NowWeather
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@Suppress("unused")
interface HeService {

  /**
   * 获取当前的天气
   */
  @GET("weather/now")
  suspend fun getNowWeather(
    @Query("location")
    location: String,
    @Query("key")
    key: String = BuildConfig.HE_WEB_KEY
  ): ApiResponse<NowWeather>

  /**
   * 获取一周内的天气
   */
  @GET("weather/7d")
  suspend fun get7DayWeather(
    @Query("location")
    location: String,
    @Query("key")
    key: String = BuildConfig.HE_WEB_KEY
  ): ApiResponse<DailyWeather>

  /**
   * 获取24h内的天气
   */
  @GET("weather/24h")
  suspend fun get24HWeather(
    @Query("location")
    location: String,
    @Query("key")
    key: String = BuildConfig.HE_WEB_KEY
  ): ApiResponse<HourlyWeather>

  /**
   * 获取今天的生活指数
   */
  @GET("indices/1d")
  suspend fun get1dIndex(
    @Query("type")
    type: String = "1,2,3,10",
    @Query("location")
    location: String,
    @Query("key")
    key: String = BuildConfig.HE_WEB_KEY
  ): ApiResponse<LifeIndex>


  @GET("air/now")
  suspend fun getNowAir(
    @Query("type")
    type: String = "1,2,3,10",
    @Query("location")
    location: String,
    @Query("key")
    key: String = BuildConfig.HE_WEB_KEY
  ): ApiResponse<Air>


  // 聚合接口
  @GET("http://117.50.189.65:3389/api/weather/all")
  suspend fun getAllWeather(
    @Query("location")
    location: String,
  ): ApiResponse<BaseResponse<Weather>>


  @GET("https://api.caiyunapp.com/v2.6/${BuildConfig.CAIYUN}/{location}/weather?alert=true&dailysteps=1&hourlysteps=24")
  suspend fun getMinutely(
    @Path("location") location: String,
    @Query("alert") alert: Boolean = true
  ): ApiResponse<CaiyunBean>

}
