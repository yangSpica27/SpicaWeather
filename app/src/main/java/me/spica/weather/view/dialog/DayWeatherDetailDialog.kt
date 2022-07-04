package me.spica.weather.view.dialog

import android.animation.ValueAnimator
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.fragment.app.FragmentManager
import me.spica.weather.databinding.DialogDayDetailBinding
import me.spica.weather.tools.dp

class DayWeatherDetailDialog(private val screenActivity: Activity) : BaseDialogFragment<DialogDayDetailBinding>() {


  // 进入动画
  private var enterOrExitAnim: ValueAnimator? = null


  override fun init() {
    viewBinding.layoutBg.setScreenshotActivity(screenActivity)
  }


  override fun show(manager: FragmentManager, tag: String?) {
    super.show(manager, tag)
    if (enterOrExitAnim!=null){
      createEnterAnim()
    }
    enterOrExitAnim?.start()
  }




  // 创建进入动画
  private fun createEnterAnim() {
    if (enterOrExitAnim?.isRunning == true) enterOrExitAnim?.cancel()
    enterOrExitAnim = ValueAnimator.ofFloat(viewBinding.layoutBg.height, 20.dp)
    enterOrExitAnim?.apply {
      duration = 550L
      addUpdateListener {
        viewBinding.layoutBg.translationY = it.animatedValue as Float
        val zoomScale: Float = 1 - (viewBinding.layoutBg.getHeight() - viewBinding.layoutBg.translationY) * 0.00002f
        viewBinding.layoutBg.scaleX = zoomScale
        viewBinding.layoutBg.scaleY = zoomScale
      }
    }
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewBinding.layoutBg.setOnClickListener {
      createExitAnim()
      enterOrExitAnim?.start()
    }
    viewBinding.layoutBg.postDelayed({
      createEnterAnim()
      enterOrExitAnim?.start()
    }, 500)
  }

  // 创建退出动画
  private fun createExitAnim() {
    if (enterOrExitAnim?.isRunning == true) enterOrExitAnim?.cancel()
    enterOrExitAnim = ValueAnimator.ofFloat(viewBinding.layoutBg.translationY, viewBinding.layoutBg.height)
    enterOrExitAnim?.apply {
      duration = 550L
      addUpdateListener {
        viewBinding.layoutBg.translationY = it.animatedValue as Float
        val zoomScale: Float = 1 - (viewBinding.layoutBg.getHeight() - viewBinding.layoutBg.translationY) * 0.00002f
        viewBinding.layoutBg.scaleX = zoomScale
        viewBinding.layoutBg.scaleY = zoomScale
        doOnEnd {
          dismiss()
        }
      }
    }
  }

  override fun setupViewBinding(inflater: LayoutInflater, container: ViewGroup?): DialogDayDetailBinding =
    DialogDayDetailBinding.inflate(inflater, container, false)
}