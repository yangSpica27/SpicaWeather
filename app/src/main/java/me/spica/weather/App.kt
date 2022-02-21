package me.spica.weather

import android.app.Application
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // 初始化AppCenter
        MMKV.initialize(this)
        createAppCenter()
    }

    private fun createAppCenter() {
        AppCenter.start(
            this,
            BuildConfig.APP_CENTER_KEY,
            Analytics::class.java, Crashes::class.java
        )
    }
}