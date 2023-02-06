package me.spica.weather.ui.weather

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.spica.weather.R
import me.spica.weather.common.getAnimRes
import me.spica.weather.common.getThemeColor
import me.spica.weather.databinding.ItemDayInfoBinding
import me.spica.weather.model.weather.DailyWeatherBean
import java.text.SimpleDateFormat
import java.util.*

class DayInfoAdapter : RecyclerView.Adapter<DayInfoAdapter.ViewHolder>() {

  val items = mutableListOf<DailyWeatherBean>()


  // 格式化为"周几"
  private val sdfWeek = SimpleDateFormat("E", Locale.CHINA)

  // 用于排序的列表
  private val sortList = mutableListOf<DailyWeatherBean>()

  //点击监听
  var itemClickListener: (DailyWeatherBean) -> Unit = {}

  // 最近几日最低气温中最低的
  private var minTempTop = 0

  // 最近几日最低气温中最高的
  private var maxTempTop = 0

  // 最近几日最高气温中最低的
  private var minTempBottom = 0

  // 最近几日最高气温中最高的
  private var maxTempBottom = 0

  fun syncTempMaxAndMin() {
    sortList.clear()
    sortList.addAll(items)
    sortList.sortBy {
      it.maxTemp
    }
    maxTempTop = sortList.last().maxTemp
    minTempTop = sortList.first().maxTemp
    sortList.sortBy {
      it.minTemp
    }
    maxTempBottom = sortList.last().minTemp
    minTempBottom = sortList.first().minTemp
  }


  class ViewHolder(val itemDayInfoBinding: ItemDayInfoBinding) : RecyclerView.ViewHolder(itemDayInfoBinding.root)


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val itemDayInfoBinding = ItemDayInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ViewHolder(itemDayInfoBinding)
  }

  override fun getItemCount(): Int = items.size

  @SuppressLint("SetTextI18n")
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.itemDayInfoBinding.tvDay.text = sdfWeek.format(items[position].fxTime())
    if (position == 0) {
      holder.itemDayInfoBinding.tvDay.text = "今天"
      holder.itemDayInfoBinding.tvDay.setTextColor(items[position].getWeatherType().getThemeColor())
    } else {
      holder.itemDayInfoBinding.tvDay.setTextColor(holder.itemView.context.getColor(R.color.textColorPrimary))
    }
    holder.itemDayInfoBinding.maxTemp.text = "${items[position].maxTemp}℃"
    holder.itemDayInfoBinding.minTemp.text = "${items[position].minTemp}℃"
    holder.itemDayInfoBinding.icon.setAnimation(
      items[position].getWeatherType().getAnimRes()
    )
    holder.itemDayInfoBinding.tempView.apply {
      maxValue = maxTempTop
      minValue = minTempBottom
      currentMaxValue = items[position].maxTemp
      currentMinValue = items[position].minTemp
    }
    holder.itemDayInfoBinding.tempView.init()
    holder.itemDayInfoBinding.tempView.postInvalidate()
    holder.itemDayInfoBinding.icon.progress = .5f
    holder.itemDayInfoBinding.icon.setMaxProgress(.5f)
    holder.itemDayInfoBinding.icon.setOnClickListener {
      holder.itemDayInfoBinding.icon.playAnimation()
    }
  }
}