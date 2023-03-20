@file:Suppress("unused")

package me.spica.weather.initializer

import android.content.Context
import androidx.startup.Initializer
import coil.Coil
import coil.ImageLoader
import coil.request.CachePolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 初始化图片加载
 */
class CoilInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val imageLoader = ImageLoader.Builder(context)
                .crossfade(true)
                .allowRgb565(true)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .allowHardware(true)
                .build()
            Coil.setImageLoader(imageLoader)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
