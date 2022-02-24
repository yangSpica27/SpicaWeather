package me.spica.weather.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import me.spica.weather.common.WeatherCodeUtils
import me.spica.weather.common.getIconRes
import me.spica.weather.databinding.ItemDayWeatherBinding
import me.spica.weather.model.weather.DailyWeatherBean
import java.text.SimpleDateFormat
import java.util.*

class DailWeatherAdapter : RecyclerView.Adapter<DailWeatherAdapter.ViewHolder>() {

    val items = mutableListOf<DailyWeatherBean>()

    // 格式化
    private val sdf2 = SimpleDateFormat("M月dd日", Locale.CHINA)

    // 格式化为"周几"
    private val sdfWeek = SimpleDateFormat("E", Locale.CHINA)

    // 用于排序的列表
    private val sortList = mutableListOf<DailyWeatherBean>()

    // 最近几日最低气温中最低的
    private var minTempTop = 0

    // 最近几日最低气温中最高的
    private var maxTempTop = 0

    // 最近几日最高气温中最低的
    private var minTempBottom = 0

    // 最近几日最高气温中最高的
    private var maxTempBottom = 0

    class ViewHolder(val itemDayWeatherBinding: ItemDayWeatherBinding) :
        RecyclerView.ViewHolder(itemDayWeatherBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemDayWeatherBinding: ItemDayWeatherBinding = ItemDayWeatherBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemDayWeatherBinding)
    }

    // 获取最高低期望用于计算折线图对应坐标
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

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items[position].let {

            holder.itemDayWeatherBinding.tvDate.text = sdf2.format(it.fxTime)
            holder.itemDayWeatherBinding.tvWeek.text = sdfWeek.format(it.fxTime)

            holder.itemDayWeatherBinding.itemLineMax.maxValue = maxTempTop
            holder.itemDayWeatherBinding.itemLineMax.minValue = minTempTop
            holder.itemDayWeatherBinding.itemLineMin.maxValue = maxTempBottom
            holder.itemDayWeatherBinding.itemLineMin.minValue = minTempBottom

            if (position == 0) {
                // 首个item不绘制左半部分
                holder.itemDayWeatherBinding.itemLineMax.drawLeftLine = false
                holder.itemDayWeatherBinding.itemLineMin.drawLeftLine = false
            } else {
                holder.itemDayWeatherBinding.itemLineMax.drawLeftLine = true
                holder.itemDayWeatherBinding.itemLineMin.drawLeftLine = true
                holder.itemDayWeatherBinding.itemLineMax.lastValue =
                    items[position - 1].maxTemp
                holder.itemDayWeatherBinding.itemLineMin.lastValue =
                    items[position - 1].minTemp
            }

            holder.itemDayWeatherBinding.itemLineMax.currentValue =
                items[position].maxTemp

            holder.itemDayWeatherBinding.itemLineMin.currentValue =
                items[position].minTemp

            holder.itemDayWeatherBinding.icon.rainfallProbability = items[position].precip
//            holder.itemDayWeatherBinding.icon.rainfallProbability = 60
            holder.itemDayWeatherBinding.icon.load(
                WeatherCodeUtils.getWeatherCode(items[position].iconId.toString()).getIconRes()
            )

            if (position == items.size - 1) {
                // 末尾item不绘制左半部分
                holder.itemDayWeatherBinding.itemLineMax.drawRightLine = false
                holder.itemDayWeatherBinding.itemLineMin.drawRightLine = false
            } else {
                holder.itemDayWeatherBinding.itemLineMax.drawRightLine = true
                holder.itemDayWeatherBinding.itemLineMin.drawRightLine = true
                holder.itemDayWeatherBinding.itemLineMax.nextValue =
                    items[position + 1].maxTemp
                holder.itemDayWeatherBinding.itemLineMin.nextValue =
                    items[position + 1].minTemp
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
