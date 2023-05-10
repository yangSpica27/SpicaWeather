package me.spica.weather.ui.about

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import me.spica.weather.R
import me.spica.weather.tools.dp
import me.spica.weather.tools.getColorWithAlpha
import me.spica.weather.view.WaveDrawable

class WaveView : View {

    private val waveDrawable = WaveDrawable(this)

    private val waveDrawable2 = WaveDrawable(this)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private val wavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        shader = LinearGradient(
            0F,
            0F,
            100.dp,
            100.dp,
            getColorWithAlpha(.5f, ContextCompat.getColor(context, R.color.water_color)),
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        pathEffect = CornerPathEffect(12.dp)
    }

    private val wavePaint2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        shader = LinearGradient(
            0F,
            0F,
            100.dp,
            100.dp,
            getColorWithAlpha(.5f, ContextCompat.getColor(context, R.color.sun_light_color)),
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        pathEffect = CornerPathEffect(12.dp)
    }

    init {

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        waveDrawable.draw(width/2f, height/2f, canvas, wavePaint)
        canvas.rotate(45f)
        waveDrawable.draw(width/2f, height/2f, canvas, wavePaint2)
    }


}