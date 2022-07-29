package me.spica.weather.view.weather_bg

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import me.spica.weather.R
import me.spica.weather.tools.dp
import java.util.*

class MiniNowWeatherView : View {
  private val random = Random()

  //画笔
  private val cloudPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = ContextCompat.getColor(context, R.color.cloud_color)
    style = Paint.Style.FILL
  }

  // 贝塞尔曲线的控制点
  private var centerY = 0

  private val cloudAnim = ValueAnimator.ofFloat(
    0f, 1f
  ).apply {
    repeatCount = Animation.INFINITE
    repeatMode = Animation.REVERSE
    duration = 3000L
    addUpdateListener {

      postInvalidateOnAnimation()
    }
  }


  private val clipPath = Path()

  private val cloudAnim2 = ValueAnimator.ofFloat(
    0f, 1f
  ).apply {
    repeatCount = Animation.INFINITE
    repeatMode = Animation.REVERSE
    duration = 4000L
    interpolator = LinearInterpolator()

  }


  //屏幕高度
  private var screenHeight = 0

  //屏幕宽度
  private var screenWidth = 0


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
            rainAnim.start()
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
  }


  private val sunnyAnim = ObjectAnimator.ofFloat(0f, 1f).apply {
    duration = 20 * 1000L
    repeatCount = Animation.INFINITE
    repeatMode = Animation.RESTART
    interpolator = LinearInterpolator()
    addUpdateListener {
      postInvalidateOnAnimation()
    }
  }

  private val rainAnim = ObjectAnimator.ofFloat(0f, 1f).apply {
    duration = 20 * 1000L
    repeatCount = Animation.INFINITE
    repeatMode = Animation.RESTART
    interpolator = LinearInterpolator()
    addUpdateListener {
      postInvalidateOnAnimation()
    }
  }

  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
  )




  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
  }


  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    screenHeight = h // 获取屏幕高度

    screenWidth = w //获取屏幕宽度


    centerY = 40.dp.toInt() //设置中心点

    clipPath.reset()
    clipPath.moveTo(12.dp, 0f)
    clipPath.lineTo(width - 12.dp, 0f)
    clipPath.quadTo(width * 1f, 0f, width * 1f, 12.dp)
    clipPath.lineTo(width * 1f, height - 12.dp)
    clipPath.quadTo(width * 1f, height * 1f, width - 12.dp, height.dp)
    clipPath.lineTo(12.dp, height.dp)
    clipPath.quadTo(0f, height.dp, 0.dp, height - 12.dp)
    clipPath.lineTo(0f, 12f)
    clipPath.quadTo(0f, 0.dp, 12.dp, 0.dp)

  }


  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    roundClip(canvas)
    drawSunny(canvas)
    drawRain(canvas)
    drawCloudy(canvas)
  }


  // 雪的实现
  private fun drawSnow(canvas: Canvas) {

  }

  // 雨的实现
  private fun drawRain(canvas: Canvas) {
    if (currentWeatherType != NowWeatherView.WeatherType.RAIN) return

  }

  // 停止所有的动画
  private fun stopAllAnim() {
    sunnyAnim.cancel()
    rainAnim.cancel()
    cloudAnim.cancel()
    cloudAnim2.cancel()
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
    canvas.translate(width*1f-6.dp, 0f)
    cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color)
    canvas.drawCircle(
      0f, 0f,
      height / 3f + (cloudAnim2.animatedValue as Float) * 16.dp,
      cloudPaint
    )
    //====
    canvas.restore()
    canvas.save()
    canvas.translate(width*1f-6.dp, 12f)
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
    canvas.translate(width / 20f * 14f+20f, 8.dp)
    cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color2)
    canvas.drawCircle(
      0f, 0f,
      height / 2f + (cloudAnim2.animatedValue as Float) * 12.dp,
      cloudPaint
    )
    //====
    canvas.restore()
    canvas.save()
    canvas.translate(width/ 20f*17f - 4.dp, 6.dp)
    cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color)
    canvas.drawCircle(
      0f, 0f,
      height / 3f + (cloudAnim2.animatedValue as Float) * 5.dp,
      cloudPaint
    )
    //====
    canvas.restore()
    canvas.save()
    canvas.translate(width/ 20f*17f , 4f)
    cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color2)
    canvas.drawCircle(
      0f, 0f,
      height / 2f + (cloudAnim.animatedValue as Float) * 20.dp,
      cloudPaint
    )
    canvas.restore()
  }


  private fun roundClip(canvas: Canvas) {
    canvas.clipPath(clipPath)
  }
}