package me.spica.weather.view.card

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.updateLayoutParams
import me.spica.weather.R
import me.spica.weather.common.getAnimRes
import me.spica.weather.common.getThemeColor
import me.spica.weather.common.getWeatherAnimType
import me.spica.weather.databinding.CardNowWeatherBinding
import me.spica.weather.model.weather.Weather
import me.spica.weather.tools.*
import me.spica.weather.ui.warn.WarningDetailActivity
import java.text.SimpleDateFormat
import java.util.*


/**
 *  用于展示现在的天气
 */
class NowWeatherCard : ConstraintLayout, SpicaWeatherCard {


  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  private val binding: CardNowWeatherBinding = CardNowWeatherBinding.inflate(LayoutInflater.from(context), this, true)

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
  private val sdfAfter = SimpleDateFormat("更新于 HH:mm", Locale.CHINA)

  private val showInAnimation by lazy { AnimationUtils.loadAnimation(context, R.anim.in_bottom) }


  fun onResume(){
    binding.weatherBg.resumeWeatherAnim()
  }

  fun onPause(){
    binding.weatherBg.pauseWeatherAnim()
  }

  @SuppressLint("SetTextI18n")
  override fun bindData(weather: Weather) {

    val nowWeatherBean = weather.todayWeather
    val themeColor = weather.getWeatherType().getThemeColor()
    binding.weatherBg.bgColor = themeColor
    binding.weatherBg.currentWeatherType = weather.getWeatherType().getWeatherAnimType()
//    binding.weatherBg.currentWeatherType = NowWeatherView.WeatherType.RAIN
    doOnMainThreadIdle({
      with(numAnim) {
        if (isRunning) cancel()
        setIntValues(0, weather.todayWeather.temp)
        removeAllListeners()
        addUpdateListener {
          binding.tvTemp.text = "${it.animatedValue as Int}℃"
          setTextViewStyles(binding.tvTemp)
        }
        doOnEnd {
          if (weather.alerts.isEmpty()) {
            binding.tvAlertTitle.hide()
          } else {
            binding.tvAlertTitle.text = weather.alerts[0].title
            binding.tvAlertTitle.setTextColor(Color.WHITE)
            binding.tvAlertTitle.show()
          }
        }
        start()
      }

      binding.tvTemp.setOnClickListener {
        binding.tvTemp.startAnimation(showInAnimation)
      }

      binding.tvNow.text = "空气质量：${weather.air.category}"
      binding.tvWeather.text = nowWeatherBean.weatherName + ","
      binding.tvFeelTemp.text = "体感温度:" + nowWeatherBean.feelTemp.toString() + "℃"
      binding.tvUpdateTime.text = sdfAfter.format(nowWeatherBean.obsTime())
      binding.tvWaterValue.text = nowWeatherBean.water.toString() + "%"
      binding.tvWindPaValue.text = nowWeatherBean.windPa.toString() + "hPa"
      binding.tvWindSpeedValue.text = nowWeatherBean.windSpeed.toString() + "km/h"
      binding.lottieView.setAnimation(weather.getWeatherType().getAnimRes())
      binding.lottieView.playAnimation()


      binding.tvAlertTitle.setOnClickListener {
        WarningDetailActivity.startActivity(context, weather.alerts[0])
      }

    })


  }

  private fun setTextViewStyles(textView: TextView) {
    val mLinearGradient = LinearGradient(
      0f,
      0f, 0f, textView.height * 1f,
      Color.WHITE,
      Color.parseColor("#80FFFFFF"),
      Shader.TileMode.CLAMP
    )
    textView.paint.shader = mLinearGradient
    textView.postInvalidateOnAnimation()
  }

  override fun resetAnim() {
    super.resetAnim()
//    animatorView.alpha = 1f
  }
}
