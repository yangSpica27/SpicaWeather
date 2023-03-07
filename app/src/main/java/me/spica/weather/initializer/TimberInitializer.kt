package me.spica.weather.initializer

import android.content.Context
import androidx.startup.Initializer
import me.spica.weather.BuildConfig
import timber.log.Timber

/**
 * 初始化日志框架
 */
@Suppress("unused")
class TimberInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        Timber.plant(Timber.DebugTree())
        Timber.d("TimberInitializer is initialized.")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
