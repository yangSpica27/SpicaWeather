package me.spica.weather.view.card

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import kotlinx.coroutines.Job
import me.spica.weather.databinding.CardHourlyWeatherBinding
import me.spica.weather.model.weather.Weather
import me.spica.weather.tools.hide
import me.spica.weather.tools.show
import me.spica.weather.ui.weather.HourWeatherAdapter

class HourWeatherCard : CardLinearlayout, SpicaWeatherCard{

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val binding = CardHourlyWeatherBinding.inflate(LayoutInflater.from(context), this, true)

    private val job = Job()



    private val hourWeatherAdapter by lazy {
        HourWeatherAdapter()
    }


    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()
    override var index: Int = 1
    override var hasInScreen: Boolean = false

    init {
        resetAnim()
        binding.rvHourWeather.adapter = hourWeatherAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun bindData(weather: Weather) {
            val items = weather.hourlyWeather
            hourWeatherAdapter.items.clear()
            hourWeatherAdapter.items.addAll(items)
            hourWeatherAdapter.sortList()
            binding.rvHourWeather.post {
                hourWeatherAdapter.notifyDataSetChanged()
            }
            binding.rvHourWeather.postDelayed(
                {
                    binding.layoutLoading.hide()
                    binding.rvHourWeather.show()
                }, 100
            )
    }


}
