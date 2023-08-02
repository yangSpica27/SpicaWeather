package me.spica.weather

import android.app.Application
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import io.sentry.Sentry
import io.sentry.android.AndroidSentryClientFactory
import timber.log.Timber

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // 初始化AppCenter
        handlerDelegate()
        checkUIThreadPriority()
        Sentry.init("http://c4a8f55137f14be49711243eb3fe4f50@43.248.185.248:29002/2",AndroidSentryClientFactory(this))
    }



    private fun handlerDelegate() {
        // 接管主线程loop  https://api.caiyunapp.com/v2.6/
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
                            // 其他错误可以进行上报..
                            Sentry.getStoredClient().sendException(e)
                            throw e
                        }
                    }
                }
            }
        }
    }

    private fun checkUIThreadPriority() {
        try {
            if (Process.getThreadPriority(0) > Process.THREAD_PRIORITY_URGENT_DISPLAY) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) Process.setThreadPriority(Process.THREAD_PRIORITY_VIDEO)
                else Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
}
