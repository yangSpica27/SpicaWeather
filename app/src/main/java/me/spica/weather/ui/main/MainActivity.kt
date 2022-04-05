package me.spica.weather.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import me.spica.weather.R
import me.spica.weather.base.BindingActivity
import me.spica.weather.databinding.ActivityMainBinding
import me.spica.weather.model.city.CityBean
import me.spica.weather.tools.dp
import me.spica.weather.tools.keyboard.FluidContentResizer
import me.spica.weather.tools.toast
import me.spica.weather.ui.city.CitySelectActivity
import me.spica.weather.ui.city.WeatherCityActivity
import me.spica.weather.ui.setting.SettingActivity
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

    private val viewModel: MainViewModel by viewModels()


    private val mainPagerAdapter = MainPagerAdapter(this)

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
                viewModel.changeCity(
                    cityList.first()
                )
            }
        }
    }

    // 百度的定位client
    @Inject
    lateinit var locationClint: LocationClient

    override fun onCreate(savedInstanceState: Bundle?) {
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
            startActivity(Intent(this, SettingActivity::class.java))
        }

        viewBinding.btnPlus.setOnClickListener {
            startActivity(Intent(this, CitySelectActivity::class.java))
        }
    }

    override fun initializer() {
        FluidContentResizer.listen(this)
        initSetting()
        initView()
        initTitle()
    }


    // 初始化设置项
    private fun initSetting() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
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


    @SuppressLint("NotifyDataSetChanged")
    private fun initView() {

        viewBinding.viewPager.adapter = mainPagerAdapter

        lifecycleScope.launch {

            viewModel.allCityFlow.collectLatest {
                val isFirst = mainPagerAdapter.diffUtil.currentList.size == 0
                mainPagerAdapter.diffUtil.submitList(it)

            }
        }


        viewBinding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.selectCity(mainPagerAdapter.diffUtil.currentList[position])
            }
        })

        lifecycleScope.launch {
            viewModel.selectCityFlow.filterNotNull().collect {
                viewBinding.toolbar.tsLocation.setText("中国，${it.cityName}")
            }
        }

    }

//    private val errorTip by lazy {
//        Snackbar.make(viewBinding.root, "请求过程中发生错误！", Snackbar.LENGTH_LONG)
//            .setAction("重试") {
//                currentCity?.let {
//                    viewModel.changedCity(it)
//                }
//            }
//    }

    override fun setupViewBinding(inflater: LayoutInflater): ActivityMainBinding =
        ActivityMainBinding.inflate(inflater)


    private fun syncNewCity(cityName: String) {
        lifecycleScope.launch(Dispatchers.Default) {
            cityList.forEach {
                if (cityName.contains(it.cityName)) {
                    if (viewModel.getAllCity().contains(it)) {
                        Snackbar.make(
                            viewBinding.root,
                            "检测您所在城市在${it.cityName},正在切换",
                            Snackbar.LENGTH_LONG
                        ).show()
                        viewModel.changeCity(it)
                        return@launch
                    }
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
