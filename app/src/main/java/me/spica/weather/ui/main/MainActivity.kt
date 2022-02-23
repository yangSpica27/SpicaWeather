package me.spica.weather.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
import com.baidu.location.LocationClientOption
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
import me.spica.weather.tools.toast
import me.spica.weather.ui.about.AboutActivity
import me.spica.weather.ui.city.CitySelectActivity
import me.spica.weather.ui.city.WeatherCityActivity
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>() {


    private val viewModel: WeatherViewModel by viewModels()

    private var locationClint: LocationClient? = null


    private val locationListener = object :BDAbstractLocationListener(){
        override fun onReceiveLocation(result: BDLocation) {
            Timber.i("BDMAP:${result.locType}")
            toast(result.city)
        }

    }

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

    private val currentCity by lazy {
        CityBean(
            lon = "118.78",
            lat = "32.04",
            cityName = "南京",
            sortName = "NanJing"
        )
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
        viewModel.changedCity(currentCity)
    }


    override fun onResume() {
        super.onResume()
        getLocation()
    }


    private fun getLocation() {
        if (hasPermissions()) {

            if (locationClint == null) {
                locationClint = LocationClient(this)
                locationClint?.locOption = LocationClientOption().apply {
                    setIsNeedAddress(true)
                    setNeedNewVersionRgc(true)
                    locationMode = LocationClientOption.LocationMode.Battery_Saving
                }
                locationClint?.registerLocationListener(locationListener)
                locationClint?.start()
            }

        } else {
            AndPermission.with(this)
                .runtime()
                .permission(Permission.ACCESS_FINE_LOCATION)
                .permission(Permission.ACCESS_COARSE_LOCATION)
                .permission(Permission.Group.LOCATION)
                .onGranted {
                    getLocation()
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

        lifecycleScope.launch {
            viewModel.cityFlow.filterNotNull()
                .collectLatest {
                    viewBinding.toolbar.tsLocation.setText("中国，" + currentCity.cityName)
                }
        }
    }


    override fun setupViewBinding(inflater: LayoutInflater): ActivityMainBinding =
        ActivityMainBinding.inflate(inflater)


    /**
     * 启动应用的设置
     *
     */
    private fun startAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        )
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
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