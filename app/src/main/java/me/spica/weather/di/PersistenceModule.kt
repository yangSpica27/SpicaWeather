package me.spica.weather.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
    fun provideCityBeans(application: Application): List<Province> {
        val moshi = Moshi.Builder().build()
        val listOfCardsType = Types.newParameterizedType(
            List::class.java, Province::class.java)

        val jsonAdapter = moshi.adapter<List<Province>>(listOfCardsType)

        return jsonAdapter.fromJson(
            getJsonString(
                application
            )
        ) ?: listOf()
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