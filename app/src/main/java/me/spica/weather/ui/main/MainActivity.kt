package me.spica.weather.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import me.spica.weather.R
import me.spica.weather.base.BindingActivity
import me.spica.weather.databinding.ActivityMainBinding
import me.spica.weather.tools.addNewFragment
import me.spica.weather.tools.hide
import me.spica.weather.tools.show
import me.spica.weather.tools.showOldFragment
import me.spica.weather.ui.city.CityFragment
import me.spica.weather.ui.home.HomeFragment
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


    private val viewModel: MainViewModel by viewModels()

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private var isNeedCheck = true


    private val homeFragment by lazy {
        HomeFragment()
    }

    private val cityFragment by lazy {
        CityFragment()
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


    override fun initializer() {

        initFragment()

        // 监听点击底栏
        viewBinding.rgBottom.setOnCheckedChangeListener { _, id ->
            runBlocking {
                if (id == viewBinding.btnHome.id) {
                    // 点击主页
                    if (viewBinding.btnHome.isSelected) {
                        return@runBlocking
                    }

                    showOldFragment(homeFragment, listOf(cityFragment))

                    viewBinding.toolbar.root.show()
                }

                if (id == viewBinding.btnLocation.id) {
                    // 点击了地区
                    if (viewBinding.btnLocation.isSelected) {
                        return@runBlocking
                    }

                    supportFragmentManager.commit {
                        // 点击了。。。
                        showOldFragment(cityFragment, listOf(homeFragment))
                    }

                    viewBinding.toolbar.root.hide()

                }
                if (id == viewBinding.btnMd.id) {
                    if (viewBinding.btnMd.isSelected) {
                        return@runBlocking
                    }

                    viewBinding.toolbar.root.hide()

                }

            }
        }

        // 监听fab点击
        viewBinding.btnPlus.setOnClickListener {
            viewBinding.btnPlus.isSelected = !viewBinding.btnPlus.isSelected
            if (viewBinding.btnPlus.isSelected) {
                viewBinding.btnPlus.animate().rotation(135f)
                viewBinding.viewMenu.animate().scaleX(1F).scaleY(1F).alpha(1F)
            } else {
                viewBinding.btnPlus.animate().rotation(0f)
                viewBinding.viewMenu.animate().scaleX(0F).scaleY(0F).alpha(0F)
            }

        }


    }

    private fun initFragment() {
        addNewFragment(homeFragment, R.id.fragment_container_view)
        addNewFragment(cityFragment, R.id.fragment_container_view)
        showOldFragment(homeFragment, listOf(cityFragment))
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

                    viewModel.syncNowWeather(
                        this@MainActivity, Pair(
                            "${info.longitude}",
                            "${info.latitude}"
                        )
                    )

                    viewModel.sync7DayWeather(
                        this@MainActivity, Pair(
                            "${info.longitude}",
                            "${info.latitude}"
                        )
                    )


                viewModel.sync1DIndices(
                    this, Pair(
                        "${info.longitude}",
                        "${info.latitude}"
                    )
                )


            }
        }
        locationClient.startLocation()
    }


    /**
     * 显示提示信息
     *
     * @since 2.5.0
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
        ) { dialog, _ ->
            startAppSettings()
        }
        builder.setCancelable(false)
        builder.show()
    }

    /**
     * @since 2.5.0
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


}