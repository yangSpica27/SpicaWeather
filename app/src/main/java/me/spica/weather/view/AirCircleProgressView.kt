package me.spica.weather.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import me.spica.weather.R
import me.spica.weather.tools.dp


// 空气质量指数view
private val VIEW_MARGIN = 14.dp

class AirCircleProgressView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private val mRectF: RectF = RectF()

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 50.dp
        color = ContextCompat.getColor(context, R.color.textColorPrimary)
    }


    private val secondTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 16.dp
        color = ContextCompat.getColor(context, R.color.textColorPrimary)
    }

    private val startColor = ContextCompat.getColor(context, R.color.line_default)

    private val endColor = ContextCompat.getColor(context, R.color.l8)

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 6.dp
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(3.dp, 2.dp), 0F)
        color = ContextCompat.getColor(context, R.color.textColorPrimaryHintLight)
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 6.dp
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.line_default)
    }

    private val startAngle = 135f

    private val swipeAngle = 270f

    private var lv = 100

    private val maxLv = 400

    private var progress = 0f

    private var category = "良"

    fun bindProgress(lv: Int, category: String) {
        this.lv = lv
        this.category = category
        ViewCompat.postInvalidateOnAnimation(this)
    }

    fun startAnim() {
        val animator = ValueAnimator.ofFloat(0f, lv * 1f / maxLv)
        animator.duration = 1500
        animator.addUpdateListener {
            progress = it.animatedValue as Float
            progress = Math.min(progress, 1f)
            ViewCompat.postInvalidateOnAnimation(this)
        }
        animator.doOnEnd {
            it.removeAllListeners()
        }
        animator.start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = (MeasureSpec.getSize(widthMeasureSpec) - 2 * VIEW_MARGIN)
        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(
                (width + 2 * VIEW_MARGIN).toInt(),
                MeasureSpec.EXACTLY
            ),

            MeasureSpec.makeMeasureSpec(
                (width + 2 * VIEW_MARGIN).toInt(),
                MeasureSpec.EXACTLY
            )
        )
        mRectF.set(
            VIEW_MARGIN * 1f, VIEW_MARGIN,
            measuredWidth - VIEW_MARGIN,
            measuredHeight - VIEW_MARGIN
        )
    }


    private val textBound = Rect()
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 背景弧
        canvas.drawArc(
            mRectF,
            startAngle,
            swipeAngle * 1f,
            false,
            bgPaint
        )
        // 根据进度变色
        linePaint.color = evaluate(progress, startColor, endColor)
        // progress弧
        canvas.drawArc(
            mRectF,
            startAngle,
            swipeAngle * progress,
            false,
            linePaint
        )

        // 绘制中心文本
        val valueText = (progress * lv).toInt().toString()
        textPaint.getTextBounds(valueText, 0, valueText.length, textBound)
        canvas.drawText(
            valueText,
            mRectF.centerX() - textBound.width() / 2f,
            mRectF.centerY() + textBound.height() / 2f,
            textPaint
        )
        val tipText = category
        secondTextPaint.getTextBounds(tipText, 0, tipText.length, textBound)
        canvas.drawText(
            tipText,
            mRectF.centerX() - textBound.width() / 2f,
            mRectF.bottom - textBound.height(),
            secondTextPaint
        )
    }


    private fun evaluate(fraction: Float, startValue: Int, endValue: Int): Int {
        val startInt = startValue
        val startA = (startInt shr 24 and 0xff) / 255.0f
        var startR = (startInt shr 16 and 0xff) / 255.0f
        var startG = (startInt shr 8 and 0xff) / 255.0f
        var startB = (startInt and 0xff) / 255.0f
        val endInt = endValue
        val endA = (endInt shr 24 and 0xff) / 255.0f
        var endR = (endInt shr 16 and 0xff) / 255.0f
        var endG = (endInt shr 8 and 0xff) / 255.0f
        var endB = (endInt and 0xff) / 255.0f
        // convert from sRGB to linear
        startR = Math.pow(startR.toDouble(), 2.2).toFloat()
        startG = Math.pow(startG.toDouble(), 2.2).toFloat()
        startB = Math.pow(startB.toDouble(), 2.2).toFloat()
        endR = Math.pow(endR.toDouble(), 2.2).toFloat()
        endG = Math.pow(endG.toDouble(), 2.2).toFloat()
        endB = Math.pow(endB.toDouble(), 2.2).toFloat()
        // compute the interpolated color in linear space
        var a = startA + fraction * (endA - startA)
        var r = startR + fraction * (endR - startR)
        var g = startG + fraction * (endG - startG)
        var b = startB + fraction * (endB - startB)
        // convert back to sRGB in the [0..255] range
        a = a * 255.0f
        r = Math.pow(r.toDouble(), 1.0 / 2.2).toFloat() * 255.0f
        g = Math.pow(g.toDouble(), 1.0 / 2.2).toFloat() * 255.0f
        b = Math.pow(b.toDouble(), 1.0 / 2.2).toFloat() * 255.0f
        return Math.round(a) shl 24 or (Math.round(r) shl 16) or (Math.round(g) shl 8) or Math.round(b)
    }

}