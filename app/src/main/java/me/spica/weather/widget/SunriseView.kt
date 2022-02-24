@file:Suppress("unused")
package me.spica.weather.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Size
import androidx.core.content.ContextCompat
import me.spica.weather.R
import me.spica.weather.tools.dp
import timber.log.Timber

// 日出view

private const val sunRiseTitle = "日出"
private const val sunFallTitle = "日落"

class SunriseView : View {

    /**
     * 太阳图标[0]
     * 月亮图标[1]X暂不做月落图
     */
    @Size(2)
    private val sunIconDrawable: List<Drawable?> =
        listOf(
            ContextCompat.getDrawable(context, R.drawable.ic_sunny),
            ContextCompat.getDrawable(context, R.drawable.ic_sunny),
        )

    private val iconSize = 24.dp

    // =========各个文本的bound========

    private val boundSunRise = Rect()

    private val boundSunFall = Rect()

    private val boundSunriseTime = Rect()

    private val boundSunFallTime = Rect()

    // =========各个文本的bound========

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

    // 是否绘制
    private var isDraw = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val roundPath = RectF()

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val mWidth = MeasureSpec.getSize(widthMeasureSpec) + paddingLeft + paddingRight

        val mHeight = mWidth + iconSize / 2F + paddingTop + paddingBottom

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(mHeight.toInt(), MeasureSpec.EXACTLY)
        )

        roundPath.apply {
            left = paddingLeft * 1F
            right = mWidth * 1F - paddingRight * 1F
            top = (paddingTop + iconSize / 2F)
            bottom = (mWidth + iconSize / 2F)
            +paddingTop + paddingBottom
        }

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        Timber.e("绘制容器：" + roundPath.toShortString())

        canvas.drawArc(
            roundPath,
            180F,
            180F,
            false,
            dottedLinePaint
        )
    }

    // 绘制半圆曲线
    private fun drawDottedLine() {
    }

    // 绘制进度曲线
    private fun drawProgressLine() {
    }

    // 绘制太阳坐标
    private fun drawSunnyIcon() {
    }
}
