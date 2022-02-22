package me.spica.weather.ui.city

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.view.animation.AnimationUtils
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import me.spica.weather.R
import me.spica.weather.base.BindingActivity
import me.spica.weather.databinding.ActivityCityBinding

/**
 * 城市选择
 */
class WeatherCityActivity : BindingActivity<ActivityCityBinding>() {

    private val cityWeatherAdapter = WeatherCityAdapter(this)

    override fun initializer() {
        val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
        viewBinding.rvCity.layoutAnimation = animation
        viewBinding.rvCity.adapter = cityWeatherAdapter
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