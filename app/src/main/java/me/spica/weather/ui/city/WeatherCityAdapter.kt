package me.spica.weather.ui.city

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import me.spica.weather.R
import me.spica.weather.common.WeatherCodeUtils
import me.spica.weather.common.getAnimRes
import me.spica.weather.databinding.ItemWeatherCityBinding
import me.spica.weather.model.city.CityBean
import me.spica.weather.tools.doOnMainThreadIdle
import me.spica.weather.tools.hide
import me.spica.weather.tools.show
import me.spica.weather.ui.main.MainActivity

/**
 * 城市
 */
class WeatherCityAdapter(
    private val activity: Activity,
) : RecyclerView.Adapter<WeatherCityAdapter.ViewHolder>() {


    val items: MutableList<CityBean> = arrayListOf()

    init {
        setHasStableIds(true)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun notifyDataChanged(cities: List<CityBean>) {
        items.clear()
        items.addAll(cities)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return "${items[position].isSelected}${items[position].isSelected}".hashCode().toLong()
    }


    var itemClickListener: (CityBean) -> Unit = {}

    class ViewHolder(val itemBinding: ItemWeatherCityBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemWeatherCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // 代入文本
        holder.itemBinding.tvCityName.text = items[position].cityName


        val lottieAnimationView = holder.itemBinding.root.findViewById<LottieAnimationView>(R.id.lottie_view)
        lottieAnimationView.setMaxProgress(.5f)
        lottieAnimationView.setAnimation(
            WeatherCodeUtils.getWeatherCode(
                items[position].iconId
            ).getAnimRes()
        )
        holder.itemBinding.tvLocation.text = "东经${items[position].lon}° | 北纬${items[position].lat}°"
        if (items[position].isSelected) {
            holder.itemBinding.icSelected.show()
            holder.itemBinding.root.setOnTouchListener { _, _ ->
                // 不可操作目前选中的item
                return@setOnTouchListener true
            }
        } else {
            holder.itemBinding.root.setOnTouchListener(null)
            holder.itemBinding.icSelected.hide()
        }
        holder.itemView.rootView.setOnClickListener {
            itemClickListener(items[position])
            holder.itemBinding.root.postDelayed(
                {
                    doOnMainThreadIdle({
                        // 进入主页
                        val intent = Intent(activity, MainActivity::class.java)
                        val options = ActivityOptions.makeSceneTransitionAnimation(
                            activity,
                            holder.itemBinding.root,
                            "shared_element_container"
                        )
                        activity.startActivity(intent, options.toBundle())
                    })
                }, 500
            )
        }


    }

    override fun getItemCount(): Int = items.size


}
