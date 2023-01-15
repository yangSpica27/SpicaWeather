package me.spica.weather.view.weather_bg

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import me.spica.weather.R
import me.spica.weather.tools.dp

class MiniNowWeatherSurfaceView :SurfaceView, SurfaceHolder.Callback {

  private lateinit var syncThread: Thread



  private var isWork = false // 是否预备绘制

  private val clipPath = Path()


  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  var bgColor = ContextCompat.getColor(context, R.color.window_background)
  var currentWeatherType = NowWeatherView.WeatherType.UNKNOWN
  set(value) {
    field = value
    post {
      animate()
        .alpha(0f)
        .alpha(1f)
        .setInterpolator(LinearInterpolator())
        .setDuration(500L)
        .start()
      when (value) {
        NowWeatherView.WeatherType.SUNNY -> {
          stopAllAnim()
          sunnyAnim.start()
        }
        NowWeatherView.WeatherType.CLOUDY -> {
          stopAllAnim()
          cloudAnim.start()
          cloudAnim2.start()
        }
        NowWeatherView.WeatherType.RAIN -> {
          stopAllAnim()
          rainAnim.start()
        }
        NowWeatherView.WeatherType.SNOW -> {
          stopAllAnim()
          snowAnim.start()
        }
        NowWeatherView.WeatherType.UNKNOWN -> {
          stopAllAnim()
        }
      }
    }
  }

