package me.spica.weather.view.dialog

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout
import android.widget.ScrollView

class ContainerConstraintLayout : RelativeLayout {

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


  // y轴变化监听
  var yChangedListener: (y: Float) -> Unit = {}


  override fun setTranslationY(translationY: Float) {
    super.setTranslationY(translationY)
    yChangedListener(translationY)
  }


  override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
    val scrollView: ScrollView? = getChildAt(0) as ScrollView?
    scrollView?.let {
      return true
    }
    return super.onInterceptTouchEvent(ev)
  }

}