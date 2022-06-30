package me.spica.weather.ui.life

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransform.FADE_MODE_THROUGH
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import me.spica.weather.R
import me.spica.weather.base.BindingActivity
import me.spica.weather.databinding.ActivityLifeBinding

// 生活指数详情
private const val shareContainerTransformName = "life_container"

class LifeActivity : BindingActivity<ActivityLifeBinding>() {


  override fun initializer() {

  }

  companion object {
    fun startActivity(context: Activity, startView: View) {
      val intent = Intent(context, LifeActivity::class.java)
      startView.transitionName = shareContainerTransformName
      val options = ActivityOptions.makeSceneTransitionAnimation(
        context,
        startView,
        "life_container"
      )
      context.startActivity(intent, options.toBundle())
    }

  }

  override fun onCreate(savedInstanceState: Bundle?) {
    window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
    findViewById<View>(android.R.id.content).transitionName = shareContainerTransformName
    setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
    window.sharedElementEnterTransition = MaterialContainerTransform().apply {
      addTarget(android.R.id.content)
      duration = 500L
      startContainerColor = Color.WHITE
      endContainerColor = getColor(R.color.window_background)
      fadeMode = FADE_MODE_THROUGH
    }
    window.sharedElementReturnTransition = MaterialContainerTransform().apply {
      addTarget(android.R.id.content)
      duration = 550L
      startContainerColor = getColor(R.color.window_background)
      endContainerColor = Color.WHITE
      fadeMode = FADE_MODE_THROUGH
    }
    super.onCreate(savedInstanceState)
  }

  override fun setupViewBinding(inflater: LayoutInflater): ActivityLifeBinding =
    ActivityLifeBinding.inflate(inflater)
}