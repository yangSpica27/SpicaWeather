package me.spica.weather.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import me.spica.weather.R
import me.spica.weather.tools.dp
import java.util.*


private const val MINIMAL_TRACK_RADIUS_PX = 300 // 半圆轨迹最小半径

// 日出view
class SunriseView : View {

    // 用于绘制虚线的paint
    private val dottedLinePaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 4.dp
            color = context.getColor(R.color.line_divider)
            style = Paint.Style.STROKE
            pathEffect = DashPathEffect(floatArrayOf(6.dp, 2.dp), 0F);
        }
    }

    // 用于绘制实线的paint
    private val solidLinePaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 4.dp
            color = context.getColor(R.color.line_default)
            style = Paint.Style.STROKE
        }
    }


    private val oval: RectF = RectF()

    // 当前的进度(0.0->1.0)
    private var fraction = 0.0F

    private var sunriseAnim = ValueAnimator.ofFloat(0F, fraction)


    private var centerX //中心X
            = 0
    private var centerY //中心Y
            = 0
    private var srcH //控件高度
            = 0
    private val startAngle = 180f //圆弧起始角度

    private val sweepAngle = 180f //圆弧所占度数

    private val airValue = 66f


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        srcH = h
        centerX = w / 2
        centerY = h / 2
        startAnim()
    }


    fun startAnim() {
        if (sunriseAnim.isRunning) {
            sunriseAnim.cancel()
        }
        syncFraction()
        // 开始动画
        sunriseAnim = ValueAnimator.ofFloat(0F, fraction)
        sunriseAnim.addUpdateListener {
            invalidate()
        }
        sunriseAnim.start()
    }


    private fun syncFraction() {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        // 获取进度
        if (currentHour > 18) {
            fraction = 1F
        }
        if (currentHour < 6) {
            fraction = 0F
        }
        if (currentHour in 6..18) {
            fraction = (currentHour - 6F) / 12F
        }
    }


    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) :
            super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val r: Float = srcH / 2 - 1.dp * 0.5f
        //移动中心
        //移动中心
        canvas.translate(centerX.toFloat(), centerY.toFloat())
        oval.left = 0F - width / 2 + dottedLinePaint.strokeWidth //左边
        oval.top = -r + dottedLinePaint.strokeWidth //上边
        oval.right = width.toFloat() / 2 - dottedLinePaint.strokeWidth //右边
        oval.bottom = r - dottedLinePaint.strokeWidth  //下边
        canvas.drawArc(
            oval, startAngle, sweepAngle,
            false, dottedLinePaint
        ) //绘制圆弧

        canvas.drawArc(
            oval, startAngle, sweepAngle - 100F,
            false, solidLinePaint
        ) //绘制圆弧

    }
}