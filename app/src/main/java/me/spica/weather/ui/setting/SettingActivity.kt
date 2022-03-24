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
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, SettingFragment())
            .commit()
    }

    override fun setupViewBinding(inflater: LayoutInflater): ActivitySettingBinding =
        ActivitySettingBinding.inflate(inflater)



}
