package me.spica.weather.ui.setting

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import me.spica.weather.R
import me.spica.weather.ui.about.AboutActivity


/** 设置页面 **/
class SettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_setting, rootKey)
        val aboutPreferences = findPreference<Preference>("about")
        aboutPreferences?.intent = Intent(requireContext(), AboutActivity::class.java)
    }
}