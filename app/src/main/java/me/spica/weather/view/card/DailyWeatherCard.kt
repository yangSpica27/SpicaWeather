package me.spica.weather.view.card

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import kotlinx.coroutines.Job
import me.spica.weather.databinding.CardDailyWeatherBinding
import me.spica.weather.model.weather.Weather
import me.spica.weather.tools.doOnMainThreadIdle
import me.spica.weather.tools.hide
import me.spica.weather.tools.show
import me.spica.weather.ui.home.DailWeatherAdapter
import me.spica.weather.ui.today.TodayWeatherActivity

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
            dailyWeatherAdapter.items.clear()
            dailyWeatherAdapter.items.addAll(items)
            dailyWeatherAdapter.syncTempMaxAndMin()
            doOnMainThreadIdle({
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
