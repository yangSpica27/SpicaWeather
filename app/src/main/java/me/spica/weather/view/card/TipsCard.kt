package me.spica.weather.view.card

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.fondesa.recyclerviewdivider.dividerBuilder
import kotlinx.coroutines.Job
import me.spica.weather.R
import me.spica.weather.databinding.CardLifeIndexBinding
import me.spica.weather.model.weather.Weather
import me.spica.weather.tools.doOnMainThreadIdle
import me.spica.weather.ui.home.TipAdapter

class TipsCard : RelativeLayout, SpicaWeatherCard{

    private val tipAdapter = TipAdapter()


    private val job = Job()



    private val binding = CardLifeIndexBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()
    override var index: Int = 4
    override var hasInScreen: Boolean = false

    init {
        resetAnim()
        context
            .dividerBuilder()
            .colorRes(R.color.line_divider)
            .build()
            .addTo(binding.rvTip)
        binding.rvTip.adapter = tipAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun bindData(weather: Weather) {
            val items = weather.lifeIndexes
            tipAdapter.items.clear()
            tipAdapter.items.addAll(items)
            doOnMainThreadIdle({
                tipAdapter.notifyDataSetChanged()
            })
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job.cancel()
    }
}
