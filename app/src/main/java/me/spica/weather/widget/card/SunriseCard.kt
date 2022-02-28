package me.spica.weather.widget.card

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import me.spica.weather.R
import me.spica.weather.databinding.CardSunriseBinding
import java.text.SimpleDateFormat
import java.util.*

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


    private val sdf = SimpleDateFormat("HH:mm", Locale.CHINA)
    fun bindTime(startTime: Date, endTime: Date, subTitle: String) {
        binding.sunriseView.bindTime(startTime, endTime)
        binding.tvTitle.text = String.format(
            context.getString(R.string.sunrise_sunset_time),
            sdf.format(startTime),
            sdf.format(endTime)
        )
        binding.tvSubtitle.text = subTitle
    }


    override fun startEnterAnim() {
        if (enterAnim.size != 0) {
            binding.sunriseView.startAnim()
        }
        super.startEnterAnim()
    }
}