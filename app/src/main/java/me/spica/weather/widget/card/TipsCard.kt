package me.spica.weather.widget.card

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.fondesa.recyclerviewdivider.dividerBuilder
import me.spica.weather.R
import me.spica.weather.databinding.CardLifeIndexBinding
import me.spica.weather.model.weather.LifeIndexBean
import me.spica.weather.ui.home.TipAdapter

class TipsCard : RelativeLayout, SpicaWeatherCard {

    private val tipAdapter = TipAdapter()

    private val binding = CardLifeIndexBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        alpha = 0f
        context
            .dividerBuilder()
            .colorRes(R.color.line_divider)
            .build()
            .addTo(binding.rvTip)
        binding.rvTip.adapter = tipAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    fun bindData(items: List<LifeIndexBean>) {
        tipAdapter.items.clear()
        tipAdapter.items.addAll(items)
        tipAdapter.notifyDataSetChanged()
    }

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
}
