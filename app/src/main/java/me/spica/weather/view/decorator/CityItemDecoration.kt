package me.spica.weather.view.decorator

import android.graphics.*
import android.text.TextPaint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import me.spica.weather.tools.dp
import me.spica.weather.ui.city.CityAdapter
import timber.log.Timber

// 用于城市选择的分割器
class CityItemDecoration(private val cityAdapter: CityAdapter) : RecyclerView.ItemDecoration() {

  private val textLeftPadding = 14.dp + 12.dp

  private val textTopAndBottomPadding = 8.dp


  //  绘制字母表
  private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    textSize = 28.dp
    color = Color.BLACK
    typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
  }


  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
    super.getItemOffsets(outRect, view, parent, state)
    val index = parent.getChildAdapterPosition(view)
    val rectText = Rect()

    // 绘制底部间隔
    if (index == cityAdapter.diffUtil.currentList.size - 1) {
      outRect.bottom = rectText.height() + 2 * textTopAndBottomPadding.toInt()
    }
    // 首个必须绘制
    if (index <= 0) {
      if (index == 0) {
        textPaint.getTextBounds(cityAdapter.diffUtil.currentList[index].sortId.uppercase(), 0, 1, rectText)
      } else {
        // -1个为null
        rectText.top = 0
        rectText.bottom = 0
      }
      outRect.top = rectText.height() + 12.dp.toInt()
    } else {
      val lastId = cityAdapter.diffUtil.currentList[index - 1].sortId
      val currentId = cityAdapter.diffUtil.currentList[index].sortId
      if (lastId == currentId) {
        // 空出间隔
        Timber.i("空出四个单位")
        textPaint.getTextBounds(cityAdapter.diffUtil.currentList[index].sortId.uppercase(), 0, 1, rectText)
        outRect.top = 12.dp.toInt()
      } else {
        // 空出绘制空间
        Timber.i("空出字体空间${cityAdapter.diffUtil.currentList[index - 1].cityName}/${cityAdapter.diffUtil.currentList[index].cityName}")
        outRect.top = rectText.height() + 2 * textTopAndBottomPadding.toInt()
      }

    }
  }

  override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    super.onDrawOver(c, parent, state)
    for (position in 0 until parent.childCount) {
      val child = parent.getChildAt(position)
      val index = parent.getChildAdapterPosition(child)
      if (index==-1)return
      if (index == 0) {
        c.drawText(
          cityAdapter.diffUtil.currentList[index].sortId.uppercase(),
          textLeftPadding,
          -textTopAndBottomPadding + child.top,
          textPaint
        )
      } else {
        val lastId = cityAdapter.diffUtil.currentList[index - 1].sortId
        val currentId = cityAdapter.diffUtil.currentList[index].sortId
        if (lastId == currentId) {
          // 空出间隔
        } else {
          // 空出绘制空间
          c.drawText(
            cityAdapter.diffUtil.currentList[index].sortId.uppercase(),
            textLeftPadding,
            -textTopAndBottomPadding + child.top,
            textPaint
          )
        }
      }
    }
  }

}