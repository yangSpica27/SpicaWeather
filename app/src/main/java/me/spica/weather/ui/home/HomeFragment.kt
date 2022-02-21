package me.spica.weather.ui.home

import android.annotation.SuppressLint
import android.graphics.Rect
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
import me.spica.weather.model.city.CityBean
import me.spica.weather.tools.Preference
import me.spica.weather.tools.doOnMainThreadIdle
import me.spica.weather.ui.main.WeatherViewModel


/**
 * 主页
 */
@AndroidEntryPoint
class HomeFragment : BindingFragment<FragmentHomeBinding>() {


    private val viewModel: WeatherViewModel by activityViewModels()


    private val currentCity by Preference(
        Preference.CUR_CITY,
        CityBean(
            lon = "118.78",
            lat = "32.04",
            cityName = "南京",
            sortName = "NanJing"
        )
    )


    override fun setupViewBinding(inflater: LayoutInflater, container: ViewGroup?):
            FragmentHomeBinding = FragmentHomeBinding.inflate(
        inflater, container,
        false
    )

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun init() {


       viewBinding.scrollView.post {
           checkAnim()
       }
        viewBinding.scrollView.setOnScrollChangeListener { _, _, _, _, _ ->
            checkAnim()
        }

        //  载入图表数据
        lifecycleScope.launch {
            viewModel.dailyWeatherFlow
                .filterNotNull()
                .collectLatest {
                    doOnMainThreadIdle({
                        viewBinding.dailyWeatherCard.bindData(it)
                    })
                }
        }


        // 请求当日
        lifecycleScope.launch {
            viewModel.nowWeatherFlow.filterNotNull().collectLatest {
                withContext(Dispatchers.Main) {
                    doOnMainThreadIdle({
                        viewBinding.nowWeatherCard.bindData(it)
                    })

                }
            }
        }


        // 载入天气指数数据
        lifecycleScope.launch {
            viewModel.currentIndices.filterNotNull().collectLatest {
                viewBinding.containerTips.bindData(it)
            }
        }

        // 载入小时天气数据
        lifecycleScope.launch {
            viewModel.hourlyWeatherFlow.filterNotNull().collectLatest {
                doOnMainThreadIdle({
                    viewBinding.hourlyWeatherCard.bindData(it)
                })
            }
        }

    }

    private val scrollBounds = Rect()
    private fun checkAnim() {
        viewBinding.scrollView.getHitRect(scrollBounds)
        if (viewBinding.nowWeatherCard.getLocalVisibleRect(scrollBounds)) {
            viewBinding.nowWeatherCard.startEnterAnim()
        }

        if (viewBinding.dailyWeatherCard.getLocalVisibleRect(scrollBounds)) {
            viewBinding.dailyWeatherCard.startEnterAnim()
        }

        if (viewBinding.hourlyWeatherCard.getLocalVisibleRect(scrollBounds)) {
            viewBinding.hourlyWeatherCard.startEnterAnim()
        }

        if (viewBinding.containerTips.getLocalVisibleRect(scrollBounds)) {
            viewBinding.containerTips.startEnterAnim()
        }

    }


}