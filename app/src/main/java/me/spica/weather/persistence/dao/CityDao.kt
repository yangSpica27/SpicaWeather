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

    @Query("SELECT * FROM t_city WHERE isSelected =:isSelect LIMIT 0,1")
    fun getSelectCity(isSelect: Boolean = true): Flow<CityBean?>

    @Query("SELECT * FROM t_city ORDER BY isSelected DESC , cityName DESC")
    fun getCities(): Flow<List<CityBean>>

    @Query("SELECT * FROM t_city ORDER  by cityName")
    fun getAllList(): List<CityBean>

    @Query("SELECT * FROM t_city WHERE isSelected == 1 LIMIT 1")
    fun getSelectedCity(): CityBean?

    @ExperimentalCoroutinesApi
    fun getAllDistinctUntilChanged() = getCities().distinctUntilChanged()

    @Update
    fun update(cityBean: CityBean)

    @Delete
    fun deleteCity(cityBean: CityBean)
}
