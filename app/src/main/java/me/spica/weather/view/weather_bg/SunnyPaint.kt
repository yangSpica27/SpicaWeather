package me.spica.weather.view.weather_bg

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import me.spica.weather.R

// 云层1的移动速度
private const val TRANSLATE_X_SPEED_1 = 7
// 云层2的移动速度
private const val TRANSLATE_X_SPEED_2 = 7
class SunnyPaint : WeatherPaint {

    // 用于绘制云层1
    private val cloudPaint1 = Paint(Paint.ANTI_ALIAS_FLAG)

    // 用于绘制云层2
    private val cloudPaint2 = Paint(Paint.ANTI_ALIAS_FLAG)

    private var view: View? = null


    private var viewHeight = 0

    private var viewWidth = 0


    override fun init(view: View) {
        this.view = view
        cloudPaint1.color = ContextCompat.getColor(view.context, R.color.cloud_color)
        cloudPaint1.style = Paint.Style.FILL
        cloudPaint2.color = ContextCompat.getColor(view.context, R.color.cloud_color)
        cloudPaint2.style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas, width: Int, height: Int) {
        viewWidth = width
        viewHeight = height

    }


    private fun  resetPx(){

    }


    override fun remove() {

    }
}