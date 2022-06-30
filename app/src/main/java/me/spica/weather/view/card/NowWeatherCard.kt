package me.spica.weather.view.card

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.updateLayoutParams
import me.spica.weather.R
import me.spica.weather.common.getAnimRes
import me.spica.weather.common.getThemeColor
import me.spica.weather.common.getWeatherAnimType
import me.spica.weather.databinding.CardWeatherBinding
import me.spica.weather.model.weather.Weather
import me.spica.weather.tools.*
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
    binding.weatherBg.updateLayoutParams<MarginLayoutParams> {
      topMargin = context.getStatusBarHeight() + 56.dp.toInt()
    }
  }

  private val numAnim = ValueAnimator.ofInt(0, 0)
    .apply {
      duration = 1050L
      interpolator = DecelerateInterpolator(1f)
    }

  // 12:00
  private val sdfAfter = SimpleDateFormat("更新于 mm:HH", Locale.CHINA)

  @SuppressLint("SetTextI18n")
  override fun bindData(weather: Weather) {
    val nowWeatherBean = weather.todayWeather
    val themeColor = weather.getWeatherType().getThemeColor()


    val bgDrawable = context.getDrawable(R.drawable.bg_card)
    bgDrawable?.colorFilter = PorterDuffColorFilter(themeColor, PorterDuff.Mode.SRC_IN)
    binding.weatherBg.background = bgDrawable
    binding.weatherBg.currentWeatherType = weather.getWeatherType().getWeatherAnimType()

    doOnMainThreadIdle({
      with(numAnim) {
        if (isRunning) cancel()
        setIntValues(0, weather.todayWeather.temp)
        removeAllListeners()
        addUpdateListener {
          binding.tvTemp.text = "${it.animatedValue as Int}℃"
        }
        doOnEnd {
          if (weather.alerts.isEmpty()) {
            binding.tvAlertTitle.hide()
          } else {
            binding.tvAlertTitle.text = weather.alerts[0].title
            binding.tvAlertTitle.setTextColor(weather.alerts[0].getAlertColor())
            binding.tvAlertTitle.show()
          }
        }
        start()
      }
      binding.tvNow.text = "空气质量：${weather.air.category}"
      binding.tvWeather.text = nowWeatherBean.weatherName + ","
      binding.tvFeelTemp.text = "体感温度:" + nowWeatherBean.feelTemp.toString() + "℃"
      binding.tvUpdateTime.text = sdfAfter.format(nowWeatherBean.obsTime)
      binding.tvWaterValue.text = nowWeatherBean.water.toString() + "%"
      binding.tvWindPaValue.text = nowWeatherBean.windPa.toString() + "hPa"
      binding.tvWindSpeedValue.text = nowWeatherBean.windSpeed.toString() + "km/h"
      binding.lottieView.setAnimation(weather.getWeatherType().getAnimRes())
      binding.lottieView.playAnimation()
    })


  }


  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
  }
}
