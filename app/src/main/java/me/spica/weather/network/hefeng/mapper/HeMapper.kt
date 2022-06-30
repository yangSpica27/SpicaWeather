package me.spica.weather.network.hefeng.mapper

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.ApiSuccessModelMapper
import me.spica.weather.model.weather.*
import me.spica.weather.network.caiyun.CaiyunBean
import me.spica.weather.network.hefeng.HeCode
import me.spica.weather.network.hefeng.air.Air
import me.spica.weather.network.hefeng.daily.DailyWeather
import me.spica.weather.network.hefeng.hourly.HourlyWeather
import me.spica.weather.network.hefeng.index.LifeIndex
import me.spica.weather.network.hefeng.now.NowWeather

object SuccessDailyWeatherMapper : ApiSuccessModelMapper<DailyWeather, List<DailyWeatherBean>> {

  @Throws(RuntimeException::class)
  override fun map(apiErrorResponse: ApiResponse.Success<DailyWeather>):
      List<DailyWeatherBean> {

    if (apiErrorResponse.data.code == HeCode.Ok.code) {
      return apiErrorResponse.data.daily.map {
        it.toDailyWeatherBean()
      }
    } else {
      throw RuntimeException(apiErrorResponse.data.code)
    }
  }
}

object SuccessNowWeatherMapper : ApiSuccessModelMapper<NowWeather, NowWeatherBean> {
  @Throws(RuntimeException::class)
  override fun map(apiErrorResponse: ApiResponse.Success<NowWeather>):
      NowWeatherBean {

    if (apiErrorResponse.data.code == HeCode.Ok.code) {
      return apiErrorResponse.data.now.toNowWeatherBean()
    } else {
      throw RuntimeException(apiErrorResponse.data.code)
    }
  }
}

object SuccessHourlyWeatherMapper : ApiSuccessModelMapper<HourlyWeather, List<HourlyWeatherBean>> {

  @Throws(RuntimeException::class)
  override fun map(apiErrorResponse: ApiResponse.Success<HourlyWeather>):
      List<HourlyWeatherBean> {

    if (apiErrorResponse.data.code == HeCode.Ok.code) {
      return apiErrorResponse.data.hourly.map {
        it.toHourlyWeatherBean()
      }
    } else {
      throw RuntimeException(apiErrorResponse.data.code)
    }
  }
}

object SuccessLifeIndexWeatherMapper : ApiSuccessModelMapper<LifeIndex, List<LifeIndexBean>> {

  @Throws(RuntimeException::class)
  override fun map(apiErrorResponse: ApiResponse.Success<LifeIndex>):
      List<LifeIndexBean> {

    if (apiErrorResponse.data.code == HeCode.Ok.code) {
      return apiErrorResponse.data.daily.map {
        it.toLifeIndexBean()
      }
    } else {
      throw RuntimeException(apiErrorResponse.data.code)
    }
  }
}


object SuccessAirMapper : ApiSuccessModelMapper<Air, AirBean> {

  @Throws(RuntimeException::class)
  override fun map(apiErrorResponse: ApiResponse.Success<Air>): AirBean {
    if (apiErrorResponse.data.code == HeCode.Ok.code) {
      return apiErrorResponse.data.now.toAir()
    } else {
      throw java.lang.RuntimeException(apiErrorResponse.data.code)
    }
  }

}

object SuccessMinutelyMapper : ApiSuccessModelMapper<CaiyunBean, CaiyunExtendBean> {

  @Throws(RuntimeException::class)
  override fun map(apiErrorResponse: ApiResponse.Success<CaiyunBean>): CaiyunExtendBean {
    if (apiErrorResponse.data.status == "ok") {

      return CaiyunExtendBean(
        alerts = apiErrorResponse.data.result.alert.content.map {
          AlertBean(title = it.title, description = it.description, status = it.status, code = it.code, source = it.source)
        }.toList(),
        description = apiErrorResponse.data.result.hourly.description,
        forecastKeypoint = apiErrorResponse.data.result.forecastKeypoint
      )
    } else {
      throw java.lang.RuntimeException(apiErrorResponse.response.message())
    }
  }

}