package me.spica.weather.ui.city

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.spica.weather.R
import me.spica.weather.base.BindingActivity
import me.spica.weather.databinding.ActivityCityBinding
import me.spica.weather.model.city.CityBean

/**
 * 城市选择
 */
@AndroidEntryPoint
class WeatherCityActivity : BindingActivity<ActivityCityBinding>() {

    private val cityWeatherAdapter = WeatherCityAdapter(this)

    private val cityViewModel: CityViewModel by viewModels()

    override fun initializer() {
        val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
        viewBinding.rvCity.layoutAnimation = animation
        viewBinding.rvCity.adapter = cityWeatherAdapter

        lifecycleScope.launch {
            cityViewModel.allCityFlow.collectLatest {
                if (it.isEmpty()) {
                    cityViewModel.addCity(
                        CityBean(
                            lon = "118.78",
                            lat = "32.04",
                            cityName = "南京",
                            sortName = "NanJing"
                        )
                    )
                }
                cityWeatherAdapter.diffUtil.submitList(it)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
        super.onCreate(savedInstanceState)
    }

    override fun setupViewBinding(inflater: LayoutInflater): ActivityCityBinding {
        return ActivityCityBinding.inflate(inflater)
    }
}