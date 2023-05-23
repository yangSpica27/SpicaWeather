package me.spica.weather.view.weather_bg

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.SurfaceTexture
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.view.TextureView
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import me.spica.weather.R
import me.spica.weather.tools.dp
import me.spica.weather.view.weather_drawable.CloudDrawable
import me.spica.weather.view.weather_drawable.FoggyDrawable
import me.spica.weather.view.weather_drawable.RainDrawable
import me.spica.weather.view.weather_drawable.SnowDrawable
import me.spica.weather.view.weather_drawable.SunnyDrawable
import me.spica.weather.view.weather_drawable.TranslationDrawable
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class WeatherBgSurfaceView : TextureView, TextureView.SurfaceTextureListener {


    @Volatile
    private var isWork = false // 是否预备绘制

    private val clipPath = Path()

    private var isPause = false

    private val lock = Any()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        surfaceTextureListener = this
    }

    private var threadPool: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    private val translationDrawable = TranslationDrawable(context)

    private lateinit var drawThread: HandlerThread

    private lateinit var drawHandler: Handler

    var bgColor = ContextCompat.getColor(context, R.color.window_background)
    var currentWeatherAnimType = NowWeatherView.WeatherAnimType.UNKNOWN
        set(value) {
            field = value
            post {
                animate().alpha(0f).alpha(1f).setInterpolator(LinearInterpolator()).setDuration(500L).start()

                when (value) {
                    NowWeatherView.WeatherAnimType.SUNNY -> {
                        stopAllAnim()
                        sunnyDrawable.startAnim()
                    }

                    NowWeatherView.WeatherAnimType.CLOUDY -> {
                        stopAllAnim()
                        cloudDrawable.startAnim()
                    }

                    NowWeatherView.WeatherAnimType.RAIN -> {
                        stopAllAnim()
                    }

                    NowWeatherView.WeatherAnimType.SNOW -> {
                        stopAllAnim()
                    }

                    NowWeatherView.WeatherAnimType.UNKNOWN -> {
                        stopAllAnim()
                        cloudDrawable.startAnim()
                    }

                    NowWeatherView.WeatherAnimType.FOG -> {
                        stopAllAnim()
                        foggyDrawable.startAnim()
                    }
                }
            }
        }


    private val cloudDrawable = CloudDrawable(context)

    private val foggyDrawable = FoggyDrawable(context)

    private val rainDrawable = RainDrawable()

    private val snowDrawable = SnowDrawable()

    private val sunnyDrawable = SunnyDrawable(context)

    private fun initSnowAndRain(width: Int, height: Int) {
        rainDrawable.ready(width, height)
        snowDrawable.ready(width, height)
    }


    // 停止所有的动画
    private fun stopAllAnim() {
        sunnyDrawable.cancelAnim()
        cloudDrawable.cancelAnim()
        foggyDrawable.cancelAnim()
    }

    private var mCanvas: Canvas? = null


    private fun doOnDraw() {

        mCanvas = lockCanvas()

        // ================进行绘制==============
        mCanvas?.let { canvas ->

//            translationDrawable.doOnDraw(canvas, width, height)
            canvas.drawColor(ContextCompat.getColor(context, R.color.window_background))
            roundClip(canvas)

            if (isPause) {
                unlockCanvasAndPost(canvas)
                return
            }

//            translationDrawable.doOnDraw(canvas, width, height)

            when (currentWeatherAnimType) {
                NowWeatherView.WeatherAnimType.SUNNY -> sunnyDrawable.doOnDraw(canvas, width, height)
                NowWeatherView.WeatherAnimType.CLOUDY -> cloudDrawable.doOnDraw(canvas, width, height)
                NowWeatherView.WeatherAnimType.RAIN -> rainDrawable.doOnDraw(canvas, width, height)
                NowWeatherView.WeatherAnimType.SNOW -> snowDrawable.doOnDraw(canvas, width, height)
                NowWeatherView.WeatherAnimType.FOG -> foggyDrawable.doOnDraw(canvas, width, height)
                NowWeatherView.WeatherAnimType.UNKNOWN -> cloudDrawable.doOnDraw(canvas, width, height)
            }
            // ================绘制结束===============

            unlockCanvasAndPost(canvas)
        }

    }

    private fun roundClip(canvas: Canvas) {
        canvas.clipPath(clipPath)
        canvas.drawColor(bgColor)
    }

    private var lastSyncTime = 0L


    private val drawRunnable = object : Runnable {
        override fun run() {
            while (isWork) {
                // 记录上次执行渲染时间
                lastSyncTime = System.currentTimeMillis()
                // 执行渲染
                synchronized(lock) {
                    doOnDraw()
                }
                // 保证两张帧之间间隔16ms(60帧)
                drawHandler.postDelayed(this, 16)
            }
        }
    }

    private var scaleValue = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        clipPath.reset()
        clipPath.addRoundRect(
            0f, 0f, width * 1f, height * 1f, floatArrayOf(
                8.dp, 8.dp, 8.dp, 8.dp,
                8.dp, 8.dp, 8.dp, 8.dp,
            ), Path.Direction.CCW
        )
        initSnowAndRain(width, height)
        foggyDrawable.ready(width, height)
    }


    @Synchronized
    fun resumeWeatherAnim() {
        isPause = false
    }

    @Synchronized
    fun pauseWeatherAnim() {
        isPause = true
    }

    override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
        isWork = true
        if (threadPool.isShutdown) {
            threadPool = Executors.newSingleThreadScheduledExecutor()
        }
        drawThread = HandlerThread("draw-thread")
        drawThread.start()
        drawHandler = Handler(drawThread.looper)
        // 单独列出一个线程用于计算 避免绘制线程执行过多的任务
        threadPool.scheduleWithFixedDelay({

            if (currentWeatherAnimType == NowWeatherView.WeatherAnimType.RAIN) {
                rainDrawable.calculate(width, height)
            } else if (currentWeatherAnimType == NowWeatherView.WeatherAnimType.SNOW) {
                snowDrawable.calculate(width, height)
            } else if (currentWeatherAnimType == NowWeatherView.WeatherAnimType.FOG) {
                foggyDrawable.calculate(width, height)
            }

        }, 0, 16, TimeUnit.MILLISECONDS)

        // 渲染线程
        drawHandler.post(drawRunnable)
        translationDrawable.ready(width / 2, height / 2)
        scaleValue = (width / width - 28.dp)
    }

    override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {

    }

    override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
        synchronized(lock) {
            isWork = false
            drawHandler.removeCallbacks(drawRunnable)
        }
        threadPool.shutdown()
        translationDrawable.cancel()
        return true
    }

    override fun onSurfaceTextureUpdated(p0: SurfaceTexture) = Unit


}