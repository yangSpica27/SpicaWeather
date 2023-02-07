package me.spica.weather.view.card

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import me.spica.weather.common.getThemeColor
import me.spica.weather.databinding.CardSunriseBinding
import me.spica.weather.model.weather.Weather
import me.spica.weather.tools.doOnMainThreadIdle
import java.text.SimpleDateFormat
import java.util.*


/**
 * 日出日落卡片
 */
class SunriseCard : SpicaWeatherCard, ConstraintLayout {


    private val binding = CardSunriseBinding.inflate(LayoutInflater.from(context), this, true)


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()
    override var index: Int = 3

    override var hasInScreen: Boolean = false

    init {
        resetAnim()
    }


    private val sdf = SimpleDateFormat("HH:mm", Locale.CHINA)

    @SuppressLint("SetTextI18n")
    override fun bindData(weather: Weather) {
        val startTime = weather.dailyWeather[0].sunriseDate()
        val endTime = weather.dailyWeather[0].sunsetDate()
        val subTitle = weather.dailyWeather[0].moonParse
        val themeColor = weather.getWeatherType().getThemeColor()
        binding.sunriseView.bindTime(startTime, endTime)
        binding.sunriseView.themeColor = themeColor
        doOnMainThreadIdle({
            binding.tvTitle.setTextColor(themeColor)
            binding.tvSubtitle.text = subTitle
            binding.tvSunrise.text = "上午 ${sdf.format(startTime)}"
            binding.tvSunset.text = "下午 ${sdf.format(endTime)}"
        })

    }

    override fun startEnterAnim() {
        super.startEnterAnim()
        binding.sunriseView.startAnim()
    }


}