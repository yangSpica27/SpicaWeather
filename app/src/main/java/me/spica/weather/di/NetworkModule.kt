package me.spica.weather.di

import android.content.Context
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.spica.weather.network.HttpLoggingInterceptor
import me.spica.weather.network.hefeng.HeClient
import me.spica.weather.network.hefeng.HeService
import me.spica.weather.repository.HeRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * 注入ohHttpClient
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {

        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://devapi.qweather.com/v7/")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory.create())
            .build()

    }

    @Provides
    @Singleton
    fun provideHeService(retrofit: Retrofit): HeService {

        return retrofit
            .create(HeService::class.java)
    }

    @Provides
    @Singleton
    fun provideHeClient(heService: HeService): HeClient {
        return HeClient(heService)
    }

    @Provides
    @Singleton
    fun provideHeRepository(heClient: HeClient): HeRepository {
        return HeRepository(heClient)
    }

    /**
     * 百度地图
     */
    @Provides
    @Singleton
    fun provideBaiduMap(@ApplicationContext context: Context): LocationClient {
        return LocationClient(
            context,
            LocationClientOption().apply {
                setIsNeedAddress(true)
                setNeedNewVersionRgc(true)
                SetIgnoreCacheException(true)
                setIgnoreKillProcess(false)
                setWifiCacheTimeOut(5 * 60 * 1000)
                locationMode = LocationClientOption.LocationMode.Battery_Saving
            }
        )
    }
}
