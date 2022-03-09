package me.spica.weather.model.weather

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import me.spica.weather.network.hefeng.air.Air
import timber.log.Timber

@Suppress("unused")
class WeatherBeanConverter {


    private val moshi = Moshi.Builder()
        .add(DateAdapter)
        .build()

    private val nowWeatherAdapter = moshi.adapter(NowWeatherBean::class.java)


    private val airAdapter = moshi.adapter(AirBean::class.java)

    private val listOfHourlyType = Types.newParameterizedType(
        List::class.java, HourlyWeatherBean::class.java
    )

    private val hourlyWeatherAdapter =
        moshi.adapter<List<HourlyWeatherBean>>(listOfHourlyType)


    private val listOfDailyType = Types.newParameterizedType(
        List::class.java, DailyWeatherBean::class.java
    )

    private val dailyWeatherAdapter =
        moshi.adapter<List<DailyWeatherBean>>(listOfDailyType)


    private val listOfLifeIndexType = Types.newParameterizedType(
        List::class.java, LifeIndexBean::class.java
    )

    private val lifeIndexWeatherAdapter =
        moshi.adapter<List<LifeIndexBean>>(listOfLifeIndexType)


    @TypeConverter
    fun nowWeatherBeanToString(nowWeatherBean: NowWeatherBean): String {
        return nowWeatherAdapter.toJson(nowWeatherBean)
    }

    @TypeConverter
    fun stringToNowWeatherBean(value: String): NowWeatherBean? {

        return nowWeatherAdapter.fromJson(value)
    }

    @TypeConverter
    fun hourlyWeatherToString(list: List<HourlyWeatherBean>): String {
        return hourlyWeatherAdapter.toJson(list)
    }

    @TypeConverter
    fun stringToHourly(string: String): List<HourlyWeatherBean> {
        return hourlyWeatherAdapter.fromJson(string) ?: listOf()
    }

    @TypeConverter
    fun stringToDaily(json: String): List<DailyWeatherBean> {
        return dailyWeatherAdapter.fromJson(json) ?: listOf()
    }

    @TypeConverter
    fun dailyToString(list: List<DailyWeatherBean>): String {
        return dailyWeatherAdapter.toJson(list)
    }

    @TypeConverter
    fun lifeIndexToString(list: List<LifeIndexBean>): String {
        return lifeIndexWeatherAdapter.toJson(list)
    }

    @TypeConverter
    fun stringToLifeIndex(string: String): List<LifeIndexBean> {
        return lifeIndexWeatherAdapter.fromJson(string) ?: listOf()
    }

    @TypeConverter
    fun nowAirToAirBean(airBean: AirBean): String {
        return airAdapter.toJson(airBean)
    }


    @TypeConverter
    fun stringToAirBean(value: String): AirBean? {
        return airAdapter.fromJson(value)
    }

}