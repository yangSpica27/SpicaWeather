package me.spica.weather.widget.card

import android.animation.Animator
import androidx.core.animation.doOnEnd

interface SpicaWeatherCard {

    var enterAnim: MutableList<Animator>

    fun startEnterAnim() {
        enterAnim.forEach {
            if (!it.isRunning) {
                it.start()
                it.doOnEnd { anim ->
                    enterAnim.remove(anim)
                }
            }
        }
    }

    fun resetAnim() = Unit
}
