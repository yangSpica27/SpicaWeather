package me.spica.weather.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qweather.sdk.bean.weather.WeatherHourlyBean
import me.spica.weather.databinding.ItemHourTempBinding
import java.text.SimpleDateFormat
import java.util.*

class HourWeatherAdapter : RecyclerView.Adapter<HourWeatherAdapter.ViewHolder>() {

    val items = mutableListOf<WeatherHourlyBean.HourlyBean?>()

    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'mm:HH+08:00", Locale.CHINA)

    private val sdfAfter = SimpleDateFormat("m时", Locale.CHINA)

    private val sortItems = mutableListOf<WeatherHourlyBean.HourlyBean?>()

    class ViewHolder(val itemHourTempBinding: ItemHourTempBinding) :
        RecyclerView.ViewHolder(itemHourTempBinding.root)


    private val divisionDate by lazy {
        val calendar = Calendar.getInstance(Locale.CHINA)
        calendar.set(Calendar.HOUR_OF_DAY, 24)
        calendar.set(Calendar.MINUTE, 0)
        return@lazy calendar
    }

    private var maxTemp = 0

    private var minTemp = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemHourTempBinding = ItemHourTempBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemHourTempBinding)
    }

    fun sortList() {
        sortItems.clear()
        sortItems.addAll(items)
        sortItems.sortBy {
            it?.temp.toString()
        }

        maxTemp = sortItems.last()?.temp?.toInt() ?: 0
        minTemp = sortItems.first()?.temp?.toInt() ?: 0

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        items[position]?.let {

            val date = sdf.parse(it.fxTime) ?: Date()


            holder.itemHourTempBinding.tvTime.text = sdfAfter.format(date)
            if (divisionDate.time.before(date)) {
                holder.itemHourTempBinding.tvTime.text = "次日" + sdfAfter.format(date)
            }

            holder.itemHourTempBinding.tvPercent.text = "降雨概率\n${it.pop ?: "0"}%"

            holder.itemHourTempBinding.tvWeather.text = it.text

            holder.itemHourTempBinding.itemLine.maxValue = maxTemp
            holder.itemHourTempBinding.itemLine.minValue = minTemp

            if (position == 0) {
                holder.itemHourTempBinding.itemLine.drawLeftLine = false

            } else {

                holder.itemHourTempBinding.itemLine.drawLeftLine = true
                holder.itemHourTempBinding.itemLine.lastValue =
                    items[position - 1]?.temp?.toInt() ?: 0
            }

            holder.itemHourTempBinding.itemLine.currentValue =
                items[position]?.temp?.toInt() ?: 0


            if (position == items.size - 1) {
                holder.itemHourTempBinding.itemLine.drawRightLine = false
            } else {
                holder.itemHourTempBinding.itemLine.drawRightLine = true
                holder.itemHourTempBinding.itemLine.nextValue =
                    items[position + 1]?.temp?.toInt() ?: 0
            }

        }
    }

    override fun getItemCount(): Int = items.size
}