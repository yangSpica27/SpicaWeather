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
import me.spica.weather.ui.home.DailWeatherAdapter

class DailyWeatherCard : LinearLayout {

    private val dailyWeatherAdapter by lazy {
        DailWeatherAdapter()
    }

    private val binding = CardDailyWeatherBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


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
        binding.rvWeather.adapter = dailyWeatherAdapter
    }




    @SuppressLint("NotifyDataSetChanged")
    fun bindData(items:List<DailyWeatherBean>){
        dailyWeatherAdapter.items.clear()
        dailyWeatherAdapter.items.addAll(items)
        dailyWeatherAdapter.syncTempMaxAndMin()
        dailyWeatherAdapter.notifyDataSetChanged()
        if (alpha == 0f) {
            enterAnim.start()
        }
    }

}