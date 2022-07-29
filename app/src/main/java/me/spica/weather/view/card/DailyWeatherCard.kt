package me.spica.weather.view.card

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.kongzue.dialogx.dialogs.FullScreenDialog
import com.kongzue.dialogx.interfaces.OnBindView
import me.spica.weather.R
import me.spica.weather.common.getThemeColor
import me.spica.weather.databinding.ActivityTodayWeatherBinding
import me.spica.weather.databinding.CardDailyWeatherBinding
import me.spica.weather.model.weather.Weather
import me.spica.weather.tools.doOnMainThreadIdle
import me.spica.weather.tools.hide
import me.spica.weather.tools.show
import me.spica.weather.ui.today.TodayWeatherActivity
import me.spica.weather.ui.weather.DailWeatherAdapter


/**
 * 天级的天气信息卡
 */
class DailyWeatherCard : CardLinearlayout, SpicaWeatherCard {

  private val dailyWeatherAdapter by lazy {
    DailWeatherAdapter()
  }


  private val binding = CardDailyWeatherBinding.inflate(LayoutInflater.from(context), this, true)

  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


  override var animatorView: View = this

  override var enterAnim: AnimatorSet = AnimatorSet()
  override var index: Int = 2
  override var hasInScreen: Boolean = false


  init {
    resetAnim()
    binding.rvWeather.adapter = dailyWeatherAdapter
    dailyWeatherAdapter.itemClickListener = {
      TodayWeatherActivity.startActivity(context, it)
    }
  }

  @SuppressLint("NotifyDataSetChanged")
  override fun bindData(weather: Weather) {
    val items = weather.dailyWeather
    binding.cardName.setTextColor(weather.getWeatherType().getThemeColor())
    dailyWeatherAdapter.items.clear()
    dailyWeatherAdapter.items.addAll(items)
    dailyWeatherAdapter.syncTempMaxAndMin()
    doOnMainThreadIdle({
      binding.tipDesc.text = weather.descriptionForToWeek
      if (weather.descriptionForToday.isEmpty()) {
        binding.tipDesc.hide()
      } else {
        binding.tipDesc.show()
      }
      dailyWeatherAdapter.notifyDataSetChanged()
      binding.rvWeather.postDelayed(
        {
          binding.layoutLoading.hide()
          binding.rvWeather.show()
        }, 100
      )
    })
  }



}
