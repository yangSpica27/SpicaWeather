@file:Suppress("unused")

package me.spica.weather.view.line

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.*
import android.widget.Scroller
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import me.spica.weather.R
import me.spica.weather.common.getIconRes
import me.spica.weather.common.getThemeColor
import me.spica.weather.model.weather.HourlyWeatherBean
import me.spica.weather.tools.dp
import java.text.SimpleDateFormat
import java.util.*


// 宽度75dp 高度200dp
class MiuiWeatherLineView : View {


  // 最高气温
  private var maxTemp = 0

  // 最低气温
  private var minTemp = 0


  // 折线可用最高点
  private var topY = 48.dp + 20.dp

  // 折线可用最低点
  private var bottomY = 48.dp + 40.dp + 30.dp

  private var bottomLineY = 200.dp - 40.dp


  private val dashXs: ArrayList<Float> = ArrayList() //不同天气之间虚线的x坐标集合

  private val icons: MutableMap<String, Bitmap> = mutableMapOf()


  private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    pathEffect = null
    style = Paint.Style.FILL
    strokeWidth = 3.dp
    isAntiAlias = true
    maskFilter = BlurMaskFilter(1.dp, BlurMaskFilter.Blur.SOLID)
  }


  companion object {
    private val ITEM_WIDTH = 75.dp
  }

  private val velocityTracker: VelocityTracker by lazy {
    VelocityTracker.obtain()
  }

  private val viewConfiguration by lazy {
    ViewConfiguration.get(context)
  }

  private val weathers: ArrayList<HourlyWeatherBean> = arrayListOf()// 数据源

  private val points: MutableList<PointF> = mutableListOf() //折线拐点的集合

  private val tempTextPaint = TextPaint().apply {
    textSize = 12.dp
    color = ContextCompat.getColor(context, R.color.textColorPrimary)
    strokeWidth = 0F
    style = Paint.Style.FILL
    textAlign = Paint.Align.CENTER
//        maskFilter = BlurMaskFilter(4.dp, BlurMaskFilter.Blur.SOLID)
  }

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


  private val textPaint = TextPaint()

  private val scroller = Scroller(context)

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    if (weathers.isEmpty()) return
    drawAxis(canvas)
    drawIcon(canvas)
  }


  private val weatherTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    .apply {
      textSize = 12.dp * 0.9f
      color = ContextCompat.getColor(context, R.color.textColorPrimary)
      strokeWidth = 0F
      style = Paint.Style.FILL
      textAlign = Paint.Align.CENTER
    }

  // 绘制天气图标
  private fun drawIcon(canvas: Canvas) {

    var leftUsedScreenLeft = false

    for (index in 0 until dashXs.size - 1) {
      val icon = icons.toList()[index]

      var minX = dashXs[index] // 左界
      var maxX = dashXs[index + 1] // 右边界

      if (minX < scrollX && maxX < scrollX + width) {
        minX = scrollX * 1f
        leftUsedScreenLeft = true
      }

      if (minX > scrollX && maxX > scrollX + width) {
        maxX = scrollX + width * 1f
      }

      var iconX = if (maxX - minX > 48.dp) {
        minX + (maxX - minX) / 2f
      } else {
        if (leftUsedScreenLeft) {
          maxX - 48.dp / 2f
        } else {
          minX + 48.dp / 2f
        }
      }


      if (maxX < scrollX) {
        iconX = maxX - 48.dp / 2f
      } else if (minX > scrollX + width) {
        iconX = minX + 48.dp / 2f
      } else if (minX < scrollX && maxX > scrollX + width) {
        iconX = scrollX + width / 2f
      }

      canvas.drawBitmap(icon.second,
        iconX,
        bottomLineY - icon.second.height - 12.dp,
        null)

    }


  }


  // 用于绘制坐标轴的虚线
  private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    pathEffect = DashPathEffect(floatArrayOf(4.dp, 2.dp), 0F)
    strokeWidth = 2.dp
  }

  // 用于绘制时间
  private val timeTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val textBound = Rect()

  // 用于绘制下雨概率
  private val popTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)

  // 分界时间 凌晨12点 区别于昨天和今天
  private val divisionDate by lazy {
    val calendar = Calendar.getInstance(Locale.CHINA)
    calendar.set(Calendar.HOUR_OF_DAY, 24)
    calendar.set(Calendar.MINUTE, 0)
    return@lazy calendar
  }

  // 被格式化的时间格式
  private val sdfAfter = SimpleDateFormat("m时", Locale.CHINA)


  // 绘制背景的位置
  private val bgPath = Path()

  private val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
  }

  // 绘制时间轴
  private fun drawAxis(canvas: Canvas) {
    canvas.save()

    dashXs.clear()

    weathers.forEachIndexed { index, hourlyWeatherBean ->

      pathPaint.shader = LinearGradient(
        0F,
        0F,
        0F,
        height.toFloat(),
        getColorWithAlpha(baseColor = hourlyWeatherBean.getWeatherType().getThemeColor()),
        Color.TRANSPARENT,
        Shader.TileMode.CLAMP
      )

      //
      val tempString = "${hourlyWeatherBean.temp}℃"
      tempTextPaint.getTextBounds(tempString, 0, tempString.length, textBound)

      val currentY = getCentY(hourlyWeatherBean)

      val baseLine = currentY - textBound.height() - 2.dp

      canvas.drawText(
        tempString,
        ITEM_WIDTH * (index + 1) - ITEM_WIDTH / 2f,
        baseLine,
        tempTextPaint
      )



      linePaint.color = hourlyWeatherBean.getWeatherType().getThemeColor()
      // 重新描述填充路径
      bgPath.reset()

      axisPaint.color = hourlyWeatherBean.getWeatherType().getThemeColor()
      textPaint.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
      textPaint.textSize = 14.dp


      // =============绘制顶部时间=============

      textPaint.color = Color.BLACK

      val timeText = if (divisionDate.time.before(hourlyWeatherBean.fxTime)) {
        "次日" + sdfAfter.format(hourlyWeatherBean.fxTime)
      } else {
        sdfAfter.format(hourlyWeatherBean.fxTime)
      }

      textPaint.getTextBounds(timeText, 0, timeText.length, textBound)

      canvas.drawText(
        timeText,
        ITEM_WIDTH * (index + 1) - ITEM_WIDTH / 2f - textBound.width() / 2f,
        textBound.height() + 12.dp,
        textPaint
      )
      //======================================

      when (index) {
        0 -> {
          val nextY = getCentY(weathers[1])

          bgPath.moveTo(ITEM_WIDTH / 2f, currentY)
          bgPath.lineTo(ITEM_WIDTH / 2f, bottomLineY)
          bgPath.lineTo(ITEM_WIDTH, bottomLineY)
          bgPath.lineTo(ITEM_WIDTH, (currentY + nextY) / 2f)

          canvas.drawPath(bgPath, pathPaint)

          // 起始 不需要绘制开头
          canvas.drawLine(
            ITEM_WIDTH / 2f,
            bottomLineY,
            ITEM_WIDTH,
            bottomLineY,
            axisPaint
          )



          canvas.drawLine(
            ITEM_WIDTH / 2f,
            currentY,
            ITEM_WIDTH,
            (currentY + nextY) / 2f,
            linePaint
          )

          canvas.drawLine(ITEM_WIDTH / 2f, currentY, ITEM_WIDTH / 2f, bottomLineY, axisPaint)
          dashXs.add(ITEM_WIDTH / 2f)

        }
        weathers.size - 1 -> {

          val lastY = getCentY(weathers[index - 1])

          bgPath.reset()
          bgPath.moveTo(
            ITEM_WIDTH * (index),
            (currentY + lastY) / 2f
          )
          bgPath.lineTo(
            ITEM_WIDTH * (index + 1) - ITEM_WIDTH / 2f,
            currentY
          )
          bgPath.lineTo(
            ITEM_WIDTH * (index + 1) - ITEM_WIDTH / 2f,
            bottomLineY
          )
          bgPath.lineTo(
            ITEM_WIDTH * (index),
            bottomLineY
          )

          canvas.drawPath(bgPath, pathPaint)


          canvas.drawLine(
            ITEM_WIDTH * (index),
            (currentY + lastY) / 2f,
            ITEM_WIDTH * (index + 1) - ITEM_WIDTH / 2f,
            currentY,
            linePaint
          )

          // 末尾
          canvas.drawLine(
            ITEM_WIDTH * (index),
            bottomLineY,
            ITEM_WIDTH * (index + 1) - ITEM_WIDTH / 2f,
            bottomLineY,
            axisPaint
          )

          if (weathers[index - 1].getWeatherType() != hourlyWeatherBean.getWeatherType()) {
            //  不同天气的分割线
            canvas.drawLine(ITEM_WIDTH * (index), (lastY + currentY) / 2f, ITEM_WIDTH * (index), bottomLineY, axisPaint)
            dashXs.add(ITEM_WIDTH * (index))
          }

          canvas.drawLine(
            ITEM_WIDTH * (index + 1) - ITEM_WIDTH / 2f,
            currentY,
            ITEM_WIDTH * (index + 1) - ITEM_WIDTH / 2f,
            bottomLineY,
            axisPaint
          )

          dashXs.add(ITEM_WIDTH * (index + 1) - ITEM_WIDTH / 2f)

        }
        else -> {
          // 其他
          canvas.drawLine(
            ITEM_WIDTH * (index),
            bottomLineY,
            ITEM_WIDTH * (index + 1),
            bottomLineY,
            axisPaint
          )

          val nextY = getCentY(weathers[index + 1])

          val lastY = getCentY(weathers[index - 1])


          bgPath.reset()
          bgPath.moveTo(
            ITEM_WIDTH * (index),
            (currentY + lastY) / 2f
          )
          bgPath.lineTo(
            ITEM_WIDTH * (index + 1),
            (currentY + nextY) / 2f,
          )
          bgPath.lineTo(
            ITEM_WIDTH * (index + 1),
            bottomLineY
          )
          bgPath.lineTo(
            ITEM_WIDTH * (index),
            bottomLineY
          )

          canvas.drawPath(bgPath, pathPaint)



          canvas.drawLine(
            ITEM_WIDTH * (index),
            (currentY + lastY) / 2f,
            ITEM_WIDTH * (index + 1),
            (currentY + nextY) / 2f,
            linePaint
          )


          if (weathers[index - 1].getWeatherType() != hourlyWeatherBean.getWeatherType()) {
            //  不同天气的分割线
            canvas.drawLine(ITEM_WIDTH * (index), (lastY + currentY) / 2f, ITEM_WIDTH * (index), bottomLineY, axisPaint)
            dashXs.add(ITEM_WIDTH * (index))
          }


        }
      }
    }
    canvas.restore()
  }

  // 获取这个item的中心Y坐标
  private fun getCentY(hourlyWeatherBean: HourlyWeatherBean): Float {
    return (bottomY -
        ((hourlyWeatherBean.temp * 1f - minTemp) / (maxTemp - minTemp)) *
        (bottomY - topY)) * 1f
  }


  // 添加数据
  fun setData(data: List<HourlyWeatherBean>) {
    weathers.clear()
    weathers.addAll(data)
    calculatePontGap()
    postInvalidate()
  }


  private val minViewHeight = 80.dp.toInt()


  private var lastX = 0f
  private var currentX = 0f

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(ev: MotionEvent): Boolean {

    // 添加速度追踪
    velocityTracker.addMovement(ev)

    when (ev.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        if (!scroller.isFinished) {  //fling还没结束
          scroller.abortAnimation()
        }
        // 获取触摸下的哪个点
        lastX = ev.x
        setParentRequestDisallowInterceptTouchEvent(true, parent)
        return true
      }
      MotionEvent.ACTION_MOVE -> {
        // 当前的x坐标
        currentX = ev.x
        // 获取水平滑动位移的距离
        val deltaX = (lastX - currentX).toInt()
        // 划过了左界限 则忽略 限定在左界限
        if (scrollX + deltaX < 0) {    //越界恢复
          scrollTo(0, 0)
          return true
        } else if (scrollX + deltaX > weathers.size * ITEM_WIDTH) {
          // 同上 右界限边缘限制
          scrollTo((weathers.size * ITEM_WIDTH).toInt(), 0)
          return true
        }
        // 其他情况跟踪手动滑动距离进行滑动
        scrollBy(deltaX, 0)
        lastX = ev.x
        postInvalidate()
        return true
      }
      MotionEvent.ACTION_UP -> {
        currentX = ev.x
        velocityTracker.computeCurrentVelocity(1000)
        val xVelocity = velocityTracker.xVelocity.toInt()
        // 滑动速度可被判定为抛动
        if (Math.abs(xVelocity) > viewConfiguration.scaledMinimumFlingVelocity) {
          // 进行惯性滑动
          scroller.fling(scrollX, 0, -xVelocity, 0, 0, (weathers.size * ITEM_WIDTH).toInt(), 0, 0)
          postInvalidateOnAnimation()
          setParentRequestDisallowInterceptTouchEvent(false, parent)
        }
        return true
      }


    }
    return super.onTouchEvent(ev)
  }

  override fun computeScroll() {
    super.computeScroll()
    if (scroller.computeScrollOffset()) {
      scrollTo(scroller.currX, scroller.currY)
      postInvalidate()
    }
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    calculatePontGap()
  }

  /**
   * 计算高度差
   */
  private fun calculatePontGap() {
    val formatWeathers = weathers.toList().sortedBy { it.temp }
    maxTemp = formatWeathers.last().temp
    minTemp = formatWeathers.first().temp
    weathers.forEach {
      icons[it.weatherName] = getFixBitmap(it)
    }
  }

  // 获取希望大小的bitmap
  private fun getFixBitmap(hourlyWeatherBean: HourlyWeatherBean): Bitmap {
    val ob = getBitmapFromDrawable(hourlyWeatherBean.getWeatherType().getIconRes())
    val matrix = Matrix()
    matrix.setScale(48.dp / ob.width, 48.dp / ob.width)
    return Bitmap.createBitmap(
      ob, 0, 0, ob.width,
      ob.height, matrix, true
    )
  }

  @Throws(IllegalArgumentException::class)
  fun getBitmapFromDrawable(@DrawableRes drawableId: Int): Bitmap {
    return when (val drawable = ContextCompat.getDrawable(context, drawableId)) {
      is BitmapDrawable -> {
        drawable.bitmap
      }
      is VectorDrawable, is VectorDrawableCompat -> {
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bitmap
      }
      else -> {
        throw IllegalArgumentException("unsupported drawable type")
      }
    }
  }

  private fun setParentRequestDisallowInterceptTouchEvent(b: Boolean, parent: ViewParent?) {
    if (parent != null) {
      if (parent is RecyclerView) {
        parent.requestDisallowInterceptTouchEvent(b)
        parent.isNestedScrollingEnabled = !b
        return
      }
      setParentRequestDisallowInterceptTouchEvent(b, parent.parent)
    }
  }

  private fun getColorWithAlpha(alpha: Float = .5f, baseColor: Int): Int {
    val a = Math.min(255, Math.max(0, (alpha * 255).toInt())) shl 24
    val rgb = 0x00ffffff and baseColor
    return a + rgb
  }

}