  // 绘制太阳的paint
  private val sunnyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = ContextCompat.getColor(context, R.color.l8)
  }

  // 绘制雨水的paint
  private val rainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    strokeCap = Paint.Cap.ROUND
    strokeWidth = 4.dp
    color = Color.WHITE
    style = Paint.Style.FILL
  }

  // 绘制雨水的paint
  private val snowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    strokeCap = Paint.Cap.ROUND
    strokeWidth = 4.dp
    color = Color.WHITE
    style = Paint.Style.FILL
  }

  // 雨水的合集
  private var rains: ArrayList<RainFlake> = arrayListOf()

  // 雪的集合
  private var snows: ArrayList<SnowFlake> = arrayListOf()


  private fun initSnowAndRain(width: Int, height: Int) {
    rains.clear()
    snows.clear()
    //mSnowFlakes所有的雨滴都生成放到这里面
    for (i in 0 until 30) {
      rains.add(RainFlake.create(width, height, rainPaint))
      snows.add(SnowFlake.create(width, height, snowPaint))
    }
  }


  // 停止所有的动画
  private fun stopAllAnim() {
    sunnyAnim.cancel()
    rainAnim.cancel()
    snowAnim.cancel()
    cloudAnim.cancel()
    cloudAnim2.cancel()
  }

  private val cloudAnim = ValueAnimator.ofFloat(
    0f, 1f
  ).apply {
    repeatCount = Animation.INFINITE
    repeatMode = ValueAnimator.REVERSE
    duration = 3000L
  }

  private val cloudAnim2 = ValueAnimator.ofFloat(
    0f, 1f
  ).apply {
    repeatCount = Animation.INFINITE
    repeatMode = ValueAnimator.REVERSE
    duration = 4000L
    interpolator = LinearInterpolator()
  }


  private val sunnyAnim = ObjectAnimator.ofFloat(0f, 1f).apply {
    duration = 20 * 1000L
    repeatCount = Animation.INFINITE
    repeatMode = ValueAnimator.RESTART
    interpolator = LinearInterpolator()
  }

  private val rainAnim = ObjectAnimator.ofFloat(0f, 1f).apply {
    repeatCount = Animation.INFINITE
    interpolator = LinearInterpolator()
  }


  private val snowAnim = ObjectAnimator.ofFloat(0f, 1f).apply {
    repeatCount = Animation.INFINITE
    interpolator = LinearInterpolator()
  }


  init {
    holder.addCallback(this)
    this.holder.setFormat(PixelFormat.TRANSLUCENT)
  }


  private fun doOnDraw() {
    val canvas = holder.lockCanvas(null) ?: return
    // ================进行绘制==============
    canvas.drawColor(ContextCompat.getColor(context, R.color.window_background))
    roundClip(canvas)
    drawSunny(canvas)
    drawRain(canvas)
    drawCloudy(canvas)
    drawSnow(canvas)
    // ================绘制结束===============
    holder.unlockCanvasAndPost(canvas)
  }

  private fun roundClip(canvas: Canvas) {
    canvas.clipPath(clipPath)
    canvas.drawColor(bgColor)
  }

  private var lastSyncTime = 0L
  override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
    isWork = true
    syncThread = Thread {
      while (isWork) {
        lastSyncTime = System.currentTimeMillis()
        doOnDraw()
        try {
          Thread.sleep(Math.max(0, 24 - (System.currentTimeMillis() - lastSyncTime)))
        } catch (e: Exception) {
          e.printStackTrace()
        }

      }
    }
    syncThread.start()
  }


  // 雨的实现
  private fun drawRain(canvas: Canvas) {
    if (currentWeatherType != NowWeatherView.WeatherType.RAIN) return
    rains.forEach {
      it.draw(canvas)
    }
  }


  // 雪的实现
  private fun drawSnow(canvas: Canvas) {
    if (currentWeatherType != NowWeatherView.WeatherType.SNOW) return
    snows.forEach {
      it.draw(canvas)
    }
  }

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
  }


  //屏幕高度
  private var screenHeight = 0

  //屏幕宽度
  private var screenWidth = 0

  override fun surfaceChanged(p0: SurfaceHolder, format: Int, width: Int, height: Int) {
    screenHeight = height // 获取屏幕高度
    screenWidth = width //获取屏幕宽度
  }

  override fun surfaceDestroyed(p0: SurfaceHolder) {
    isWork = false
    syncThread.interrupt()
  }


  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
  }

  //画笔
  private val cloudPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = ContextCompat.getColor(context, R.color.cloud_color)
    style = Paint.Style.FILL
  }

  // 晴天的实现
  private fun drawSunny(canvas: Canvas) {
    if (currentWeatherType != NowWeatherView.WeatherType.SUNNY) return
    sunnyPaint.alpha = 20
    sunnyPaint.color = ContextCompat.getColor(context, R.color.sun_light_color)
    val centerX = width - 20.dp
    val centerY = 0 + 12.dp
    // 保存画布的位置
    canvas.save()
    // 将画布位移到右上角方便测量
    for (index in 1..4) {
      canvas.translate(centerX, centerY)
      // 将画布旋转三次 每次30度（正方形旋转90度看上去一致）+动画进度x90度（用于旋转）
      canvas.rotate(index * (30) + 90 * (sunnyAnim.animatedValue as Float))
      // 绘制内外三层矩形
      val smallSize = 50.dp
      val midSize = 70.dp
      val largeSize = 100.dp
      canvas.drawRect(-smallSize, -smallSize, smallSize, smallSize, sunnyPaint)
      canvas.drawRect(-midSize, -midSize, midSize, midSize, sunnyPaint)
      canvas.drawRect(-largeSize, -largeSize, largeSize, largeSize, sunnyPaint)
      canvas.restore()
      canvas.save()
    }
    // 画布复位用于后面的绘制
    canvas.restore()
  }


  //多云的实现
  private fun drawCloudy(canvas: Canvas) {
    if (currentWeatherType != NowWeatherView.WeatherType.CLOUDY) return
    // ====
    canvas.save()
    canvas.translate(width * 1f - 6.dp, 0f)
    cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color)
    canvas.drawCircle(
      0f, 0f,
      height / 3f + (cloudAnim2.animatedValue as Float) * 16.dp,
      cloudPaint
    )
    //====
    canvas.restore()
    canvas.save()
    canvas.translate(width * 1f - 6.dp, 12f)
    cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color2)
    canvas.drawCircle(
      0f, 0f,
      height / 2f + (cloudAnim.animatedValue as Float) * 8.dp,
      cloudPaint
    )
    //====
    canvas.restore()
    canvas.save()
    canvas.translate(width / 20f * 14f, 4.dp)
    cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color)
    canvas.drawCircle(
      0f, 0f,
      height / 3f + (cloudAnim.animatedValue as Float) * 5.dp,
      cloudPaint
    )
    //====
    canvas.restore()
    canvas.save()
    canvas.translate(width / 20f * 14f + 20f, 8.dp)
    cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color2)
    canvas.drawCircle(
      0f, 0f,
      height / 2f + (cloudAnim2.animatedValue as Float) * 12.dp,
      cloudPaint
    )
    //====
    canvas.restore()
    canvas.save()
    canvas.translate(width / 20f * 17f - 4.dp, 6.dp)
    cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color)
    canvas.drawCircle(
      0f, 0f,
      height / 3f + (cloudAnim2.animatedValue as Float) * 5.dp,
      cloudPaint
    )
    //====
    canvas.restore()
    canvas.save()
    canvas.translate(width / 20f * 17f, 4f)
    cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color2)
    canvas.drawCircle(
      0f, 0f,
      height / 2f + (cloudAnim.animatedValue as Float) * 20.dp,
      cloudPaint
    )
    canvas.restore()
  }
}