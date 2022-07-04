package me.spica.weather.view.dialog

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import me.spica.weather.databinding.DialogDayDetailBinding

class DayWeatherDetailDialog(private val screenActivity: Activity) : BaseDialogFragment<DialogDayDetailBinding>() {


  override fun init() {
    viewBinding.layoutBg.setScreenshotActivity(screenActivity)

  }

  override fun setupViewBinding(inflater: LayoutInflater, container: ViewGroup?): DialogDayDetailBinding =
    DialogDayDetailBinding.inflate(inflater, container, false)
}