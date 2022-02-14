package me.spica.weather.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.spica.weather.base.BindingFragment
import me.spica.weather.databinding.FragmentHomeBinding
import me.spica.weather.tools.initDailyLineChart
import me.spica.weather.ui.main.MainViewModel
import java.text.SimpleDateFormat
import java.util.*


/**
 * 主页
 */
@AndroidEntryPoint
class HomeFragment : BindingFragment<FragmentHomeBinding>() {


    private val viewModel: MainViewModel by activityViewModels()


    // 2022-02-14T16:42+08:00
    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'mm:HH+08:00", Locale.CHINA)

    //12:00
    private val sdfAfter = SimpleDateFormat("更新于 mm:HH", Locale.CHINA)

    private val tipAdapter by lazy {
        TipAdapter()
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

        viewBinding.rvTip.adapter = tipAdapter
        viewBinding.rvWeather.adapter = dailyWeatherAdapter

        //  载入图表数据
        lifecycleScope.launch {
            viewModel.dailyWeatherFlow.filterNotNull()
                .collectLatest {
                    initDailyLineChart(
                        viewBinding.weatherChart,
                        requireContext(),
                        it
                    )
                    dailyWeatherAdapter.items.clear()
                    dailyWeatherAdapter.items.addAll(it.daily)
                    withContext(Dispatchers.Main) {
                        dailyWeatherAdapter.notifyDataSetChanged()
                    }
                }
        }


        lifecycleScope.launch {
            viewModel.nowWeatherFlow.filterNotNull().collectLatest {
                withContext(Dispatchers.Main) {
                    val updateDate = sdf.parse(it.now.obsTime) ?: Date()
                    viewBinding.cardNowWeather.tvUpdateTime.text =
                        sdfAfter.format(updateDate)
                    viewBinding.cardNowWeather.tvTemp.text = it.now.temp + "℃"
                    viewBinding.cardNowWeather.tvNow.text = "早上好！"
                    viewBinding.cardNowWeather.tvWeather.text = it.now.text
                    viewBinding.cardNowWeather.tvFeelTemp.text = "体感温度" + it.now.feelsLike + "℃"
                    viewBinding.cardExtraInfo.apply {
                        tvWaterValue.text = it.now.humidity + "%"
                        tvWindPaValue.text = it.now.pressure + "hPa"
                        tvWindSpeedValue.text = it.now.windSpeed + "km/h"
                    }
                }
            }
        }


        lifecycleScope.launch {
            viewModel.currentIndices.filterNotNull().collectLatest {
                withContext(Dispatchers.Main) {
                    tipAdapter.items.clear()
                    tipAdapter.items.addAll(it)
                    tipAdapter.notifyDataSetChanged()
                }
            }

        }

    }





}