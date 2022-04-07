package me.spica.weather.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fondesa.recyclerviewdivider.dividerBuilder
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

/**
 * 天气单元页面
 */
@AndroidEntryPoint
class WeatherFragment : BindingFragment<FragmentListBinding>() {

    private val viewModel by viewModels<WeatherViewModel>()

    private lateinit var mainCardAdapter: MainCardAdapter

    private var currentCity: CityBean? = null


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
            currentCity?.let {
                viewModel.changeCity(it)
            }
        }

        lifecycleScope.launch {
            viewModel.weatherFlow.collectLatest {
                if (it == null) {
                    Toast.makeText(requireContext(), "请求错误", Toast.LENGTH_SHORT).show()
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
}