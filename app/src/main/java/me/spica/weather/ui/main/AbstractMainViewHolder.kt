package me.spica.weather.ui.main

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import me.spica.weather.model.weather.Weather
import me.spica.weather.widget.card.SpicaWeatherCard

open class AbstractMainViewHolder(val card: SpicaWeatherCard, itemView: View) :
    RecyclerView.ViewHolder(itemView) {


    fun bindView(weather: Weather) {
        card.bindData(weather)
    }

    fun reset(){
        card.resetAnim()
    }

    fun checkEnterScreen(host: RecyclerView) {
        if (!itemView.isLaidOut || itemView.top >= host.measuredHeight) {
            return
        }

        card.checkEnterScreen(true)
    }

}