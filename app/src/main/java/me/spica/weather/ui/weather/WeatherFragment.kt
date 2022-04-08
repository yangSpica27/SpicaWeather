package me.spica.weather.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import me.spica.weather.base.BindingFragment
import me.spica.weather.databinding.FragmentListBinding
import me.spica.weather.model.city.CityBean
import me.spica.weather.tools.doOnMainThreadIdle
import me.spica.weather.tools.dp
import me.spica.weather.ui.main.MainCardAdapter
import javax.inject.Inject

/**
 * 天气单元页面
 */
@AndroidEntryPoint
class WeatherFragment : BindingFragment<FragmentListBinding>() {

    private val viewModel by viewModels<WeatherViewModel>()

    private lateinit var mainCardAdapter: MainCardAdapter

    private var currentCity: CityBean? = null


    @Inject
    lateinit var cityList: List<CityBean>

    override fun setupViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListBinding = FragmentListBinding.inflate(layoutInflater, container, false)


    override fun onResume() {
        super.onResume()
        mainCardAdapter.onScroll()
    }

    override fun init() {
        currentCity = arguments?.getParcelable("city")
        currentCity?.let {
            viewModel.changeCity(it)
        }
        mainCardAdapter = MainCardAdapter(
            viewBinding.rvList, lifecycleScope,
            viewBinding.scrollView
        )

        // 滑动检测
        viewBinding.scrollView.setOnScrollChangeListener { _, _, _, _, _ ->
            mainCardAdapter.onScroll()
        }

        viewBinding.swipeRefreshLayout.isEnabled = false


        // 设置适配器
        viewBinding.rvList.adapter = mainCardAdapter

        requireContext()
            .dividerBuilder()
            .colorRes(android.R.color.transparent)
            .size(12.dp.toInt())
            .showFirstDivider()
            .showLastDivider()
            .build().addTo(viewBinding.rvList)

        // 设置下拉刷新
        viewBinding.swipeRefreshLayout.setOnRefreshListener {
            currentCity?.let { city->

                viewModel.changeCity(city)

            }
        }

        lifecycleScope.launch {
            viewModel.weatherFlow.collectLatest {
                if (it == null) {
                    errorTip.show()
                }
                viewBinding.swipeRefreshLayout.isRefreshing = false
            }
        }

        lifecycleScope.launch {
            viewModel.weatherCacheFlow.filterNotNull().collectLatest {
                doOnMainThreadIdle({
                    mainCardAdapter.notifyData(it)
                })
            }
        }

    }


        private val errorTip by lazy {
        Snackbar.make(viewBinding.root, "请求过程中发生错误！", Snackbar.LENGTH_LONG)
            .setAction("重试") {
                currentCity?.let {
                    viewModel.changeCity(it)
                }
            }
    }
}