package me.spica.weather.view.weather_bg

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import me.spica.weather.R
import me.spica.weather.tools.dp
import java.util.*


/**
 * 目前的天气
 */
open class NowWeatherView : View, SensorEventListener {

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


  var currentWeatherType = WeatherType.UNKNOWN
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
          WeatherType.SUNNY -> {
            stopAllAnim()
            sunnyAnim.start()
          }
          WeatherType.CLOUDY -> {
            stopAllAnim()
            cloudAnim.start()
            cloudAnim2.start()
          }
          WeatherType.RAIN -> {
            stopAllAnim()
            rainAnim.start()
          }
          WeatherType.SNOW -> {
            stopAllAnim()
            rainAnim.start()
          }
          WeatherType.UNKNOWN -> {
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


  // 雨水的合集
  private val mDropList: ArrayList<RainDrop> = arrayListOf()


  init {
    for (i in 1..30) {
      val drop = RainDrop(random, rainPaint)
      mDropList.add(drop)
    }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    for (drop in mDropList) {
      drop.init(measuredWidth, measuredHeight)
      drop.initPos()
    }
  }


  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    screenHeight = h // 获取屏幕高度

    screenWidth = w //获取屏幕宽度


    centerY = 40.dp.toInt() //设置中心点


  }


  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    drawSunny(canvas)
    drawRain(canvas)
    drawCloudy(canvas)
  }


  // 雪的实现
  private fun drawSnow(canvas: Canvas) {

  }

  // 雨的实现
  private fun drawRain(canvas: Canvas) {
    if (currentWeatherType != WeatherType.RAIN) return
    mDropList.forEach {
      it.rain(canvas)
    }
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
    if (currentWeatherType != WeatherType.SUNNY) return
    sunnyPaint.alpha = 20
    sunnyPaint.color = ContextCompat.getColor(context, R.color.sun_light_color)
    val centerX = width - 50.dp
    val centerY = 0 + 50.dp
    // 保存画布的位置
    canvas.save()
    // 将画布位移到右上角方便测量
    for (index in 1..4) {
      canvas.translate(centerX, centerY)
      // 将画布旋转三次 每次30度（正方形旋转90度看上去一致）+动画进度x90度（用于旋转）
      canvas.rotate(index * (30) + 90 * (sunnyAnim.animatedValue as Float))
      // 绘制内外三层矩形
      val smallSize = 100.dp
      val midSize = 180.dp
      val largeSize = 300.dp
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
    if (currentWeatherType != WeatherType.CLOUDY) return
    canvas.save()
    val centerX = width / 8f * 7f
    val centerY = 0f
    canvas.translate(centerX, centerY)
    cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color)
    canvas.drawCircle(
      0f, 0f,
      screenWidth / 5f + (cloudAnim2.animatedValue as Float) * 16.dp,
      cloudPaint
    )
    canvas.translate(40.dp, 0f)
    cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color2)
    canvas.drawCircle(
      0f, 0f,
      screenWidth / 3f + (cloudAnim.animatedValue as Float) * 8.dp,
      cloudPaint
    )
    canvas.restore()
    canvas.save()
    //=========
    canvas.translate(screenWidth / 2f, 8.dp)
    cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color)
    canvas.drawCircle(
      0f, 0f,
      screenWidth / 5f + (cloudAnim.animatedValue as Float) * 5.dp,
      cloudPaint
    )
    canvas.translate(40f, -18f)
    cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color2)
    canvas.drawCircle(
      0f, 0f,
      screenWidth / 3f + (cloudAnim2.animatedValue as Float) * 8.dp,
      cloudPaint
    )
    canvas.restore()
    // = =====
    canvas.save()
    canvas.translate(screenWidth / 5f - 20.dp, 12.dp)
    cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color)
    canvas.drawCircle(
      0f, 0f,
      screenWidth / 5f + (cloudAnim2.animatedValue as Float) * 5.dp,
      cloudPaint
    )
    canvas.translate((-40).dp, 8f)
    cloudPaint.color = ContextCompat.getColor(context, R.color.cloud_color2)
    canvas.drawCircle(
      0f, 0f,
      screenWidth / 3f + (cloudAnim.animatedValue as Float) * 20.dp,
      cloudPaint
    )
    canvas.restore()
  }


  enum class WeatherType() {
    SUNNY,// 晴朗
    CLOUDY,// 多云
    RAIN,// 下雨
    SNOW,// 下雪
    UNKNOWN,// 无效果
  }


  override fun onSensorChanged(event: SensorEvent) {
//    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
//      mAccelerateValues = lowPass(event.values.clone(), mAccelerateValues)
//    }
//    if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
//      mMagneticValues = lowPass(event.values.clone(), mMagneticValues)
//    }
//    if (mMagneticValues != null && mAccelerateValues != null)
//    SensorManager.getRotationMatrix(mR, null, mAccelerateValues, mMagneticValues);
//    SensorManager.getOrientation(mR, values);
//    // x轴的偏转角度
//    val degreeX = Math.toDegrees(values[1].toDouble())
//    // y轴的偏转角度
//    val degreeY = Math.toDegrees(values[2].toDouble())
//    var scrollX = 0
//    var scrollY = 0
//    if (degreeY <= 0 && degreeY > mDegreeYMin) {
//      scrollX = ((degreeY / Math.abs(mDegreeYMin) * MOVE_DISTANCE_X * mDirection).toInt())
//    } else if (degreeY > 0 && degreeY < mDegreeYMax) {
//      scrollX = ((degreeY / Math.abs(mDegreeYMax) * MOVE_DISTANCE_X * mDirection).toInt())
//    }
//    if (degreeX <= 0 && degreeX > mDegreeXMin) {
//      scrollY = ((degreeX / Math.abs(mDegreeXMin) * MOVE_DISTANCE_Y * mDirection).toInt())
//    } else if (degreeX > 0 && degreeX < mDegreeXMax) {
//      scrollY = ((degreeX / Math.abs(mDegreeXMax) * MOVE_DISTANCE_Y * mDirection).toInt())
//    }
//    sX = scrollX
//    sY = scrollY
  }


  override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

  }

}