package me.spica.weather.widget

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Shader
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import me.spica.weather.R
import me.spica.weather.tools.dp
import timber.log.Timber

class TempLineItem : View {
    var maxValue = 0 // 最高值

    var minValue = 0 // 最低值

    var currentValue = 0 // 当前值

    var lastValue = 0 // 上一个数值

    var nextValue = 0 // 下一个数值

    var currentPop = 0 // 当前的降雨概率

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {


    }

    private val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.pathBgColor)
        style = Paint.Style.FILL
    }

    private val dottedLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        pathEffect = DashPathEffect(floatArrayOf(4.dp, 2.dp), 0F)
        strokeWidth = 2.dp
        color = ContextCompat.getColor(context, R.color.dottedLineColor)
    }

    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint = TextPaint().apply {
        textSize = 12.dp
        color = ContextCompat.getColor(context, R.color.textColorPrimaryLight)
        strokeWidth = 0F
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
//        maskFilter = BlurMaskFilter(4.dp, BlurMaskFilter.Blur.SOLID)
    }


    private val rainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        strokeCap = Paint.Cap.ROUND
        strokeWidth = 12.dp
        color = ContextCompat.getColor(context, R.color.rainRectColor)
    }

    private var viewHeight = 0

    private var viewWidth = 0

    private var pointTopY = 0F // 最高点的Y坐标 130

    private var pointBottomY = 0F // 最低点的Y坐标 255

    private var pointX = 0F // 所有点的x坐标
    private var pointY = 0F // 当前点的Y

    var drawLeftLine = true //是否画左边的线

    var drawRightLine = true //是否画右边的线

    private val tempName: String
        get() = "$currentValue℃"


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE,null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        initViewValue()
    }


    private fun initViewValue() {
        viewHeight = measuredHeight
        viewWidth = measuredWidth
        pointX = viewWidth / 2F // 中心
        pointTopY = paddingTop + 10.dp
        pointBottomY = viewHeight - 10.dp - paddingBottom

        pathPaint.shader = LinearGradient(
            0F,
            0F,
            0F,
            viewHeight.toFloat(),
            ContextCompat.getColor(
                context,
                R.color.black
            ),
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        pointY = pointBottomY - ((currentValue * 1f - minValue) / (maxValue - minValue)) *
                (pointBottomY - pointTopY)
        drawRainPop(canvas)
        drawDottedLine(canvas)
        drawGraph(canvas)
        drawPoint(canvas)
        drawValue(canvas)

    }


    private val textRect = Rect()

    // 绘制数字
    private fun drawValue(canvas: Canvas) {
        textPaint.getTextBounds(tempName, 0, tempName.length, textRect)
        val baseLine1 = pointY - textRect.height() - 2.dp

        canvas.drawText("$currentValue℃", viewWidth / 2F, baseLine1, textPaint)
    }


    private fun drawRainPop(canvas: Canvas) {
        canvas.drawLine(
            width / 2f,
            height * 1F,
            width / 2f,
            height * 1f - height * (currentPop / 100f),
            rainPaint
        )
    }


    private var pathLeft = Path()

    private var pathRight = Path()


    // 绘制折线
    private fun drawGraph(canvas: Canvas) {
        linePaint.pathEffect = null

        linePaint.style = Paint.Style.FILL
        linePaint.color = ContextCompat.getColor(
            context,
            R.color.textColorPrimaryHintLight
        )

        linePaint.strokeWidth = 2.dp
        linePaint.isAntiAlias = true

        linePaint.maskFilter = BlurMaskFilter(4.dp, BlurMaskFilter.Blur.SOLID)

        if (drawLeftLine) {


            val lastPointY = pointBottomY - ((lastValue * 1f - minValue) / (maxValue - minValue)) *
                    (pointBottomY - pointTopY)


            pathLeft = Path()

            pathLeft.moveTo(pointX, pointY)

            pathLeft.lineTo(pointX,viewHeight.toFloat())

            pathLeft.lineTo(0F, viewHeight.toFloat())

            pathLeft.lineTo(0F, (lastPointY + pointY) / 2F)

            pathLeft.close()

            // 绘制背景
            canvas.drawPath(
                pathLeft,
                pathPaint
            )

            canvas.drawLine(
                pointX, pointY,
                0F,
                (lastPointY + pointY) / 2F,
                linePaint
            )

            canvas.drawLine(
                pointX, viewHeight.toFloat() - dottedLinePaint.strokeWidth / 2F,
                0F,
                viewHeight.toFloat() - dottedLinePaint.strokeWidth / 2F,
                dottedLinePaint
            )

        }


        if (drawRightLine) {

            val nextPointY = pointBottomY -
                    ((nextValue * 1f - minValue) / (maxValue - minValue)) *
                    (pointBottomY - pointTopY)


            // 绘制背景

            pathRight = Path()
            pathRight.moveTo(pointX,pointY)
            pathRight.lineTo(pointX,viewHeight.toFloat())
            pathRight.lineTo(viewWidth.toFloat(),height.toFloat())
            pathRight.lineTo(viewWidth.toFloat(),(pointY + nextPointY) / 2F)


            canvas.drawPath(
                pathRight,
                pathPaint
            )

            canvas.drawLine(
                pointX,
                pointY,
                viewWidth.toFloat(),
                (pointY + nextPointY) / 2F,
                linePaint
            )

            canvas.drawLine(
                pointX,
                viewHeight.toFloat() - dottedLinePaint.strokeWidth / 2F,
                viewWidth.toFloat(),
                viewHeight.toFloat() - dottedLinePaint.strokeWidth / 2F,
                dottedLinePaint
            )

            Timber.e("lastP${nextPointY}")
        }

        Timber.e("======================")
    }


    private fun drawDottedLine(canvas: Canvas) {
        canvas.drawLine(
            pointX,
            pointY,
            pointX,
            viewHeight.toFloat(),
            dottedLinePaint
        )
    }


    // 画点
    private fun drawPoint(canvas: Canvas) {
        pointPaint.color = Color.WHITE
        pointPaint.pathEffect = null
        pointPaint.strokeWidth = 4.dp
        pointPaint.style = Paint.Style.FILL
        canvas.drawCircle(pointX, pointY, 6.dp, pointPaint)
        pointPaint.color = ContextCompat.getColor(context, R.color.textColorPrimaryHint)
        pointPaint.strokeWidth = 3.dp
        pointPaint.style = Paint.Style.STROKE
        canvas.drawCircle(pointX, pointY, 5.dp, pointPaint)
    }
}