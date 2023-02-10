package me.spica.weather.view.line

import android.content.Context
import android.graphics.*
import android.graphics.Paint.FontMetricsInt
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import me.spica.weather.R
import me.spica.weather.common.getThemeColor
import me.spica.weather.model.weather.HourlyWeatherBean
import me.spica.weather.tools.dp
import me.spica.weather.tools.getColorWithAlpha
import java.text.SimpleDateFormat
import java.util.*

// 宽度75dp 高度200dp
class HourlyForecastView : View {

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  private val weathers: ArrayList<HourlyWeatherBean> = arrayListOf() // 数据源


  fun setData(weathers: List<HourlyWeatherBean>) {
    this.weathers.clear()
    this.weathers.addAll(weathers)
    init()
  }

  private fun init() {
    // 获取最高气温和最低气温
    val sorts = weathers.toList().sortedBy { it.temp }
    minTemp = sorts.first().temp
    maxTemp = sorts.last().temp

    tempLinePath.reset()
    shadowPath.reset()

    dashLineList.clear()
    weathers.forEachIndexed { index, hourlyWeatherBean ->
      // 横坐标
      val w: Float = (ITEM_WIDTH * index + paddingL)
      // 纵坐标
      val h: Float = tempHeightPixel(hourlyWeatherBean.temp)
      // 当前点
      val point = Point(w.toInt(), h.toInt())
      mPointList.add(point)

      if (index != 0 && hourlyWeatherBean.getWeatherType() != weathers[index - 1].getWeatherType()) {
        dashLineList.add(index)
      }


      if (dashLineList.contains(index)) {
        dashWidth.add(w)
        dashHeight.add(h)
      }
      if (index == 0) {
        // 使用当前的做线段的主题色
        val themeColor = hourlyWeatherBean.getWeatherType().getThemeColor()
        tempLinePaint.color = themeColor
        dashLinePaint.color = themeColor
        shadowPaint.shader = LinearGradient(
          0f,
          0f,
          0f,
          paddingT + ITEM_MIN_HEIGHT + TEMP_TEXT_HEIGHT + TEMP_HEIGHT_SECTION,
          getColorWithAlpha(.4f, themeColor),
          Color.TRANSPARENT,
          Shader.TileMode.CLAMP
        )
      }
    }
    weathers.forEachIndexed { index, _ ->

      val point = mPointList[index]

      // 上个点
      val lastPoint: Point

      // 上上个点
      val lastLastPoint: Point

      // 下一个点
      val nextPoint: Point


      // 前2点 最后面的点 缺少点用当前点补缺
      when (index) {
        0 -> {
          // 起始点
          lastPoint = Point(point.x, point.y)
          lastLastPoint = Point(point.x, point.y)
          nextPoint = Point(mPointList[index + 1].x, mPointList[index + 1].y)
          tempLinePath.moveTo(point.x * 1f, point.y * 1f)
          shadowPath.moveTo(point.x * 1f, point.y * 1f)

        }
        1 -> {
          lastPoint = Point(point.x, point.y)
          lastLastPoint = Point(point.x, point.y)
          nextPoint = Point(mPointList[index + 1].x, mPointList[index + 1].y)
        }
        weathers.size - 1 -> {
          // 结束点
          lastLastPoint = Point(mPointList[index - 2].x, mPointList[index - 2].y)
          lastPoint = Point(mPointList[index - 1].x, mPointList[index - 1].y)
          nextPoint = Point(point.x, point.y)
        }
        else -> {
          // 中间点
          lastLastPoint = Point(mPointList[index - 2].x, mPointList[index - 2].y)
          lastPoint = Point(mPointList[index - 1].x, mPointList[index - 1].y)
          nextPoint = Point(mPointList[index + 1].x, mPointList[index + 1].y)
        }
      }

      if (index != 0) {
        // 第一个点是起始点 不用计算曲线 从第二点开始
        // 求出两个锚点的坐标差
        val firstDiffX: Float = point.x * 1f - lastLastPoint.x
        val firstDiffY: Float = point.y * 1f - lastLastPoint.y

        val secondDiffX: Float = nextPoint.x * 1f - lastPoint.x
        val secondDiffY: Float = nextPoint.y * 1f - lastPoint.y


        // 根据锚点间坐标差 求出 两个控制点 参数给的越大 斜率越小

        val controlPointLeft = PointF(
          lastPoint.x + 0.1f * firstDiffX, lastPoint.y + 0.1f * firstDiffY
        )

        val controlPointRight = PointF(
          point.x - 0.1f * secondDiffX, point.y - 0.1f * secondDiffY
        )

        // 做二阶贝塞尔
        tempLinePath.cubicTo(
          controlPointLeft.x, controlPointLeft.y, controlPointRight.x, controlPointRight.y, point.x * 1f, point.y * 1f
        )

        shadowPath.cubicTo(
          controlPointLeft.x, controlPointLeft.y, controlPointRight.x, controlPointRight.y, point.x * 1f, point.y * 1f
        )

      }
    }

    shadowPath.lineTo(
      mPointList.last().x * 1f, paddingT + ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + TEMP_TEXT_HEIGHT
    )

    shadowPath.lineTo(
      paddingL, paddingT + ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + TEMP_TEXT_HEIGHT
    )

    shadowPath.close()
    postInvalidate()

  }

