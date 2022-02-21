package me.spica.weather.widget.card

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import me.spica.weather.databinding.CardDailyWeatherBinding
import me.spica.weather.model.weather.DailyWeatherBean
import me.spica.weather.tools.hide
import me.spica.weather.tools.show
import me.spica.weather.ui.home.DailWeatherAdapter

class DailyWeatherCard : LinearLayout, SpicaWeatherCard {

    private val dailyWeatherAdapter by lazy {
        DailWeatherAdapter()
    }

    private val binding = CardDailyWeatherBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


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
        binding.rvWeather.adapter = dailyWeatherAdapter
    }


    @SuppressLint("NotifyDataSetChanged")
    fun bindData(items: List<DailyWeatherBean>) {
        dailyWeatherAdapter.items.clear()
        dailyWeatherAdapter.items.addAll(items)
        dailyWeatherAdapter.syncTempMaxAndMin()
        dailyWeatherAdapter.notifyDataSetChanged()
        binding.rvWeather.postDelayed(
            {
                binding.layoutLoading.hide()
                binding.rvWeather.show()
            }, 100
        )


    }



}