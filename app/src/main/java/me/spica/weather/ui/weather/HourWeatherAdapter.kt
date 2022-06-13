package me.spica.weather.ui.weather

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import me.spica.weather.common.WeatherCodeUtils
import me.spica.weather.common.getAnimRes
import me.spica.weather.common.getIconRes
import me.spica.weather.databinding.ItemHourTempBinding
import me.spica.weather.model.weather.HourlyWeatherBean
import java.text.SimpleDateFormat
import java.util.*

class HourWeatherAdapter : RecyclerView.Adapter<HourWeatherAdapter.ViewHolder>() {

    val items = mutableListOf<HourlyWeatherBean>()

    // 被格式化的时间格式
    private val sdfAfter = SimpleDateFormat("m时", Locale.CHINA)

    // 用于排序的列表
    private val sortItems = mutableListOf<HourlyWeatherBean>()

    class ViewHolder(val itemHourTempBinding: ItemHourTempBinding) :
        RecyclerView.ViewHolder(itemHourTempBinding.root)

    private val divisionDate by lazy {
        val calendar = Calendar.getInstance(Locale.CHINA)
        calendar.set(Calendar.HOUR_OF_DAY, 24)
        calendar.set(Calendar.MINUTE, 0)
        return@lazy calendar
    }

    // 最高温度
    private var maxTemp = 0

    // 最低温度
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
            it.temp
        }
        maxTemp = sortItems.last().temp
        minTemp = sortItems.first().temp
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        items[position].let {

            holder.itemHourTempBinding.tvTime.text = sdfAfter.format(it.fxTime)
            if (divisionDate.time.before(it.fxTime)) {
                holder.itemHourTempBinding.tvTime.text = "次日" + sdfAfter.format(it.fxTime)
            }

            holder.itemHourTempBinding.tvPercent.text = "${it.pop}%"

            holder.itemHourTempBinding.icWeather.setAnimation(
                WeatherCodeUtils.getWeatherCode(it.iconId.toString()).getAnimRes()
            )

            holder.itemHourTempBinding.itemLine.maxValue = maxTemp
            holder.itemHourTempBinding.itemLine.minValue = minTemp

            if (position == 0) {
                holder.itemHourTempBinding.itemLine.drawLeftLine = false
            } else {

                holder.itemHourTempBinding.itemLine.drawLeftLine = true
                holder.itemHourTempBinding.itemLine.lastValue =
                    items[position - 1].temp
            }

            holder.itemHourTempBinding.itemLine.currentValue =
                items[position].temp

            holder.itemHourTempBinding.itemLine.currentPop = items[position].pop

//            holder.itemHourTempBinding.itemLine.currentPop = 500
            if (position == items.size - 1) {
                holder.itemHourTempBinding.itemLine.drawRightLine = false
            } else {
                holder.itemHourTempBinding.itemLine.drawRightLine = true
                holder.itemHourTempBinding.itemLine.nextValue =
                    items[position + 1].temp
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
