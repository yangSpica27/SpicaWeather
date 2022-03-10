package me.spica.weather.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
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
        color = ContextCompat.getColor(context, R.color.textColorPrimaryLight)
        setTypeface(Typeface.DEFAULT_BOLD)
    }

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

    fun bindProgress(lv: Int) {
        this.lv = lv
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

        // progress弧
        canvas.drawArc(
            mRectF,
            startAngle,
            swipeAngle * progress,
            false,
            linePaint
        )

        // 绘制中心文本
        val tip = (progress * lv).toInt().toString()
        textPaint.getTextBounds(tip, 0, tip.length, textBound)
        canvas.drawText(
            tip,
            mRectF.centerX() - textBound.width() / 2f,
            mRectF.centerY() + textBound.height() / 2f,
            textPaint
        )
    }


}