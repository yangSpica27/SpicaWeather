package me.spica.weather.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qweather.sdk.bean.weather.WeatherDailyBean
import me.spica.weather.databinding.ItemDayWeatherBinding
import java.text.SimpleDateFormat
import java.util.*

class DailWeatherAdapter : RecyclerView.Adapter<DailWeatherAdapter.ViewHolder>() {

    val items = mutableListOf<WeatherDailyBean.DailyBean?>()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

    val sortList = mutableListOf<WeatherDailyBean.DailyBean?>()

    private var minTemp = 0

    private var maxTemp = 0

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
        maxTemp = sortList.last()?.tempMax?.toInt() ?: 0
        sortList.sortBy {
            it?.tempMin?.toInt()
        }
        minTemp = sortList.first()?.tempMin?.toInt() ?: 0

    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items[position]?.let {
            val date = sdf.parse(it.fxDate) ?: Date()
            val calendar = Calendar.getInstance()
            calendar.time = date
            holder.itemDayWeatherBinding.tvDate.text = "${date.month}/${date.day}"
            holder.itemDayWeatherBinding.tvDate.text = "星期${calendar.get(Calendar.DAY_OF_WEEK) - 1}"

            holder.itemDayWeatherBinding.itemLine.maxValue = maxTemp

            holder.itemDayWeatherBinding.itemLine.minValue = minTemp

            if (position == 0) {
                holder.itemDayWeatherBinding.itemLine.drawLeftLine = false

            } else {
                holder.itemDayWeatherBinding.itemLine.drawLeftLine = true
                holder.itemDayWeatherBinding.itemLine.lastValue =
                    items[position - 1]?.tempMax?.toInt() ?: 0
            }
            holder.itemDayWeatherBinding.itemLine.currentValue =
                items[position]?.tempMax?.toInt() ?: 0


            if (position == items.size - 1) {
                holder.itemDayWeatherBinding.itemLine.drawRightLine = false
            } else {
                holder.itemDayWeatherBinding.itemLine.drawRightLine = true
                holder.itemDayWeatherBinding.itemLine.nextValue =
                    items[position + 1]?.tempMax?.toInt() ?: 0
            }

        }

    }

    override fun getItemCount(): Int = items.size


}