package me.spica.weather.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.spica.weather.model.weather.Weather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged


@Dao
interface WeatherDao {


    @Query("SELECT * FROM weather WHERE id == 1 LIMIT 0,1")
    fun getWeather(): Flow<Weather?>

    @ExperimentalCoroutinesApi
    fun getWeatherFlowDistinctUntilChanged()= getWeather().distinctUntilChanged()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeather(weather: Weather)
}