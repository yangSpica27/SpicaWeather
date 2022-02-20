package me.spica.weather.widget.card

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import me.spica.weather.databinding.CardHourlyWeatherBinding
import me.spica.weather.model.weather.HourlyWeatherBean
import me.spica.weather.ui.home.HourWeatherAdapter

class HourWeatherCard : LinearLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private val binding = CardHourlyWeatherBinding.inflate(LayoutInflater.from(context), this, true)


    private val hourWeatherAdapter by lazy {
        HourWeatherAdapter()
    }


    private val enterAnim by lazy {
        val a: Animator = ObjectAnimator.ofFloat(
            this,
            "alpha", 0f, 1f
        )
        a.duration = 500
        a.startDelay = 100
        a.interpolator = FastOutSlowInInterpolator()
        return@lazy a
    }

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
        if (alpha == 0f) {
            enterAnim.start()
        }
    }


}