package me.spica.weather.ui.today

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import me.spica.weather.base.BindingActivity
import me.spica.weather.databinding.ActivityTodayWeatherBinding
import me.spica.weather.model.weather.DailyWeatherBean


/**
 * 今天的天气
 */
private const val KEY_DAY = "daily_info"
private const val KEY_NOW = "today_info"

class TodayWeatherActivity : BindingActivity<ActivityTodayWeatherBinding>() {


    private var dailyWeatherBean: DailyWeatherBean? = null

    companion object {

        fun startActivity(context: Context, dailyWeatherBean: DailyWeatherBean) {
            val intent = Intent(context, TodayWeatherActivity::class.java)
            intent.putExtra(KEY_DAY, dailyWeatherBean)
            context.startActivity(intent)
        }


    }


    override fun initializer() {

        dailyWeatherBean = intent.getParcelableExtra(KEY_DAY)

        dailyWeatherBean?.let {
            initDailyWeatherBean(it)
        }
    }


    // 显示改日的天气
    private fun initDailyWeatherBean(info: DailyWeatherBean) {

    }


    override fun setupViewBinding(inflater: LayoutInflater):
            ActivityTodayWeatherBinding = ActivityTodayWeatherBinding.inflate(inflater)
}