package me.spica.weather.widget.card

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import me.spica.weather.databinding.CardSunriseBinding

class SunriseCard : SpicaWeatherCard, ConstraintLayout {

    override var enterAnim: MutableList<Animator> = mutableListOf(
        ObjectAnimator.ofFloat(this, "alpha", 0F, 1F).apply {
            duration = 400
            startDelay = 100
            interpolator = FastOutSlowInInterpolator()
        },
        ObjectAnimator.ofFloat(this, "translationY", 100F, 0F).apply {
            duration = 400
            startDelay = 100
            interpolator = FastOutSlowInInterpolator()
        }
    )

    private val binding = CardSunriseBinding.inflate(LayoutInflater.from(context), this, true)


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    init {
        alpha = 0f
    }

}