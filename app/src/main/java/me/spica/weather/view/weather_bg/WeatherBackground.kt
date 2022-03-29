package me.spica.weather.view.weather_bg

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class WeatherBackground :View {


    private var weatherPaint = SunnyPaint()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private fun setWeather(){
        weatherPaint.init(this)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        weatherPaint.draw(canvas,width,height)
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        weatherPaint.remove()
    }
}