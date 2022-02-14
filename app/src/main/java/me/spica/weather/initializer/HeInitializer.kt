@file:Suppress("unused")

package me.spica.weather.initializer

import android.content.Context
import androidx.startup.Initializer
import com.amap.api.location.AMapLocationClient
import timber.log.Timber

/**
 * 初始化和风天气SDK
 */

class HeInitializer : Initializer<Unit> {


    override fun create(context: Context) {
        Timber.i("初始化和风天气成功")
        AMapLocationClient.updatePrivacyShow(context, true, true);
        AMapLocationClient.updatePrivacyAgree(context, true);
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()


}