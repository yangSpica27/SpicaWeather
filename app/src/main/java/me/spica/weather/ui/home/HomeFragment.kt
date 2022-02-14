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

/**
 * 主页
 */
@AndroidEntryPoint
class HomeFragment : BindingFragment<FragmentHomeBinding>() {


    private val viewModel: MainViewModel by activityViewModels()


    private val tipAdapter by lazy {
        TipAdapter()
    }

    override fun setupViewBinding(inflater: LayoutInflater, container: ViewGroup?):
            FragmentHomeBinding = FragmentHomeBinding.inflate(
        inflater, container,
        false
    )

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun init() {

        viewBinding.rvTip.adapter = tipAdapter

        //  载入图表数据
        lifecycleScope.launch {
            viewModel.dailyWeatherFlow.filterNotNull()
                .collectLatest {
                    initDailyLineChart(
                        viewBinding.weatherChart,
                        requireContext(),
                        it
                    )
                }
        }


        lifecycleScope.launch {
            viewModel.nowWeatherFlow.filterNotNull().collectLatest {
                withContext(Dispatchers.Main) {
                    viewBinding.cardNowWeather.tvUpdateTime.text = "刚刚更新"
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