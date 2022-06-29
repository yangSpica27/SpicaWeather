package me.spica.weather.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import me.spica.weather.model.city.CityBean
import me.spica.weather.model.weather.Weather
import me.spica.weather.persistence.dao.CityDao
import me.spica.weather.persistence.dao.WeatherDao


/**
 * 数据库
 */
@Database(entities = [CityBean::class, Weather::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cityDao(): CityDao

    abstract fun weatherDao(): WeatherDao
}
