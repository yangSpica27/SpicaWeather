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

    class ViewHolder(val itemDayWeatherBinding: ItemDayWeatherBinding) :
        RecyclerView.ViewHolder(itemDayWeatherBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemDayWeatherBinding: ItemDayWeatherBinding = ItemDayWeatherBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemDayWeatherBinding)
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

        }

    }

    override fun getItemCount(): Int = items.size


}