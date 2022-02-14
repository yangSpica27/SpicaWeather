package me.spica.weather.widget

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import me.spica.weather.R
import me.spica.weather.tools.dp

class LineItem : View {

    var maxValue = 0 // 最高值

    var minValue = 0 // 最低值

    var currentValue = 0 // 当前值

    var lastValue = 0 // 上一个数值

    var nextValue = 0 // 下一个数值

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint = TextPaint()

    private var viewHeight = 0

    private var viewWidth = 0

    private var pointTopY = 130 // 最高点的Y坐标 130

    private var pointBottomY = 255 // 最低点的Y坐标 255

    private var pointX = 0F // 所有点的x坐标 
    private var pointY = 0F // 当前点的Y

    var drawLeftLine = true //是否画左边的线

    var drawRightLine = true //是否画右边的线


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        initViewValue()
    }


    private fun initViewValue() {
        viewHeight = measuredHeight
        viewWidth = measuredWidth
        pointX = viewWidth / 2F
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        pointY = ((pointBottomY - pointTopY) * 1f /
                (maxValue - minValue) *
                (maxValue - currentValue + minValue) + pointTopY)
        drawLine(canvas);
        drawGraph(canvas);
        drawPoint(canvas);
        drawValue(canvas);
    }


    // 绘制线条
    private fun drawLine(canvas: Canvas) {
        linePaint.color = ContextCompat.getColor(context, R.color.textColorPrimaryHint)
        linePaint.pathEffect = null
        linePaint.strokeWidth = 2.dp
        linePaint.style = Paint.Style.FILL
        if (drawLeftLine) {
            canvas.drawLine(0F, 500F, viewWidth / 2F, 500F, textPaint)// 这两个值表示下面那条横线的Y坐标（第二四个参数）

        }
        if (drawRightLine) {
            canvas.drawLine(viewWidth / 2F, 500F, viewWidth.toFloat(), 500F, textPaint) // 这两个值表示下面那条横线的Y坐标（第二四个参数）

        }
    }

    // 绘制数字
    private fun drawValue(canvas: Canvas) {
        textPaint.textSize = 12.dp
        textPaint.color = ContextCompat.getColor(context,R.color.textColorPrimaryLight)
        textPaint.strokeWidth = 0F
        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = Paint.Align.CENTER
        val fontMetrics: Paint.FontMetrics = textPaint.fontMetrics
        val baseLine1 = pointY - fontMetrics.bottom * 4-2.dp
        canvas.drawText("$currentValue℃", viewWidth / 2F, baseLine1, textPaint)
    }


    // 绘制折线
    private fun drawGraph(canvas: Canvas) {
        linePaint.pathEffect = null
        linePaint.style = Paint.Style.FILL
        linePaint.color = ContextCompat.getColor(context, R.color.textColorPrimaryHintLight)
        linePaint.strokeWidth = 2.dp
        linePaint.isAntiAlias = true
        linePaint.maskFilter = BlurMaskFilter(4.dp,BlurMaskFilter.Blur.SOLID)
        if (drawLeftLine) {
            val middleValue = currentValue - (currentValue - lastValue) / 2f
            val middleY = (pointBottomY - pointTopY) * 1f / (maxValue - minValue) * (maxValue - middleValue + minValue) + pointTopY
            canvas.drawLine(0F, middleY, pointX, pointY, linePaint)
        }
        if (drawRightLine) {
            val middleValue = currentValue - (currentValue - nextValue) / 2f
            val middleY = (pointBottomY - pointTopY) * 1f / (maxValue - minValue) * (maxValue - middleValue + minValue) + pointTopY
            canvas.drawLine(pointX, pointY, viewWidth.toFloat(), middleY, linePaint)
        }
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