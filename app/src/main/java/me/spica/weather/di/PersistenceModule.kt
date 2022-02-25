package me.spica.weather.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.github.stuxuhai.jpinyin.PinyinFormat
import com.github.stuxuhai.jpinyin.PinyinHelper
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.spica.weather.model.city.CityBean
import me.spica.weather.model.city.Province
import me.spica.weather.persistence.AppDatabase
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        application: Application,
    ): AppDatabase {
        return Room
            .databaseBuilder(
                application, AppDatabase::class.java,
                "spica_weather.db"
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideCityDao(appDatabase: AppDatabase) = appDatabase.cityDao()

    @Provides
    @Singleton
    fun provideProvinces(application: Application): List<CityBean> {

        val provinces = arrayListOf<Province>()
        val cityList = arrayListOf<CityBean>()
        val moshi = Moshi.Builder().build()
        val listOfCardsType = Types.newParameterizedType(
            List::class.java, Province::class.java
        )

        val jsonAdapter = moshi.adapter<List<Province>>(listOfCardsType)

        provinces.addAll(
            jsonAdapter.fromJson(
                getJsonString(
                    application
                )
            ) ?: listOf()
        )

        cityList.addAll(
            provinces.map {
                CityBean(
                    cityName = it.name,
                    sortName = PinyinHelper.convertToPinyinString
                        (it.name, "", PinyinFormat.WITHOUT_TONE),
                    lon = it.log,
                    lat = it.lat
                )
            }.filter {
                it.cityName.isNotEmpty()
            }
        )

        provinces.forEach {
            cityList.addAll(
                it.children.map { city ->
                    CityBean(
                        cityName = city.name,
                        sortName = PinyinHelper.convertToPinyinString
                            (city.name, "", PinyinFormat.WITHOUT_TONE),
                        lon = city.log,
                        lat = city.lat
                    )
                }
            )
        }

        cityList.sortBy {
            it.sortName
        }

        return cityList.filter {
            it.cityName.isNotEmpty()
        }

    }


    /**
     * 读取assets下配置文件
     *
     * @param context 上下文
     * @return 内容
     */
    @Throws(IOException::class)
    private fun getJsonString(context: Context): String {
        var br: BufferedReader? = null
        val sb = StringBuilder()
        try {
            val manager = context.assets
            br = BufferedReader(InputStreamReader(manager.open("city.json")))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        } finally {
            try {
                br?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }
}
