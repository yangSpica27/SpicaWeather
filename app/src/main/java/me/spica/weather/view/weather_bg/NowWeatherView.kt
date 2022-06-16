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


/**
 * 目前的天气
 */
class NowWeatherView : View {

    private val random = Random()

    //路径
    private val cloudPath = Path()

    //画笔
    private val cloudPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context,R.color.cloud_color)
        style = Paint.Style.FILL
    }

    // 贝塞尔曲线的控制点
    private var centerY = 0


    //屏幕高度
    private var screenHeight = 0

    //屏幕宽度
    private var screenWidth = 0

    //波长
    private val waveLength = 800

    private val cloudAnim: ValueAnimator = ValueAnimator.ofInt(0, waveLength).apply {
        duration = 1350
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            mOffset = it.animatedValue as Int
            postInvalidateOnAnimation()
        }
    }

    // 偏移量
    private var mOffset = 0

    var currentWeatherType = WeatherType.CLOUDY
        set(value) {
            if (field == value) return
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

    private val sunnyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.l8)
    }

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
        post {
            cloudAnim.start()
        }
        for (i in 1..30) {
            val drop = RainDrop(random, rainPaint)
            mDropList.add(drop);
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (drop in mDropList) {
            drop.init(measuredWidth, measuredHeight)
            drop.initPos()
        }
    }

    private var waveCount = 0;

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        screenHeight = h // 获取屏幕高度

        screenWidth = w //获取屏幕宽度


        centerY = 40.dp.toInt() //设置中心点

        waveCount = Math.round(screenWidth / waveLength + 1.5).toInt() //波长的数量

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

    }


    public enum class WeatherType() {
        SUNNY,// 晴朗
        CLOUDY,// 多云
        RAIN,// 下雨
        SNOW,// 下雪
        UNKNOWN,// 无效果
    }

}