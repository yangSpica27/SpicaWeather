package me.spica.weather.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.fondesa.recyclerviewdivider.dividerBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.spica.weather.R
import me.spica.weather.base.BindingFragment
import me.spica.weather.common.WeatherCodeUtils
import me.spica.weather.common.getIconRes
import me.spica.weather.common.getThemeColor
import me.spica.weather.databinding.FragmentHomeBinding
import me.spica.weather.tools.doOnMainThreadIdle
import me.spica.weather.tools.show
import me.spica.weather.ui.main.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*


/**
 * 主页
 */
@AndroidEntryPoint
class HomeFragment : BindingFragment<FragmentHomeBinding>() {


    private val viewModel: WeatherViewModel by activityViewModels()



    //12:00
    private val sdfAfter = SimpleDateFormat("更新于 mm:HH", Locale.CHINA)


    private val tipAdapter by lazy {
        TipAdapter()
    }

    private val hourWeatherAdapter by lazy {
        HourWeatherAdapter()
    }

    private val dailyWeatherAdapter by lazy {
        DailWeatherAdapter()
    }

    override fun setupViewBinding(inflater: LayoutInflater, container: ViewGroup?):
            FragmentHomeBinding = FragmentHomeBinding.inflate(
        inflater, container,
        false
    )

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun init() {

        requireContext()
            .dividerBuilder()
            .colorRes(R.color.line_divider)
            .build()
            .addTo(viewBinding.rvTip)

        viewBinding.rvTip.adapter = tipAdapter
        viewBinding.rvWeather.adapter = dailyWeatherAdapter
        viewBinding.rvHourWeather.adapter = hourWeatherAdapter


        //  载入图表数据
        lifecycleScope.launch {
            viewModel.dailyWeatherFlow.filterNotNull()
                .collectLatest {
                    dailyWeatherAdapter.items.clear()
                    dailyWeatherAdapter.items.addAll(it.daily)
                    dailyWeatherAdapter.syncTempMaxAndMin()
                    withContext(Dispatchers.Main) {
                        dailyWeatherAdapter.notifyDataSetChanged()
                        doOnMainThreadIdle({
                            viewBinding.containerDayWeather.show()
                        })
                    }
                }
        }


        // 请求当日
        lifecycleScope.launch {
            viewModel.nowWeatherFlow.filterNotNull().collectLatest {
                withContext(Dispatchers.Main) {
                    viewBinding.cardNowWeather.tvUpdateTime.text =
                        sdfAfter.format(it.obsTime)
                    viewBinding.cardNowWeather.tvTemp.text = it.temp.toString() + "℃"
                    viewBinding.cardNowWeather.tvNow.text = "早上好！"
                    viewBinding.cardNowWeather.tvWeather.text = it.weatherName
                    viewBinding.cardNowWeather.tvFeelTemp.text = "体感温度" + it.feelTemp.toString() + "℃"
                    viewBinding.cardExtraInfo.apply {
                        tvWaterValue.text = it.water.toString() + "%"
                        tvWindPaValue.text = it.windPa.toString() + "hPa"
                        tvWindSpeedValue.text = it.windSpeed.toString() + "km/h"
                    }
                    viewBinding.cardExtraInfo.root.show()
                    // 主题颜色
                    val themeColor = WeatherCodeUtils
                        .getWeatherCode(it.iconId.toString()).getThemeColor()

                    viewBinding.cardNowWeather.root.setBackgroundColor(themeColor)

                    viewBinding.cardNowWeather.icWeather.load(
                        WeatherCodeUtils.getWeatherCode(it.iconId.toString()).getIconRes()
                    )

                    doOnMainThreadIdle({
                        viewBinding.cardNowWeather.root.show()
                    })


                }
            }
        }


        // 载入天气指数数据
        lifecycleScope.launch {
            viewModel.currentIndices.filterNotNull().collectLatest {
                tipAdapter.items.clear()
                tipAdapter.items.addAll(it)
                withContext(Dispatchers.Main) {
                    tipAdapter.notifyDataSetChanged()
                    doOnMainThreadIdle({
                        viewBinding.containerTips.show()
                    })
                }
            }
        }

        // 载入小时天气数据
        lifecycleScope.launch {
            viewModel.currentDayHourWeather.filterNotNull().collectLatest {
                hourWeatherAdapter.items.clear()
                hourWeatherAdapter.items.addAll(it.hourly)
                hourWeatherAdapter.sortList()
                withContext(Dispatchers.Main) {
                    hourWeatherAdapter.notifyDataSetChanged()
                    doOnMainThreadIdle({
                        viewBinding.containerHourWeather.show()
                    })
                }
            }
        }

    }


}