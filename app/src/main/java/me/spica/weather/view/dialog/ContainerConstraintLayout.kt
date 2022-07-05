package me.spica.weather.view.dialog

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

class ContainerConstraintLayout : ConstraintLayout {

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


  // y轴变化监听
  var yChangedListener: (y: Float) -> Unit = {}


  override fun setTranslationY(translationY: Float) {
    super.setTranslationY(translationY)
    yChangedListener(translationY)
  }


}