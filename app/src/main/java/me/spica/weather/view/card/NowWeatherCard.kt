package me.spica.weather.view.card

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import me.spica.weather.databinding.CardWeatherBinding
import me.spica.weather.model.weather.Weather
import me.spica.weather.tools.doOnMainThreadIdle
import java.text.SimpleDateFormat
import java.util.*

/**
 *  用于展示现在的天气
 */
class NowWeatherCard : ConstraintLayout, SpicaWeatherCard {



    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val binding: CardWeatherBinding = CardWeatherBinding.inflate(LayoutInflater.from(context), this, true)

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()
    override var index: Int = 0
    override var hasInScreen: Boolean = false


    init {
        resetAnim()
    }

    // 12:00
    private val sdfAfter = SimpleDateFormat("更新于 mm:HH", Locale.CHINA)

    @SuppressLint("SetTextI18n")
    override fun bindData(weather: Weather) {
            val nowWeatherBean = weather.todayWeather

            // 设置主题颜色

            doOnMainThreadIdle({
                binding.tvTemp.text = nowWeatherBean.temp.toString() + "℃"
                binding.tvNow.text = "空气质量：${weather.air.category}"
                binding.tvWeather.text = nowWeatherBean.weatherName+","
                binding.tvFeelTemp.text = "体感温度:" + nowWeatherBean.feelTemp.toString() + "℃"
                binding.tvUpdateTime.text = sdfAfter.format(nowWeatherBean.obsTime)
                binding.tvWaterValue.text = nowWeatherBean.water.toString() + "%"
                binding.tvWindPaValue.text = nowWeatherBean.windPa.toString() + "hPa"
                binding.tvWindSpeedValue.text = nowWeatherBean.windSpeed.toString() + "km/h"
            })
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
}
