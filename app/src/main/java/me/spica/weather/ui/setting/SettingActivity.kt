package me.spica.weather.ui.setting

import android.view.LayoutInflater
import me.spica.weather.base.BindingActivity
import me.spica.weather.databinding.ActivitySettingBinding

class SettingActivity : BindingActivity<ActivitySettingBinding>() {

    override fun initializer() {

    }

    override fun setupViewBinding(inflater: LayoutInflater): ActivitySettingBinding =
        ActivitySettingBinding.inflate(inflater)
}
