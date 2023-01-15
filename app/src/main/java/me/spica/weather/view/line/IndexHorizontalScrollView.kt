package me.spica.weather.view.line

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.HorizontalScrollView

class IndexHorizontalScrollView : HorizontalScrollView {


  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  private val hourlyForecastView: HourlyForecastView by lazy {
    getChildAt(0) as HourlyForecastView
  }

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    val offset = computeHorizontalScrollOffset()
    val maxOffset: Int = computeHorizontalScrollRange() - getScreenWidth(context)
//      hourlyForecastView.setScrollOffset(offset, maxOffset)

  }

  private fun getScreenWidth(context: Context): Int {
    val dm = context.resources.displayMetrics
    return dm.widthPixels
  }

}