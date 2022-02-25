package me.spica.weather.ui.main

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
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
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import javax.inject.Inject

private const val RC_LOCATION_PERM = 0x01

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(),
    EasyPermissions.RationaleCallbacks,
    EasyPermissions.PermissionCallbacks {

    private val viewModel: WeatherViewModel by viewModels()

    private var currentCity = ""

    @Inject
    lateinit var cityList: List<CityBean>

    // 获取地理信息后
    private val locationListener = object : BDAbstractLocationListener() {
        override fun onReceiveLocation(result: BDLocation) {
            Timber.e("定位${result.city}")
            if (result.hasAddr()) {
                syncNewCity(result.city)
            } else {
                Snackbar.make(
                    viewBinding.root,
                    "获取地理信息失败",
                    Snackbar.LENGTH_LONG
                ).show()
                viewModel.changedCity(
                    cityList.first()
                )
            }
        }
    }

    // 百度的定位client
    @Inject
    lateinit var locationClint: LocationClient

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        findViewById<View>(android.R.id.content).transitionName = "shared_element_container"
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 500L
            containerColor = Color.WHITE
            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 550L
            containerColor = Color.WHITE
            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
        }
        super.onCreate(savedInstanceState)
        // 绑定定位监听
        locationClint.registerLocationListener(locationListener)
        requestPermission()
    }

    // 请求定位权限
    private fun requestPermission() {
        if (!hasPermissions()) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_location),
                RC_LOCATION_PERM,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        } else {
            Timber.e("开始定位2")
            locationClint.start()
        }
    }


    // 初始化Toolbar
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this
        )
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        locationClint.start()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (!locationClint.isStarted) {
            locationClint.start()
        }
    }

    // 是否拥有定位权限
    private fun hasPermissions(): Boolean {
        return EasyPermissions.hasPermissions(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
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
                        return@collectLatest
                    } else {
                        if (currentCity != it.cityName) {
                            val text = "中国，" + it.cityName
                            viewBinding.toolbar.tsLocation.setText(text)
                        }
                        currentCity = it.cityName
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


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationClint.unRegisterLocationListener(locationListener)
    }

    override fun onRationaleAccepted(requestCode: Int) {
        // android 12 需要二段请求
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_location),
                RC_LOCATION_PERM,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onRationaleDenied(requestCode: Int) = Unit
}
