package me.spica.weather.ui.webview

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.WebView
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransform.FADE_MODE_CROSS
import com.google.android.material.transition.platform.MaterialContainerTransform.FADE_MODE_THROUGH
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import me.spica.weather.R
import me.spica.weather.base.BindingActivity
import me.spica.weather.databinding.ActivityWebviewBinding
import me.spica.weather.tools.doOnMainThreadIdle
import me.spica.weather.ui.life.LifeActivity
import me.spica.weather.view.WebViewPool


private const val url_tag_name = "url"
private const val shareContainerTransformName = "web_container"

class WebViewActivity : BindingActivity<ActivityWebviewBinding>() {


  private lateinit var webView: WebView

  companion object {
    fun startActivity(context: Activity, startView: View, url: String) {
      val intent = Intent(context, WebViewActivity::class.java)
      intent.putExtra(url_tag_name, url)
      startView.transitionName = shareContainerTransformName
      val options = ActivityOptions.makeSceneTransitionAnimation(
        context,
        startView,
        shareContainerTransformName
      )
      context.startActivity(intent, options.toBundle())
    }

  }


  override fun initializer() {
    val url = intent.getIntExtra(url_tag_name, 0).toString()
    doOnMainThreadIdle(
      {
        webView = WebViewPool.instance.getWebView(this)
        viewBinding.root.addView(
          webView,
          ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
          )
        )
        webView.loadUrl(url)
      }, 500
    )
  }

  override fun onBackPressed() {
    super.onBackPressed()
    WebViewPool.instance.removeWebView(viewBinding.root, webView)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
    findViewById<View>(android.R.id.content).transitionName = shareContainerTransformName
    setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
    window.sharedElementEnterTransition = MaterialContainerTransform().apply {
      addTarget(android.R.id.content)
      duration = 400L
      startContainerColor = Color.WHITE
      endContainerColor = getColor(R.color.window_background)
      fadeMode = FADE_MODE_THROUGH
    }

    window.sharedElementReturnTransition = MaterialContainerTransform().apply {
      addTarget(android.R.id.content)
      duration = 450L
      startContainerColor = getColor(R.color.window_background)
      endContainerColor = getColor(R.color.white)
      fadeMode = FADE_MODE_CROSS
    }
    super.onCreate(savedInstanceState)
  }


  override fun setupViewBinding(inflater: LayoutInflater): ActivityWebviewBinding = ActivityWebviewBinding.inflate(inflater)
}