package me.spica.weather.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.spica.weather.R
import me.spica.weather.base.BindingActivity
import me.spica.weather.databinding.ActivityMainBinding
import me.spica.weather.model.city.CityBean
import me.spica.weather.tools.doOnMainThreadIdle
import me.spica.weather.tools.dp
import me.spica.weather.tools.keyboard.FluidContentResizer
import me.spica.weather.ui.about.AboutActivity
import me.spica.weather.ui.city.CitySelectActivity
import me.spica.weather.ui.city.WeatherCityActivity
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>() {

    private val viewModel: WeatherViewModel by viewModels()


    private var currentCity = ""

    @Inject
    lateinit var cityList: List<CityBean>

    private val locationListener = object : BDAbstractLocationListener() {
        override fun onReceiveLocation(result: BDLocation) {
            Timber.e("定位${result.city}")
            if (result.hasAddr()) {
                syncNewCity(result.city)
            }
        }
    }

    @Inject
    lateinit var locationClint: LocationClient

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        findViewById<View>(android.R.id.content).transitionName = "shared_element_container"
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 300L
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 250L
        }
        super.onCreate(savedInstanceState)
    }


    private val errorTip by lazy {
        val sb = Snackbar.make(viewBinding.root, "", Snackbar.LENGTH_INDEFINITE)
        sb.setAction("重试") {
            sb.dismiss()
        }
    }


    private fun initTitle() {
        viewBinding.toolbar.tsLocation.setFactory {
            val textView = TextView(this)
            textView.setTextColor(ContextCompat.getColor(this, R.color.textColorPrimary))
            textView.textSize = 6.dp
            textView.isSingleLine = true
            textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            textView.ellipsize = TextUtils.TruncateAt.END
            textView
        }

        viewBinding.toolbar.iconMenu.setOnClickListener {
            startActivity(Intent(this, WeatherCityActivity::class.java))
        }

        viewBinding.toolbar.iconAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        viewBinding.btnPlus.setOnClickListener {
            startActivity(Intent(this, CitySelectActivity::class.java))
        }
    }

    override fun initializer() {
        FluidContentResizer.listen(this)
        initView()
        initTitle()
    }

    // 获取定位信息
    private fun getLocation() {
        if (hasPermissions()) {
            // 定位
            Timber.e("拥有定位权限")
            locationClint.registerLocationListener(locationListener)
            locationClint.start()
        } else {
            // 无权限则请求权限
            Timber.e("没有定位权限")

            AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.LOCATION)
                .onGranted {
                    it.forEach {
                        Timber.e("授权" + it)
                    }
                    getLocation()
                }.onDenied {
                    if (hasPermissions()) {
                        getLocation()
                    } else {
                        // 否则给默认
                        viewModel.changedCity(
                            CityBean(
                                cityName = "南京",
                                lon = "118.78",
                                lat = "32.04",
                                sortName = "nanjing"
                            )
                        )
                    }

                }
                .start()
        }
    }

    private fun hasPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun initView() {
        viewBinding.scrollView.post {
            doOnMainThreadIdle({
                checkAnim()
            })
        }
        viewBinding.scrollView.setOnScrollChangeListener { _, _, _, _, _ ->
            doOnMainThreadIdle({
                checkAnim()
            })
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

        // 获取当前城市
        lifecycleScope.launch {
            viewModel.cityFlow
                .collectLatest {
                    if (it == null) {
                        getLocation()
                    } else {
                        currentCity = it.cityName
                        viewBinding.toolbar.tsLocation.setText("中国，" + it.cityName)
                    }

                }
        }
    }

    override fun setupViewBinding(inflater: LayoutInflater): ActivityMainBinding =
        ActivityMainBinding.inflate(inflater)

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


    private fun syncNewCity(cityName: String) {
        lifecycleScope.launch(Dispatchers.Default) {
            cityList.forEach {
                if (cityName.contains(it.cityName)) {
                    if (currentCity != it.cityName)
                        withContext(Dispatchers.IO) {
                            Snackbar.make(
                                viewBinding.root,
                                "检查到您目前的城市在${it.cityName}，正在切换",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    viewModel.changedCity(it)
                    return@launch
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationClint.unRegisterLocationListener(locationListener)
    }
}
