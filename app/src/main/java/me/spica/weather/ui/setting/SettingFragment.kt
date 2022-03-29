package me.spica.weather.ui.setting

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import me.spica.weather.R

class SettingFragment :PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_setting, rootKey)
    }
}