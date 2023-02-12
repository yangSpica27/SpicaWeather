package me.spica.weather.view.weather_bg

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import me.spica.weather.R
import me.spica.weather.common.WeatherType
import me.spica.weather.tools.dp


/**
 * 目前的天气
 */
open class NowWeatherView  {


  enum class WeatherType() {
    SUNNY,// 晴朗
    CLOUDY,// 多云
    RAIN,// 下雨
    SNOW,// 下雪
    FOG,
    UNKNOWN,// 无效果
  }


}