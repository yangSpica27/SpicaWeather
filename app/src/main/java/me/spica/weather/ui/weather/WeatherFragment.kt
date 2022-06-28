package me.spica.weather.ui.weather

import android.content.SharedPreferences
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import me.spica.weather.base.BindingFragment
import me.spica.weather.common.Preference
import me.spica.weather.common.WeatherCodeUtils
import me.spica.weather.common.getThemeColor
import me.spica.weather.common.getWeatherAnimType
import me.spica.weather.databinding.FragmentListBinding
import me.spica.weather.model.city.CityBean
import me.spica.weather.tools.doOnMainThreadIdle
import me.spica.weather.tools.dp
import me.spica.weather.ui.main.MainCardAdapter
import me.spica.weather.view.card.HomeCardType
import me.spica.weather.view.card.toHomeCardType
import me.spica.weather.view.weather_bg.NowWeatherView
import timber.log.Timber

/**
 * 天气单元页面
 */
@AndroidEntryPoint
class WeatherFragment(
  private val scrollListener: View.OnScrollChangeListener
) : BindingFragment<FragmentListBinding>(

),
  SharedPreferences.OnSharedPreferenceChangeListener {

  private val viewModel by viewModels<WeatherViewModel>()

  private lateinit var mainCardAdapter: MainCardAdapter

  private var currentColor = Color.parseColor("#1F787474")

  private var currentWeatherType = NowWeatherView.WeatherType.CLOUDY

  private var currentCity: CityBean? = null

  // 更改颜色
  var onColorChange: (Int, NowWeatherView.WeatherType) -> Unit = { _, _ -> }


  private val sp by lazy {
    PreferenceManager.getDefaultSharedPreferences(requireContext())
  }

  override fun setupViewBinding(
    inflater: LayoutInflater,
    container: ViewGroup?
  ): FragmentListBinding = FragmentListBinding.inflate(layoutInflater, container, false)


  override fun onResume() {
    if (!isFirstLoad) {
      onColorChange(currentColor, currentWeatherType)
    } else {
      // 首次加载
      lifecycleScope.launch {
        viewModel.weatherFlow.collectLatest {
          viewBinding.swipeRefreshLayout.isRefreshing = false
        }
      }

      lifecycleScope.launch(Dispatchers.Default) {
        viewModel.weatherCacheFlow.filterNotNull().collectLatest {
          doOnMainThreadIdle({
            mainCardAdapter.notifyData(it)
          })
          currentColor = WeatherCodeUtils.getWeatherCode(
            it.todayWeather.iconId.toString()
          ).getThemeColor()
          currentWeatherType = WeatherCodeUtils.getWeatherCode(
            it.todayWeather.iconId.toString()
          ).getWeatherAnimType()
          onColorChange(currentColor, currentWeatherType)
        }
      }
    }
    super.onResume()
    viewBinding.scrollView.smoothScrollTo(0, 0)
//        mainCardAdapter.onScroll()


  }


  override fun onDestroyView() {
    super.onDestroyView()
    sp.unregisterOnSharedPreferenceChangeListener(this)
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
    viewBinding.scrollView.setOnScrollChangeListener { view, x, y, ox, oy ->
      mainCardAdapter.onScroll()
      scrollListener.onScrollChange(viewBinding.rvList, x, y, ox, oy)
    }

    viewBinding.swipeRefreshLayout.isEnabled = false


    // 设置适配器
    viewBinding.rvList.adapter = mainCardAdapter

    initCards(sp, false)
    sp.registerOnSharedPreferenceChangeListener(this)

    requireContext()
      .dividerBuilder()
      .colorRes(android.R.color.transparent)
      .size(12.dp.toInt())
      .showFirstDivider()
      .showLastDivider()
      .build().addTo(viewBinding.rvList)

    // 设置下拉刷新
    viewBinding.swipeRefreshLayout.setOnRefreshListener {
      currentCity?.let { city ->

        viewModel.changeCity(city)
      }
    }


  }


  @Suppress("NotifyDataSetChanged")
  private fun initCards(sp: SharedPreferences, needRefresh: Boolean = false) {
    // 根据情况隐藏卡片
    val cards = mutableListOf<HomeCardType>()
    (sp.getStringSet(Preference.CARDS.key, setOf()) ?: setOf()).forEach { value ->
      val code = value.toIntOrNull()
      code?.let {
        cards.add(it.toHomeCardType())
      }
    }

    viewBinding.rvList.post {
      mainCardAdapter.setItems(cards)
      // 重新拉取数据
      if (needRefresh) {
        mainCardAdapter.notifyDataSetChanged()
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

  override fun onSharedPreferenceChanged(sp: SharedPreferences, key: String) {
    Timber.tag("触发刷新").i(key)
    if (key == Preference.CARDS.key) {
      // 根据情况隐藏卡片
      lifecycleScope.launch {
        initCards(sp, true)
      }
    }
  }
}