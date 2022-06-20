package me.spica.weather.view.card

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import me.spica.weather.common.WeatherCodeUtils
import me.spica.weather.common.getAnimRes
import me.spica.weather.common.getThemeColor
import me.spica.weather.common.getWeatherAnimType
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

    private val numAnim = ValueAnimator.ofInt(0, 0)
        .apply {
            duration = 1050L
            interpolator = DecelerateInterpolator(1f)
            addUpdateListener {
                binding.tvTemp.text = "${it.animatedValue as Int}℃"
            }
        }

    // 12:00
    private val sdfAfter = SimpleDateFormat("更新于 mm:HH", Locale.CHINA)

    @SuppressLint("SetTextI18n")
    override fun bindData(weather: Weather) {
        val nowWeatherBean = weather.todayWeather


        doOnMainThreadIdle({
            if (numAnim.isRunning) numAnim.cancel()
            numAnim.setIntValues(0, nowWeatherBean.temp)
            numAnim.start()
            binding.tvNow.text = "空气质量：${weather.air.category}"
            binding.tvWeather.text = nowWeatherBean.weatherName + ","
            binding.tvFeelTemp.text = "体感温度:" + nowWeatherBean.feelTemp.toString() + "℃"
            binding.tvUpdateTime.text = sdfAfter.format(nowWeatherBean.obsTime)
            binding.tvWaterValue.text = nowWeatherBean.water.toString() + "%"
            binding.tvWindPaValue.text = nowWeatherBean.windPa.toString() + "hPa"
            binding.tvWindSpeedValue.text = nowWeatherBean.windSpeed.toString() + "km/h"
            binding.lottieView.setAnimation(WeatherCodeUtils.getWeatherCode(nowWeatherBean.iconId.toString()).getAnimRes())
            binding.lottieView.playAnimation()
        })


    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
}
