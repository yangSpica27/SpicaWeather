package me.spica.weather

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import dagger.hilt.android.HiltAndroidApp
import me.spica.weather.view.WebViewPool
import timber.log.Timber

@HiltAndroidApp
class App : Application() {

  override fun onCreate() {
    super.onCreate()
    // 初始化AppCenter
    createAppCenter()
//    WebViewPool.init(this)
    handlerDelegate()
  }

  private fun createAppCenter() {
    AppCenter.start(
      this,
      BuildConfig.APP_CENTER_KEY,
      Analytics::class.java, Crashes::class.java
    )
  }

  private fun handlerDelegate() {
    // 接管主线程loop
    if (!BuildConfig.DEBUG) {
      Handler(Looper.getMainLooper()).post {
        while (true) {
          try {
            Looper.loop()
          } catch (e: Throwable) {
            val stack = Log.getStackTraceString(e)
            if (e is SecurityException) {
              Timber.tag("SecurityException").w(e)
            } else if (stack.contains("Toast") ||
              stack.contains("SFEffectsAPI") ||
              stack.contains("BadTokenException")
            ) {
              Timber.tag("warning!").w(e.message!!)
            } else {
              // 其他错误可以进行上报...
              throw e
            }
          }
        }
      }
    }

  }
}
