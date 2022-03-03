package me.spica.weather.ui.main

import android.animation.Animator
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import me.spica.weather.model.weather.Weather
import me.spica.weather.widget.card.SpicaWeatherCard

//abstract class AbstractMainViewHolder(val card: SpicaWeatherCard, itemView: View) :
//    RecyclerView.ViewHolder(itemView) {
//
//    private var context: Context? = null
//
//    private var inScreen = false // 是否在屏幕内
//
//    private var itemAnimator: List<Animator> = arrayListOf()
//
//    fun bindView(weather: Weather) {
//
//    }
//
//
//    fun checkEnterScreen(
//        host: RecyclerView,
//        pendingAnimatorList: List<Animator?>?,
//        listAnimationEnabled: Boolean
//    ) {
//        if (!itemView.isLaidOut || itemView.top >= host.measuredHeight) {
//            return
//        }
//        if (!inScreen) {
//            inScreen = true
//            if (listAnimationEnabled) {
//                executeEnterAnimator(pendingAnimatorList)
//            }
//        }
//    }
//
//    private fun executeEnterAnimator(pendingAnimatorList: List<Animator>) {
//        itemView.alpha = 0f
//        itemAnimator = card.enterAnim
//
//    }
//
//
//}