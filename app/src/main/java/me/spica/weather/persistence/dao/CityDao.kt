@file:Suppress("unused")

package me.spica.weather.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import me.spica.weather.model.city.CityBean


@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg cityBean: CityBean)

    @Query("SELECT * FROM t_city WHERE isSelected =:isSelect")
    suspend fun getAll(isSelect: Boolean): List<CityBean>


    @Query("SELECT * FROM t_city")
    fun getAll(): Flow<List<CityBean>>

    @Query("SELECT * FROM t_city")
    fun getAllList(): List<CityBean>

    @ExperimentalCoroutinesApi
    fun getAllDistinctUntilChanged() = getAll().distinctUntilChanged()

    @Update
    fun update(cityBean: CityBean)

    @Delete
    fun deleteCity(cityBean: CityBean)


}