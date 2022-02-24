package me.spica.weather.widget.card

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import coil.load
import me.spica.weather.common.WeatherCodeUtils
import me.spica.weather.common.getIconRes
import me.spica.weather.common.getThemeColor
import me.spica.weather.databinding.CardWeatherBinding
import me.spica.weather.model.weather.NowWeatherBean
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

    override var enterAnim: MutableList<Animator> = mutableListOf(
        ObjectAnimator.ofFloat(this, "alpha", 0F, 1F).apply {
            duration = 400
            startDelay = 100
            interpolator = FastOutSlowInInterpolator()
        },
        ObjectAnimator.ofFloat(this, "translationY", 100F, 0F).apply {
            duration = 400
            startDelay = 100
            interpolator = FastOutSlowInInterpolator()
        }
    )

    init {
        alpha = 0f
    }

    // 12:00
    private val sdfAfter = SimpleDateFormat("更新于 mm:HH", Locale.CHINA)

    @SuppressLint("SetTextI18n")
    fun bindData(nowWeatherBean: NowWeatherBean) {
        val themeColor = WeatherCodeUtils
            .getWeatherCode(nowWeatherBean.iconId.toString()).getThemeColor()

        // 设置主题颜色
        binding.root.setBackgroundColor(themeColor)
        // 加载图标
        binding.icWeather.load(
            WeatherCodeUtils.getWeatherCode(nowWeatherBean.iconId.toString()).getIconRes()
        )

        binding.tvTemp.text = nowWeatherBean.temp.toString() + "℃"
        binding.tvNow.text = "早上好！"
        binding.tvWeather.text = nowWeatherBean.weatherName
        binding.tvFeelTemp.text = "体感温度:" + nowWeatherBean.feelTemp.toString() + "℃"
        binding.tvUpdateTime.text = sdfAfter.format(nowWeatherBean.obsTime)
        binding.tvWaterValue.text = nowWeatherBean.water.toString() + "%"
        binding.tvWindPaValue.text = nowWeatherBean.windPa.toString() + "hPa"
        binding.tvWindSpeedValue.text = nowWeatherBean.windSpeed.toString() + "km/h"
    }
}
