package me.spica.weather.view.card

import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import me.spica.weather.common.getThemeColor
import me.spica.weather.databinding.CardDailyWeatherBinding
import me.spica.weather.model.weather.Weather
import me.spica.weather.tools.getColorWithAlpha
import me.spica.weather.tools.hide
import me.spica.weather.tools.show
import me.spica.weather.ui.weather.DayInfoAdapter


/**
 * 天级的天气信息卡
 */
class DailyWeatherCard : CardLinearlayout, SpicaWeatherCard {

//  private val dailyWeatherAdapter by lazy {
//    DailWeatherAdapter()
//  }

    private val dayInfoAdapter by lazy {
        DayInfoAdapter()
    }

    private val binding = CardDailyWeatherBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override var animatorView: View = this

    override var enterAnim: AnimatorSet = AnimatorSet()
    override var index: Int = 2
    override var hasInScreen: Boolean = false


    init {
        resetAnim()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun bindData(weather: Weather) {
        val items = weather.dailyWeather
//    context.dividerBuilder()
//      .showFirstDivider()
//      .colorRes(R.color.window_background)
//      .size(2.dp.toInt())
//      .build()
//      .addTo(binding.rvDayInfo)

        val themeColor = weather.getWeatherType().getThemeColor()

        val bgDrawable = binding.root.background
        bgDrawable.colorFilter = PorterDuffColorFilter(
            getColorWithAlpha(.05f, themeColor), PorterDuff.Mode.SRC_IN
        )
        binding.root.background = bgDrawable


        binding.cardName.setTextColor(themeColor)
        dayInfoAdapter.themeColor = themeColor

        binding.rvDayInfo.adapter = dayInfoAdapter
        dayInfoAdapter.items.clear()
        dayInfoAdapter.items.addAll(items)
        dayInfoAdapter.syncTempMaxAndMin()

        binding.tipDesc.text = weather.descriptionForToWeek
        if (weather.descriptionForToday.isNullOrEmpty()) {
            binding.tipDesc.hide()
        } else {
            binding.tipDesc.show()
        }

        (parent as RecyclerView?)?.post {
            dayInfoAdapter.notifyDataSetChanged()
        }


//    doOnMainThreadIdle({
//
//    })
    }


}
