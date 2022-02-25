package me.spica.weather.ui.today

import android.view.LayoutInflater
import me.spica.weather.base.BindingActivity
import me.spica.weather.databinding.ActivityTodayWeatherBinding



/**
 * 今天的天气
 */
class TodayWeatherActivity : BindingActivity<ActivityTodayWeatherBinding>() {




    override fun initializer() {

    }




    override fun setupViewBinding(inflater: LayoutInflater):
            ActivityTodayWeatherBinding = ActivityTodayWeatherBinding.inflate(inflater)
}