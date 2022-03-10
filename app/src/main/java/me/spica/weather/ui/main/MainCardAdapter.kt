package me.spica.weather.ui.main

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.spica.weather.model.weather.Weather
import me.spica.weather.view.card.AirCard
import me.spica.weather.view.card.DailyWeatherCard
import me.spica.weather.view.card.HomeCardType
import me.spica.weather.view.card.HourWeatherCard
import me.spica.weather.view.card.NowWeatherCard
import me.spica.weather.view.card.SunriseCard
import me.spica.weather.view.card.TipsCard
import timber.log.Timber

class MainCardAdapter(
    private val recyclerView: RecyclerView,
    private val scope: CoroutineScope
) : RecyclerView.Adapter<AbstractMainViewHolder>() {

    private var items = HomeCardType.values().toList()

    private var weather: Weather? = null


    init {
        recyclerView.setItemViewCacheSize(10)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            AbstractMainViewHolder {
        val lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        when (viewType) {
            HomeCardType.DAY_WEATHER.code -> {
                val itemView = DailyWeatherCard(parent.context)
                itemView.layoutParams = lp
                return AbstractMainViewHolder(itemView, itemView)
            }
            HomeCardType.HOUR_WEATHER.code -> {
                val itemView = HourWeatherCard(parent.context)
                itemView.layoutParams = lp
                return AbstractMainViewHolder(itemView, itemView)
            }
            HomeCardType.LIFE_INDEX.code -> {
                val itemView = TipsCard(parent.context)
                itemView.layoutParams = lp
                return AbstractMainViewHolder(itemView, itemView)
            }
            HomeCardType.NOW_WEATHER.code -> {
                val itemView = NowWeatherCard(parent.context)
                itemView.layoutParams = lp
                return AbstractMainViewHolder(itemView, itemView)
            }
            HomeCardType.SUNRISE.code -> {
                val itemView = SunriseCard(parent.context)
                itemView.layoutParams = lp
                return AbstractMainViewHolder(itemView, itemView)
            }
            HomeCardType.AIR.code -> {
                val itemView = AirCard(parent.context)
                itemView.layoutParams = lp
                return AbstractMainViewHolder(itemView, itemView)
            }
            else -> {
                val itemView = DailyWeatherCard(parent.context)
                itemView.layoutParams = lp
                return AbstractMainViewHolder(itemView, itemView)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return items[position].code
    }

    override fun onBindViewHolder(holder: AbstractMainViewHolder, position: Int) {
        holder.reset()
        weather?.let {
            scope.launch(Dispatchers.Default) {
                holder.bindView(it)
                holder.itemView.requestLayout()
            }
        }
        Timber.e("绑定视图${position}")
        recyclerView.post {
            holder.checkEnterScreen(recyclerView)
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun notifyData(weather: Weather) {
        this.weather = weather
        notifyDataSetChanged()
        recyclerView.scrollToPosition(0)
    }


    fun onScroll() {
        var holder: AbstractMainViewHolder
        for (i in 0 until itemCount) {
            if (recyclerView.findViewHolderForAdapterPosition(i) != null) {
                holder = recyclerView.findViewHolderForAdapterPosition(i) as AbstractMainViewHolder
                holder.checkEnterScreen(recyclerView)
            }
        }
    }

    override fun getItemCount(): Int = items.size


}