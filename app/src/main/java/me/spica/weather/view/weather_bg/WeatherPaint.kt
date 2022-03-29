package me.spica.weather.view.weather_bg

import android.graphics.Canvas
import android.view.View

interface WeatherPaint {

    fun init(view: View)

    fun draw(canvas: Canvas,width:Int,height:Int)

    fun remove()

}