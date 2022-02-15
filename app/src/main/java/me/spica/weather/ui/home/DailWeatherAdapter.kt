package me.spica.weather.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qweather.sdk.bean.weather.WeatherDailyBean
import me.spica.weather.databinding.ItemDayWeatherBinding
import java.text.SimpleDateFormat
import java.util.*

private val dayName = arrayListOf(
    "周日",
    "周一",
    "周二",
    "周三",
    "周四",
    "周五",
    "周六",
)
class DailWeatherAdapter : RecyclerView.Adapter<DailWeatherAdapter.ViewHolder>() {

    val items = mutableListOf<WeatherDailyBean.DailyBean?>()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

    private val sdf2 = SimpleDateFormat("M月dd日", Locale.CHINA)

    private val sdfWeek = SimpleDateFormat("E", Locale.CHINA)

    private val sortList = mutableListOf<WeatherDailyBean.DailyBean?>()

    private var minTempTop = 0

    private var maxTempTop = 0

    private var minTempBottom = 0

    private var maxTempBottom = 0

    class ViewHolder(val itemDayWeatherBinding: ItemDayWeatherBinding) :
        RecyclerView.ViewHolder(itemDayWeatherBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemDayWeatherBinding: ItemDayWeatherBinding = ItemDayWeatherBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemDayWeatherBinding)
    }


    fun syncTempMaxAndMin() {
        sortList.clear()
        sortList.addAll(items)
        sortList.sortBy {
            it?.tempMax?.toInt()
        }
        maxTempTop = sortList.last()?.tempMax?.toInt() ?: 0
        minTempTop = sortList.first()?.tempMax?.toInt() ?: 0
        sortList.sortBy {
            it?.tempMin?.toInt()
        }
        maxTempBottom = sortList.last()?.tempMin?.toInt() ?: 0
        minTempBottom = sortList.first()?.tempMin?.toInt() ?: 0

    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items[position]?.let {

            val date = sdf.parse(it.fxDate) ?: Date()

            holder.itemDayWeatherBinding.tvDate.text = sdf2.format(date)
            holder.itemDayWeatherBinding.tvWeek.text = sdfWeek.format(date)

            holder.itemDayWeatherBinding.itemLineMax.maxValue = maxTempTop
            holder.itemDayWeatherBinding.itemLineMax.minValue = minTempTop
            holder.itemDayWeatherBinding.itemLineMin.maxValue = maxTempBottom
            holder.itemDayWeatherBinding.itemLineMin.minValue = minTempBottom

            if (position == 0) {
                holder.itemDayWeatherBinding.itemLineMax.drawLeftLine = false
                holder.itemDayWeatherBinding.itemLineMin.drawLeftLine = false
            } else {
                holder.itemDayWeatherBinding.itemLineMax.drawLeftLine = true
                holder.itemDayWeatherBinding.itemLineMin.drawLeftLine = true
                holder.itemDayWeatherBinding.itemLineMax.lastValue =
                    items[position - 1]?.tempMax?.toInt() ?: 0
                holder.itemDayWeatherBinding.itemLineMin.lastValue =
                    items[position - 1]?.tempMin?.toInt() ?: 0
            }

            holder.itemDayWeatherBinding.itemLineMax.currentValue =
                items[position]?.tempMax?.toInt() ?: 0

            holder.itemDayWeatherBinding.itemLineMin.currentValue =
                items[position]?.tempMin?.toInt() ?: 0

            if (position == items.size - 1) {
                holder.itemDayWeatherBinding.itemLineMax.drawRightLine = false
                holder.itemDayWeatherBinding.itemLineMin.drawRightLine = false
            } else {
                holder.itemDayWeatherBinding.itemLineMax.drawRightLine = true
                holder.itemDayWeatherBinding.itemLineMin.drawRightLine = true
                holder.itemDayWeatherBinding.itemLineMax.nextValue =
                    items[position + 1]?.tempMax?.toInt() ?: 0
                holder.itemDayWeatherBinding.itemLineMin.nextValue =
                    items[position + 1]?.tempMin?.toInt() ?: 0
            }

        }

    }

    override fun getItemCount(): Int = items.size


}