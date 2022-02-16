@file:Suppress("unused")
package me.spica.weather.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import me.spica.weather.R
import me.spica.weather.tools.dp
import java.text.SimpleDateFormat
import java.util.*


// 日出view

private const val sunRiseTitle = "日出"
private const val sunFallTitle = "日落"

class SunriseView : View {


    private lateinit var iconBitmap: Bitmap

    private var radius = 0F

    private var centerX = 0F

    private var centerY = 0F

    //=========各个文本的bound========

    private val boundSunRise = Rect()

    private val boundSunFall = Rect()

    private val boundSunriseTime = Rect()

    private val boundSunFallTime = Rect()

    //=========各个文本的bound========

    // 升起的时间
    private lateinit var sunRiseDate: Date

    // 落下的时间
    private lateinit var sunFallDate: Date

    // 间隔1
    private val spaceHeight = 4.dp

    // 间隔2
    private val spaceHeight2 = 4.dp

    // 格式化日期
    private val sdf = SimpleDateFormat("mm:HH", Locale.CHINA)


    private var fraction = 0F // 比例 0 -> 1.0F

    // 绘制日出日落的Paint
    private val statePaint = TextPaint(Paint.FAKE_BOLD_TEXT_FLAG).apply {
        textSize = 16.dp
        color = context.getColor(R.color.textColorPrimaryLight)
    }

    // 绘制时间的Paint
    private val timePaint = TextPaint().apply {
        textSize = 12.dp
        color = context.getColor(R.color.textColorPrimary)
    }


    private val dottedLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        pathEffect = DashPathEffect(floatArrayOf(4.dp, 2.dp), 0F)
        strokeWidth = 2.dp
        color = ContextCompat.getColor(context, R.color.textColorPrimaryHintLight)
    }

    //是否绘制
    private var isDraw = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        initMeasure()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!isDraw) {
            return
        }

        canvas.drawArc(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY,
            0F,
            180F,
            false,
            dottedLinePaint
        )

    }

    fun showSun(sunriseTime: Date, sunFallTime: Date) {
        // 载入时间
        this.sunFallDate = sunFallTime
        this.sunRiseDate = sunriseTime

        fraction = syncFraction(sunriseTime, sunFallTime)

        // 分别测量其文本 宽度/高度 获取半径数值
        statePaint.getTextBounds(sunRiseTitle, 0, sunRiseTitle.length, boundSunRise)
        statePaint.getTextBounds(sunFallTitle, 0, sunFallTitle.length, boundSunFall)

        val sunriseTimeStr = sdf.format(sunriseTime)
        val sunfallTimeStr = sdf.format(sunFallTime)

        timePaint.getTextBounds(sunriseTimeStr, 0, sunriseTimeStr.length, boundSunriseTime)
        timePaint.getTextBounds(sunfallTimeStr, 0, sunfallTimeStr.length, boundSunFallTime)

        val max = boundSunFall.width()
            .coerceAtLeast(boundSunFallTime.width())
            .coerceAtLeast(boundSunRise.width())
            .coerceAtLeast(boundSunriseTime.width())

        radius = width / 2F - max

        centerY = height -
                spaceHeight -
                spaceHeight2 -
                boundSunFall.height() -
                boundSunFallTime.height()

        radius = radius.coerceAtMost(height - centerY)

        isDraw = true
        invalidate()

    }


    // 获取当前的进度
    private fun syncFraction(sunriseTime: Date, sunFallTime: Date): Float {

        val currentDate = Date()
        if (currentDate.before(sunriseTime)) {
            return 0F
        }
        if (currentDate.after(sunFallTime)) {
            return 1F
        }

        return (currentDate.time - sunriseTime.time) * 1F /
                (sunFallTime.time - sunriseTime.time)

    }


    private fun initMeasure() {
        centerX = width / 2F
        centerY = height / 2F

    }


    // 根据X坐标获取Y轴的坐标
    private fun getPointY(x: Float) {

    }

}