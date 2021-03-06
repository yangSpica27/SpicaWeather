package me.spica.weather.view.card

import android.animation.AnimatorSet
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import me.spica.weather.R
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
    override fun bindData(weather: Weather) {
        val startTime = weather.dailyWeather[0].sunriseDate
        val endTime = weather.dailyWeather[0].sunsetDate
        val subTitle = weather.dailyWeather[0].moonParse
        binding.sunriseView.bindTime(startTime, endTime)
        binding.sunriseView.themeColor =
            weather.getWeatherType().getThemeColor()
        doOnMainThreadIdle({
            binding.tvTitle.text = String.format(
                context.getString(R.string.sunrise_sunset_time),
                sdf.format(startTime),
                sdf.format(endTime)
            )
            binding.tvSubtitle.text = subTitle
        })

    }

    override fun startEnterAnim() {
        super.startEnterAnim()
        binding.sunriseView.startAnim()
    }


}