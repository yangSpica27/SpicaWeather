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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
import me.spica.weather.tools.toast
import me.spica.weather.ui.about.AboutActivity
import me.spica.weather.ui.city.CitySelectActivity
import me.spica.weather.ui.city.WeatherCityActivity
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import javax.inject.Inject

// 请求的返回码
private const val PERMISSION_CODE = 0x01

// 需要的权限
private val LOCATION_PERMISSION = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)
@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(),
    EasyPermissions.RationaleCallbacks,
    EasyPermissions.PermissionCallbacks {

    private val viewModel: WeatherViewModel by viewModels()

    private var currentCity: CityBean? = null

    @Inject
    lateinit var cityList: List<CityBean>


    private val permissionDialog by lazy {
        MaterialAlertDialogBuilder(this)
            .setTitle("申请运行时权限")
            .setMessage(R.string.rationale_location)
            .setPositiveButton(
                R.string.rationale_location_ok
            ) { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    LOCATION_PERMISSION,
                    PERMISSION_CODE
                )
            }
            .setNegativeButton(R.string.rationale_location_cancel) { _, _ ->
                locationClint.start()
            }.create()
    }

    // 获取地理信息后
    private val locationListener = object : BDAbstractLocationListener() {
        override fun onReceiveLocation(result: BDLocation) {
            Timber.e("定位${result.city}")
            if (result.hasAddr()) {
                syncNewCity(result.city)
            } else {
                toast("获取地理位置失败")
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
            permissionDialog.show()
        } else {
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


        // 设置下拉刷新
        viewBinding.swipeRefreshLayout.setOnRefreshListener {
            currentCity?.let {
                viewModel.changedCity(it)
            }
        }

        lifecycleScope.launch {
            viewModel.weatherFlow.collectLatest {
                if (it != null) {
                    viewBinding.dailyWeatherCard.bindData(it.dailyWeather)
                    viewBinding.nowWeatherCard.bindData(it.todayWeather)
                    viewBinding.containerTips.bindData(it.lifeIndexes)
                    viewBinding.hourlyWeatherCard.bindData(it.hourlyWeather)
                } else {
                    errorTip.show()
                }
                viewBinding.swipeRefreshLayout.isRefreshing = false

            }
        }


        // 获取当前城市
        lifecycleScope.launch {
            viewModel
                .cityFlow
                .filterNotNull()
                .collectLatest {
                    if (currentCity?.cityName != it.cityName) {
                        val text = "中国，" + it.cityName
                        viewBinding.toolbar.tsLocation.setText(text)
                    }
                    currentCity = it
                }
        }


    }

    private val errorTip by lazy {
        Snackbar.make(viewBinding.root, "请求过程中发生错误！", Snackbar.LENGTH_LONG)
            .setAction("重试") {
                currentCity?.let {
                    viewModel.changedCity(it)
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
        if (viewBinding.sunriseCard.getLocalVisibleRect(scrollBounds)) {
            viewBinding.sunriseCard.startEnterAnim()
        }
    }

    private fun syncNewCity(cityName: String) {
        lifecycleScope.launch(Dispatchers.Default) {
            cityList.forEach {
                if (cityName.contains(it.cityName)) {
                    if (currentCity?.cityName != it.cityName)
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
        if (permissionDialog.isShowing) {
            permissionDialog.cancel()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {
        // android 12 需要二段请求
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_location),
                PERMISSION_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onRationaleDenied(requestCode: Int) = Unit
}
