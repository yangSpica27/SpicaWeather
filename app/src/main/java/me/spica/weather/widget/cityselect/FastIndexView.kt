package me.spica.weather.widget.cityselect

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import me.spica.weather.R
import me.spica.weather.tools.dp


private const val INDEX_NAME = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ"

@Suppress("unused")
class FastIndexView : View {

    // 点击监听
    private var listener: (String) -> Unit = {}

    private var cellHeight = -1F

    private var viewWidth: Float = -1F

    private var touchIndex = -1

    // 选中的颜色
    private var selectedColor = ContextCompat.getColor(context, R.color.purple_200)

    // paint
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 14.dp
        color = selectedColor
    }


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)


    // 测量文本的容器
    private val bounds = Rect()

    override fun onDraw(canvas: Canvas) {
        for (i in INDEX_NAME.indices) {
            val text = INDEX_NAME.substring(i, i + 1)
            //计算绘制字符的X方向起点
            val x = (viewWidth / 2.0f - mPaint.measureText(text) / 2.0f)

            mPaint.getTextBounds(text, 0, text.length, bounds)
            val textHeight: Int = bounds.height()
            //计算绘制字符的Y方向起点
            val y = (cellHeight / 2.0f + textHeight / 2.0f + (i
                    * cellHeight))
            mPaint.color = selectedColor

            canvas.drawText(text, x, y, mPaint)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val index: Int
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                //计算当前触摸的字符索引
                index = (event.y / cellHeight).toInt()
                if (index >= 0 && index < INDEX_NAME.length) {
                    listener(INDEX_NAME.substring(index, index + 1))
                    touchIndex = index
                }
            }
            MotionEvent.ACTION_MOVE -> {
                //计算当前触摸的字符索引
                index = (event.y / cellHeight).toInt()
                if (index >= 0 && index < INDEX_NAME.length) {
                    if (index != touchIndex) {
                        listener(INDEX_NAME.substring(index, index + 1))
                        touchIndex = index
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                performClick()
            }
        }
        invalidate()
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //得到当前控件的宽度
        viewWidth = measuredWidth.toFloat()
        val mHeight = measuredHeight
        //获取单个字符能够拥有的高度
        cellHeight = mHeight * 1.0f / INDEX_NAME.length
    }
}