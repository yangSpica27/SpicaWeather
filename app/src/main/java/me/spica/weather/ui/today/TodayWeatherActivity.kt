package me.spica.weather.ui.today

import android.annotation.SuppressLint
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
        // 点击顶栏返回
        viewBinding.toolbar.setNavigationOnClickListener {
            finish()
        }

        dailyWeatherBean = intent.getParcelableExtra(KEY_DAY)

        dailyWeatherBean?.let {
            initDailyWeatherBean(it)
        }
    }


    // 显示改日的天气
    @SuppressLint("SetTextI18n")
    private fun initDailyWeatherBean(info: DailyWeatherBean) {
        with(viewBinding) {
            tvDayWeather.text = info.weatherNameDay
            tvNightWeather.text = info.weatherNameNight
            tvMaxTemp.text = "${info.maxTemp}℃"
            tvMinTemp.text = "${info.minTemp}℃"
            tvDayWindDir.text = info.dayWindDir
            tvNightWindDir.text = info.nightWindDir
            tvDayWindSpeed.text = info.dayWindSpeed+"km/h"
            tvNightWindSpeed.text = info.nightWindSpeed+"km/h"
            tvPressureTitle.text = "${info.pressure}pa"
            tvUvTitle.text = "${info.uv}级"
            tvVisTitle.text = "${info.vis}km"
            tvCloudTitle.text = "${info.cloud}%"
        }
    }


    override fun setupViewBinding(inflater: LayoutInflater):
            ActivityTodayWeatherBinding = ActivityTodayWeatherBinding.inflate(inflater)
}