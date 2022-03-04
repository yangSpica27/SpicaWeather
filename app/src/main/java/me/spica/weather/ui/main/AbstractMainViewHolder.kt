package me.spica.weather.ui.main

import android.graphics.Rect
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

    private val rect = Rect()
    fun checkEnterScreen(host: RecyclerView) {
//        if (!ViewCompat.isLaidOut(itemView) && itemView.top >= host.measuredHeight) {
//            return
//        }
        val isVisible = itemView.getGlobalVisibleRect(rect)
        card.checkEnterScreen(isVisible&&rect.bottom-rect.top>=itemView.height/10f)
    }

}