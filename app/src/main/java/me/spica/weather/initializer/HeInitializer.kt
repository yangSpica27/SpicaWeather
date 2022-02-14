@file:Suppress("unused")

package me.spica.weather.initializer

import android.content.Context
import androidx.startup.Initializer
import com.amap.api.location.AMapLocationClient
import com.qweather.sdk.view.HeConfig
import timber.log.Timber

/**
 * 初始化和风天气SDK
 */

class HeInitializer : Initializer<Unit> {


    override fun create(context: Context) {
        HeConfig.init(
            "HE2112081349381915",
            "01425b0eb61144c9966b38093f648765"
        )
        HeConfig.switchToDevService()// 使用开发版本
        AMapLocationClient.updatePrivacyShow(context, true, true);
        AMapLocationClient.updatePrivacyAgree(context, true);
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()


}