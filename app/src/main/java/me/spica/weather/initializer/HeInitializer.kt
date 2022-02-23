@file:Suppress("unused")

package me.spica.weather.initializer

import android.content.Context
import androidx.startup.Initializer
import com.qweather.sdk.view.HeConfig
import me.spica.weather.BuildConfig

/**
 * 初始化和风天气SDK
 */

class HeInitializer : Initializer<Unit> {


    // 在此初始化和风天气
    override fun create(context: Context) {
        HeConfig.init(
            BuildConfig.HE_ID,
            BuildConfig.He_KEY
        )
        HeConfig.switchToDevService()// 使用开发版本
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()


}