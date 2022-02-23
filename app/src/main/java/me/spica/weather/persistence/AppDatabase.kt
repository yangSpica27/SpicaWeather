package me.spica.weather.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import me.spica.weather.model.city.CityBean
import me.spica.weather.persistence.dao.CityDao

@Database(entities = [CityBean::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cityDao(): CityDao

}