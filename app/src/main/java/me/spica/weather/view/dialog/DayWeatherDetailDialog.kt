package me.spica.weather.view.dialog

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.core.animation.doOnEnd
import androidx.fragment.app.FragmentManager
import me.spica.weather.databinding.DialogDayDetailBinding
import me.spica.weather.tools.dp


class DayWeatherDetailDialog(private val screenActivity: Activity) :
  BaseDialogFragment<DialogDayDetailBinding>() {


  // 进入动画
  private var enterOrExitAnim: ValueAnimator? = null


  private var touchDownY = 0f
  private var oldY = 0f
  private var isTouch = false

  @SuppressLint("ClickableViewAccessibility")
  override fun init() {
    viewBinding.layoutBg.setScreenshotActivity(screenActivity)
    viewBinding.containerMain.yChangedListener = {
      val zoomScale: Float = 1 - (viewBinding.layoutBg.getHeight() - viewBinding.containerMain.translationY) * 0.00004f
      viewBinding.layoutBg.scaleX = zoomScale
      viewBinding.layoutBg.scaleY = zoomScale
      viewBinding.layoutBg.setRadius(
        18.dp *
            (viewBinding.containerMain.height - viewBinding.containerMain.translationY) / viewBinding.containerMain.height
      )
    }
    viewBinding.dialogTap.setOnTouchListener { _, motionEvent ->
      run {

        when (motionEvent.action) {
          MotionEvent.ACTION_DOWN -> {
            isTouch = true
            oldY = motionEvent.y
          }

          MotionEvent.ACTION_MOVE -> {
            if (isTouch) {
              touchDownY = motionEvent.y
              val changeY = touchDownY - oldY
              viewBinding.containerMain.translationY = viewBinding.containerMain.translationY + changeY
            }
          }

          MotionEvent.ACTION_UP,
          MotionEvent.ACTION_CANCEL -> {
            isTouch = false
            if (viewBinding.containerMain.translationY > viewBinding.containerMain.height / 2) {
              createExitAnim()
              enterOrExitAnim?.start()
            } else {
              createEnterAnim()
              enterOrExitAnim?.start()
            }
          }
        }

        return@setOnTouchListener true
      }
    }

  }


  override fun show(manager: FragmentManager, tag: String?) {
    //super.show(manager, tag);
    val ft = manager.beginTransaction()
    ft.add(this, tag)
    ft.commitAllowingStateLoss()
    if (enterOrExitAnim != null) {
      createEnterAnim()
    }
    enterOrExitAnim?.start()
  }

  override fun onResume() {
    super.onResume()
    view?.isFocusableInTouchMode = true
    view?.requestFocus()
    view?.setOnKeyListener(View.OnKeyListener { _, i, keyEvent ->
      if (keyEvent.action == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK) {
        if (dialog?.isShowing == true) {
          dismiss()
          return@OnKeyListener true
        }
        return@OnKeyListener false
      }
      false
    })
  }

  // 创建进入动画
  private fun createEnterAnim() {
    if (enterOrExitAnim?.isRunning == true) enterOrExitAnim?.cancel()
    if (viewBinding.containerMain.translationY != 0f) {
      enterOrExitAnim = ValueAnimator.ofFloat(viewBinding.containerMain.translationY, 70.dp)
    } else {
      enterOrExitAnim = ValueAnimator.ofFloat(viewBinding.containerMain.height * 1f, 70.dp)
    }
    enterOrExitAnim?.apply {
      duration = 550L
      addUpdateListener {
        viewBinding.containerMain.translationY = it.animatedValue as Float
      }
    }
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewBinding.layoutBg.post {
      createEnterAnim()
      enterOrExitAnim?.start()
    }
  }

  // 创建退出动画
  private fun createExitAnim() {
    if (enterOrExitAnim?.isRunning == true) enterOrExitAnim?.cancel()
    enterOrExitAnim = ValueAnimator.ofFloat(viewBinding.containerMain.translationY, viewBinding.containerMain.height * 1f)
    enterOrExitAnim?.apply {
      duration = 550L
      addUpdateListener {
        viewBinding.containerMain.translationY = it.animatedValue as Float
        doOnEnd {
          dismissAllowingStateLoss()
        }
      }
    }
  }

  override fun dismiss() {
    createExitAnim()
    enterOrExitAnim?.start()
  }


  override fun setupViewBinding(inflater: LayoutInflater, container: ViewGroup?): DialogDayDetailBinding =
    DialogDayDetailBinding.inflate(inflater, container, false)
}