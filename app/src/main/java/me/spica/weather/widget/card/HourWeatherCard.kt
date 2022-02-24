package me.spica.weather.widget.card

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import me.spica.weather.databinding.CardHourlyWeatherBinding
import me.spica.weather.model.weather.HourlyWeatherBean
import me.spica.weather.tools.hide
import me.spica.weather.tools.show
import me.spica.weather.ui.home.HourWeatherAdapter

class HourWeatherCard : CardLinearlayout, SpicaWeatherCard {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val binding = CardHourlyWeatherBinding.inflate(LayoutInflater.from(context), this, true)

    private val hourWeatherAdapter by lazy {
        HourWeatherAdapter()
    }

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
        binding.rvHourWeather.adapter = hourWeatherAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    fun bindData(items: List<HourlyWeatherBean>) {
        hourWeatherAdapter.items.clear()
        hourWeatherAdapter.items.addAll(items)
        hourWeatherAdapter.sortList()
        hourWeatherAdapter.notifyDataSetChanged()
        binding.rvHourWeather.postDelayed(
            {
                binding.layoutLoading.hide()
                binding.rvHourWeather.show()
            }, 100
        )
    }
}
