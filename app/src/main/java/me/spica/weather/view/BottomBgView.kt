package me.spica.weather.view

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import me.spica.weather.R
import me.spica.weather.tools.dp

class BottomBgView : View {

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 2.dp
        color = ContextCompat.getColor(context, R.color.line_divider)
    }

    private val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.white)
        style = Paint.Style.FILL
        strokeJoin = Paint.Join.ROUND
    }

    private val arcPaint = Paint().apply {
        strokeWidth = 1.dp
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.line_divider)
        maskFilter = BlurMaskFilter(2.dp, BlurMaskFilter.Blur.SOLID)
    }

    private var path = Path()

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        path = Path()

        path.moveTo(0F, 0F)

        path.lineTo(w - 20.dp - 56.dp - 12.dp, 0F)

        path.addArc(
            width - 20.dp - 56.dp - 12.dp,
            -(height.toFloat() - 14.dp),
            width - 20.dp + 12.dp,
            height.toFloat() - 14.dp,
            180F,
            -180F
        )

        path.lineTo(w.toFloat(), 0F)
        path.lineTo(w.toFloat(), height.toFloat())
        path.lineTo(0F, height.toFloat())

        path.lineTo(0F, 0F)

        path.close()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, pathPaint)
        canvas.drawLine(0F, 0F, width - 20.dp - 56.dp - 12.dp, 0F, linePaint)
        canvas.drawLine(width - 20.dp + 12.dp, 0F, width.toFloat(), 0F, linePaint)
        canvas.drawArc(
            width - 20.dp - 56.dp - 12.dp,
            -(height.toFloat() - 14.dp),
            width - 20.dp + 12.dp,
            height.toFloat() - 14.dp,
            180F,
            -180F,
            false,
            arcPaint
        )
    }
}