  private var minTemp = 0

  private var maxTemp = 0


  private val screenWidth by lazy {
    val dm = resources.displayMetrics
    dm.widthPixels
  }

  companion object {
    private val ITEM_WIDTH = 75.dp // 每个单元的宽度

    private val ITEM_MIN_HEIGHT = 35.dp // 每个单元最低的高度

    private val TEMP_TEXT_HEIGHT = 24.dp // 预留给温度指示器的间距

    private val DATE_TEXT_HEIGHT = 20.dp // 日期文本预留空间

    private val LEVEL_RECT_HEIGHT = 18.dp // 风力等级治时期高度

    private val TEMP_HEIGHT_SECTION = 25.dp // 温度折线图高度可变区间

  }


  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    setMeasuredDimension(
      (ITEM_WIDTH * (weathers.size - 1) + paddingL + paddingR).toInt(), (ITEM_MIN_HEIGHT + paddingT + paddingB + DATE_TEXT_HEIGHT + LEVEL_RECT_HEIGHT + TEMP_TEXT_HEIGHT + TEMP_HEIGHT_SECTION).toInt()
    )
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    drawPops(canvas)
    drawDate(canvas)
    drawLines(canvas)
    drawTemp(canvas)
  }

  private val tempTextRect = Rect()

  private val tempTextPaint = TextPaint().apply {
    textSize = 12.dp
    color = context.getColor(R.color.window_background)
  }
  private val tempTextBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
  }

  private fun drawTemp(canvas: Canvas) {
    val item = weathers[currentItemIndex]
    tempTextBgPaint.color = item.getWeatherType().getThemeColor()
    val showText = "${item.weatherName},${item.temp}℃"
    val y = (tempHeightPixel(item.temp)).toInt()
    val offset = ITEM_WIDTH / 4f
    tempTextRect.left = getScrollBarX().toInt()
    tempTextRect.top = (y - TEMP_TEXT_HEIGHT).toInt()
    tempTextRect.right = (getScrollBarX() + offset).toInt()
    tempTextRect.bottom = (y - TEMP_TEXT_HEIGHT + 20.dp).toInt()

    val fontMetrics: FontMetricsInt = tempTextPaint.fontMetricsInt
    val baseline: Int = (tempTextRect.bottom + tempTextRect.top - fontMetrics.bottom - fontMetrics.top) / 2

    tempTextPaint.textAlign = Paint.Align.LEFT
    dateTextPaint.getTextBounds(showText, 0, showText.toCharArray().size, textRect)

    canvas.drawRoundRect(
      tempTextRect.centerX() - 4.dp,
      tempTextRect.top * 1f,
      tempTextRect.centerX() + textRect.width().toFloat(),
      tempTextRect.bottom * 1f, 3.dp, 3.dp, tempTextBgPaint
    )

    tempTextBgPaint.color = getColorWithAlpha(.1f, tempTextBgPaint.color)

    canvas.drawRoundRect(
      tempTextRect.centerX() - ITEM_WIDTH / 2f + textRect.width() / 2f,
      paddingT / 2f,
      tempTextRect.centerX() + ITEM_WIDTH / 2f + textRect.width() / 2f,
      height * 1f - paddingB / 2f,
      4.dp,
      4.dp,
      tempTextBgPaint
    )

    canvas.drawText(showText, tempTextRect.centerX().toFloat(), baseline.toFloat(), tempTextPaint)

  }

  private val dateTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    .apply {
      textSize = 14.dp
      color = context.getColor(R.color.textColorPrimaryHint)
    }

  private val sdfHHMM = SimpleDateFormat("HH:mm", Locale.CHINA)

  private val textRect = Rect()

  // 绘制事件
  private fun drawDate(canvas: Canvas) {
    weathers.forEachIndexed { index, hourlyWeatherBean ->
      val drawText = sdfHHMM.format(hourlyWeatherBean.fxTime())
      dateTextPaint.getTextBounds(drawText, 0, drawText.toCharArray().size, textRect)

      canvas.drawText(
        drawText,
        ITEM_WIDTH * index + paddingL - textRect.width() / 2f,
        DATE_TEXT_HEIGHT + paddingT + ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + LEVEL_RECT_HEIGHT + TEMP_TEXT_HEIGHT,
        dateTextPaint
      )
    }
  }


  private val dashWidth: MutableList<Float> = arrayListOf()

  private val dashHeight: MutableList<Float> = arrayListOf()

  private val mPointList: MutableList<Point> = arrayListOf()

  //画虚线的点的index
  private val dashLineList: MutableList<Int> = arrayListOf()


  private val paddingL = 20.dp
  private val paddingT = 12.dp

  private val paddingR = 20.dp
  private val paddingB = 12.dp


  // 气温曲线的轨迹
  private val tempLinePath = Path()

  // 阴影背景的Path
  private val shadowPath = Path()


  // 气温折线的配套画笔
  private val tempLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    strokeWidth = 3.dp
    style = Paint.Style.STROKE
    strokeCap = Paint.Cap.ROUND
  }

  private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
  }

  private val dashLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.STROKE
    pathEffect = DashPathEffect(floatArrayOf(8f, 8f, 8f, 8f), 1f)
    strokeWidth = 3f
  }

  // 用于绘制矩形
  private val levelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    .apply {
      style = Paint.Style.FILL
    }

  private val roundRect = RectF()
  private fun drawLines(canvas: Canvas) {
    // 绘制气温折线

    dashLineList.forEachIndexed { index, i ->
      canvas.drawLine(
        mPointList[i].x * 1f,
        mPointList[i].y * 1f,
        mPointList[i].x * 1f,
        paddingT + ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + TEMP_TEXT_HEIGHT,
        dashLinePaint
      )

      levelPaint.color = getColorWithAlpha(
        .2f,
        weathers[i].getWeatherType().getThemeColor()
      )

      // 绘制下方的色块
      roundRect.setEmpty()

      roundRect.left = if (index != 0) {
        ITEM_WIDTH * i + paddingL + 2.dp
      } else {
        paddingL + 2.dp
      }
      roundRect.right = if (index == dashLineList.size - 1) {
        width - paddingR - 2.dp
      } else {
        ITEM_WIDTH * dashLineList[index + 1] + paddingL - 2.dp
      }
      roundRect.top = ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + paddingT + TEMP_TEXT_HEIGHT + 4
      roundRect.bottom = paddingT + ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + LEVEL_RECT_HEIGHT + TEMP_TEXT_HEIGHT + 4

      canvas.drawRoundRect(
        roundRect,
        2.dp,
        2.dp,
        levelPaint
      )
    }

    canvas.drawPath(tempLinePath, tempLinePaint)
    canvas.drawPath(shadowPath, shadowPaint)

  }


  // 计算对应温度所对应的Y轴坐标
  private fun tempHeightPixel(temp: Int): Float {
    val res: Float = (temp - minTemp) * 1f / (maxTemp - minTemp) * (TEMP_HEIGHT_SECTION) + ITEM_MIN_HEIGHT
    return ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + paddingT + TEMP_TEXT_HEIGHT - res //y从上到下

  }

  private val popPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    .apply {
      style = Paint.Style.FILL
    }


  private fun drawPops(canvas: Canvas) {
    mPointList.forEachIndexed { index, point ->

      if (weathers[index].pop >= 40) {
        popPaint.color = context.getColor(R.color.light_blue_200)
      } else {
        popPaint.color = context.getColor(R.color.light_blue_50)
      }

      canvas.drawRoundRect(
        point.x - 6.dp,
        ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + paddingT + TEMP_TEXT_HEIGHT,
        point.x + 6.dp,
        ITEM_MIN_HEIGHT + TEMP_HEIGHT_SECTION + paddingT + TEMP_TEXT_HEIGHT -
            ((weathers[index].pop) * 1f / (100f) * (TEMP_HEIGHT_SECTION + ITEM_MIN_HEIGHT)), 3.dp, 3.dp,
        popPaint
      )
    }
  }


  private var maxScrollOffset = 0 //滚动条最长滚动距离

  private var scrollOffset = 0 //滚动条偏移量

  private var currentItemIndex = 0 //当前滚动的位置所对应的item下标


  //设置scrollerView的滚动条的位置，通过位置计算当前的时段
  fun setScrollOffset(offset: Int, maxScrollOffset: Int) {
    this.maxScrollOffset = (maxScrollOffset + 50.dp).toInt()
    this.scrollOffset = offset
    currentItemIndex = calculateItemIndex()
    postInvalidate()
  }

  private fun calculateItemIndex(): Int {
    val x: Float = getScrollBarX()
    var sum: Float = (paddingL - ITEM_WIDTH / 2)
    for (i in 0 until weathers.size - 1) {
      sum += ITEM_WIDTH
      if (x < sum) {
        return i
      }
    }
    return weathers.size - 1
  }


  private fun getScrollBarX(): Float {
    var x: Float = (weathers.size - 1) * ITEM_WIDTH * scrollOffset * 1f / maxScrollOffset
    x = (x - 3.dp)
    return x
  }


}