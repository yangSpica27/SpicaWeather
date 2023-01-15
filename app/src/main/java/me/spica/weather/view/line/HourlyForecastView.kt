package me.spica.weather.view.line

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import me.spica.weather.model.weather.HourlyWeatherBean
import me.spica.weather.tools.dp

// 宽度75dp 高度200dp
class HourlyForecastView : View, ScrollWatcher {

  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  private val weathers: ArrayList<HourlyWeatherBean> = arrayListOf() // 数据源


  // 折线可用最高点
  private var topY = 48.dp + 20.dp

  // 折线可用最低点
  private var bottomY = 48.dp + 40.dp + 30.dp

  private var bottomLineY = 200.dp - 40.dp

  private val screenWidth by lazy {
    val dm = resources.displayMetrics
    dm.widthPixels
  }

  companion object {
    private val ITEM_WIDTH = 75.dp
  }


  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
    val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
    val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
    val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    drawLines(canvas)
    drawTemps(canvas)
  }

  private var baseLineHeight = 0f

  val dashWidth: MutableList<Float> = arrayListOf()

  val dashHeight: MutableList<Float> = arrayListOf()

  val mPointList: MutableList<Point> = arrayListOf()

  //画虚线的点的index
  private val dashLineList: MutableList<Int> = arrayListOf()

  private val paddingL = 0
  private val paddingT = 0
  private val paddingR = 0
  private val paddingB = 0

  private fun drawLines(canvas: Canvas) {
    weathers.forEachIndexed { index, hourlyWeatherBean ->
      val w: Float = (ITEM_WIDTH * index + paddingL)
      val h: Float = tempHeightPixel(hourlyWeatherBean.temp) + paddingT
      val point = Point(w.toInt(), h.toInt())
      mPointList.add(point)
      if (dashLineList.contains(index)) {
        dashWidth.add(w)
        dashHeight.add(h)
      }
    }
  }


  fun tempHeightPixel(temp:Int):Float{
    return 0f
  }


  private fun drawTemps(canvas: Canvas) {

  }

  override fun update(scrollX: Int) {

  }


}