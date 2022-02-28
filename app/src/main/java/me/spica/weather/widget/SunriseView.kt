@file:Suppress("unused")
package me.spica.weather.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import me.spica.weather.R
import me.spica.weather.tools.dp
import timber.log.Timber
import java.util.*

// 日出view

private val mMargin = 14.dp
private const val ARC_ANGLE = 135

class SunriseView : View {

    /**
     * 太阳图标[0]
     * 月亮图标[1]X暂不做月落图
     */
    private var sunIcon: Bitmap

    private val drawablePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val iconSize = 32.dp

    // =========各个文本的bound========


    private val mRectF: RectF = RectF()

    // 用于清除绘制内容
    private val clearfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)


    private val dottedLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        pathEffect = DashPathEffect(floatArrayOf(3.dp, 2.dp), 0F)
        strokeWidth = 2.dp
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.textColorPrimaryHintLight)
    }

    // 是否绘制
    private var isDraw = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        val option = BitmapFactory.Options()
        option.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, R.drawable.ic_sun, option)
        option.inJustDecodeBounds = false
        option.inDensity = option.outWidth
        option.inTargetDensity = iconSize.toInt()
        sunIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_sun, option)
    }

    private var startTime = 0

    private var currentTime = 100

    private var endTime = 200

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = (MeasureSpec.getSize(widthMeasureSpec) - 2 * mMargin)
        val deltaRadians = Math.toRadians((180 - ARC_ANGLE) / 2.0)
        val radius = (width / 2 / Math.cos(deltaRadians)).toInt()
        val height = (radius - width / 2 * Math.tan(deltaRadians)).toInt()
        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec((width + 2 * mMargin).toInt(), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec((height + 2 * mMargin).toInt(), MeasureSpec.EXACTLY)
        )

        val centerX = measuredWidth / 2F
        val centerY = (mMargin + radius) * 1F
        mRectF.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
    }


    /**
     *  设置时间
     */
    fun bindTime(startTime: Date, endTime: Date, currentTime: Date = Date()) {
        this.startTime = decodeTime(startTime)
        this.endTime = decodeTime(endTime)
        this.currentTime = decodeTime(currentTime)
        ensureProgress()
    }

    fun startAnim() {

        val animator = ValueAnimator.ofInt(0, currentTime-startTime)
        animator.duration = 1500
        animator.addUpdateListener {
            progress = it.animatedValue as Int
            ViewCompat.postInvalidateOnAnimation(this)
        }
        animator.doOnEnd {
            it.removeAllListeners()
        }
        animator.start()
    }

    private var progress = 100

    private var max = 200

    private fun ensureProgress() {
        max = endTime - startTime
        progress = currentTime - startTime
        progress = Math.max(0, progress)
        progress = Math.min(max, progress)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val startAngle: Float = 270f - ARC_ANGLE / 2f

        val progressSweepAngle = (progress.toFloat() / max.toFloat() * ARC_ANGLE)


        val progressEndAngle = startAngle + progressSweepAngle

        val deltaAngle = progressEndAngle - 180

        val deltaWidth = Math.abs(
            mRectF.width() / 2f *
                    Math.cos(Math.toRadians(deltaAngle.toDouble()))
        ).toFloat()
        val deltaHeight = Math.abs(mRectF.width() / 2f * Math.sin(Math.toRadians(deltaAngle.toDouble()))).toFloat()

        val iconPositionX = mRectF.centerX() - deltaWidth - iconSize / 2f

        val iconPositionY = mRectF.centerY() - deltaHeight - iconSize / 2f

        // 绘制背景虚线
        canvas.drawArc(
            mRectF,
            startAngle,
            ARC_ANGLE.toFloat(),
            false,
            dottedLinePaint
        )
        // 绘制底线
        canvas.drawLine(
            mMargin,
            measuredHeight - mMargin,
            measuredWidth - mMargin,
            measuredHeight - mMargin,
            dottedLinePaint
        )
        val restoreCount: Int = canvas.save()

        Timber.i("progress${progressEndAngle}")



        if (0 <= 270) {

            canvas.translate(iconPositionX, iconPositionY)

            canvas.drawBitmap(
                sunIcon,
                0f, 0f,
                drawablePaint
            )
        }
        canvas.restoreToCount(restoreCount)
    }


    @Suppress("DEPRECATION")
    private fun decodeTime(time: Date): Int {
        return time.hours * 60 + time.minutes
    }


}
