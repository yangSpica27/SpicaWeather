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
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
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
import me.spica.weather.tools.Preference
import me.spica.weather.tools.doOnMainThreadIdle
import me.spica.weather.tools.dp
import me.spica.weather.tools.keyboard.FluidContentResizer
import me.spica.weather.ui.city.CitySelectActivity
import me.spica.weather.ui.city.WeatherCityActivity
import timber.log.Timber


/**
 * 主页面
 */
private val needPermissions = arrayOf(
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.READ_PHONE_STATE
)
private const val PERMISSON_REQUESTCODE = 0

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>() {


    private val viewModel: WeatherViewModel by viewModels()

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private var isNeedCheck = true


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

    private val currentCity by Preference(
        Preference.CUR_CITY,
        CityBean(
            lon = "118.78",
            lat = "32.04",
            cityName = "南京",
            sortName = "NanJing"
        )
    )

    private val errorTip by lazy {
        val sb = Snackbar.make(viewBinding.root, "", Snackbar.LENGTH_INDEFINITE)
        sb.setAction("重试") {
            sb.dismiss()
        }
    }


    private val locationClient by lazy {
        AMapLocationClient(applicationContext).apply {
            val option = AMapLocationClientOption()
            option.apply {
                // 使用低功耗模式
                locationMode = AMapLocationClientOption.AMapLocationMode.Battery_Saving
                // 单次定位
                isOnceLocation = true
            }
            setLocationOption(option)
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

        viewBinding.btnPlus.setOnClickListener {
            startActivity(Intent(this, CitySelectActivity::class.java))
        }
    }

    override fun initializer() {
        FluidContentResizer.listen(this)
        initView()
        initTitle()
    }




    override fun onResume() {
        super.onResume()
        if (isNeedCheck) {
            checkPermissions()
        }
        getLocation()
    }


    private fun getLocation() {
        locationClient.setLocationListener { info ->
            kotlin.run {
                Timber.e("${info.longitude},${info.latitude}")
                locationClient.stopLocation()
                viewModel.changedCity(currentCity)
            }
        }
        locationClient.startLocation()
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

    /**
     * 显示提示信息
     */
    private fun showMissingPermissionDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("提示")
        builder.setMessage("当前应用缺少必要权限。\n\n请点击\"设置\"-\"权限\"-打开所需权限")

        // 拒绝, 退出应用
        builder.setNegativeButton(
            "取消"
        ) { _, _ -> finish() }
        builder.setPositiveButton(
            "设置"
        ) { _, _ ->
            startAppSettings()
        }
        builder.setCancelable(false)
        builder.show()
    }

    /**
     * 检查必要权限是否获取到
     */
    private fun checkPermissions(permissions: Array<String> = needPermissions) {
        val needRequestPermissonList: List<String> = findDeniedPermissions(permissions)
        if (needRequestPermissonList.isNotEmpty()
        ) {
            ActivityCompat.requestPermissions(
                this,
                needRequestPermissonList.toTypedArray(),
                PERMISSON_REQUESTCODE
            )
        }
    }


    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private fun verifyPermissions(grantResults: IntArray): Boolean {
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }


    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private fun findDeniedPermissions(permissions: Array<String>): List<String> {
        val needRequestPermissonList: MutableList<String> = ArrayList()
        for (perm in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    perm
                ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm
                )
            ) {
                needRequestPermissonList.add(perm)
            }
        }
        return needRequestPermissonList
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(grantResults)) {
                showMissingPermissionDialog()
                isNeedCheck = false
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

    override fun onDestroy() {
        super.onDestroy()
        locationClient.onDestroy()
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