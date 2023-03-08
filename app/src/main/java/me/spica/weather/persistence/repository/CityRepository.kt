package me.spica.weather.persistence.repository

import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import me.spica.weather.model.city.CityBean
import me.spica.weather.persistence.dao.CityDao
import javax.inject.Inject

@Suppress("unused")
class CityRepository @Inject constructor(
    private val cityDao: CityDao
) {


    /**
     * 获取所有城市的flow
     */
    fun allCityFlow() = cityDao.getAllDistinctUntilChanged().distinctUntilChanged().flowOn(Dispatchers.IO)


    /**
     * 获取所有城市的列表
     */
    fun allCityList() = cityDao.getAllList()


    /**
     * 添加城市
     */
    @WorkerThread
    suspend fun addCity(cityBean: CityBean): String {
        var result = "添加成功"
        cityDao.getAllList().forEach {
            if (it.cityName == cityBean.cityName) {
                result = "已经添加过了"
                return result
            }
        }
        cityDao.insert(cityBean)
        return result
    }


    /**
     * 选择城市
     */
    @WorkerThread
    suspend fun selected(cityBean: CityBean) = withContext(Dispatchers.IO) {
        cityDao.getAllList().forEach {
            it.isSelected = false
            cityDao.update(it)
        }
        cityBean.isSelected = true
        cityDao.insert(cityBean)
    }

    /**
     * 删除城市
     */
    @WorkerThread
    fun deleteCity(cityBean: CityBean) {
        cityDao.deleteCity(cityBean = cityBean)
    }

    // 选择的城市
    fun selectedCityFlow() = cityDao.getSelectCity()
}
