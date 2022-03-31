package me.spica.weather.ui.setting

import android.view.LayoutInflater
import me.spica.weather.R
import me.spica.weather.base.BindingActivity
import me.spica.weather.databinding.ActivitySettingBinding

/**
 * 设置页面
 */
class SettingActivity : BindingActivity<ActivitySettingBinding>() {

    override fun initializer() {
        initToolbar()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, SettingFragment())
            .commit()
    }

    /**
     * 初始化顶栏
     */
    private fun initToolbar(){
        viewBinding.icBack.setOnClickListener {
            finish()
        }
    }

    override fun setupViewBinding(inflater: LayoutInflater): ActivitySettingBinding =
        ActivitySettingBinding.inflate(inflater)



}
