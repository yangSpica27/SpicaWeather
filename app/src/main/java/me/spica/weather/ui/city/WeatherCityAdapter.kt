package me.spica.weather.ui.city

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.spica.weather.R
import me.spica.weather.common.WeatherCodeUtils
import me.spica.weather.common.getThemeColor
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

  val diffUtil = AsyncListDiffer(
    this,
    object : DiffUtil.ItemCallback<CityBean>() {
      override fun areItemsTheSame(oldItem: CityBean, newItem: CityBean): Boolean = oldItem.cityName == newItem.cityName
      override fun areContentsTheSame(oldItem: CityBean, newItem: CityBean): Boolean = false
    }
  )


  var itemClickListener: (CityBean) -> Unit = {}

  class ViewHolder(val itemBinding: ItemWeatherCityBinding) :
    RecyclerView.ViewHolder(itemBinding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val itemBinding = ItemWeatherCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ViewHolder(itemBinding)
  }

  @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val bgDrawable = holder.itemView.context.getDrawable(R.drawable.bg_card)
    bgDrawable?.colorFilter =
      PorterDuffColorFilter(WeatherCodeUtils.getWeatherCode(
        diffUtil.currentList[position].iconId
      )
        .getThemeColor(), PorterDuff.Mode.SRC_IN)
    holder.itemBinding.root.background = bgDrawable
    holder.itemBinding.tvCityName.text = diffUtil.currentList[position].cityName
    holder.itemBinding.tvLocation.text = "东经${diffUtil.currentList[position].lon} 北纬${diffUtil.currentList[position].lat}"
    if (diffUtil.currentList[position].isSelected) {
      holder.itemBinding.tvIsDefault.show()
      holder.itemBinding.root.setOnTouchListener { _, _ ->
        // 不可操作目前选中的item
        return@setOnTouchListener true
      }
    } else {
      holder.itemBinding.root.setOnTouchListener(null)
      holder.itemBinding.tvIsDefault.hide()
    }
    holder.itemView.rootView.setOnClickListener {

      itemClickListener(diffUtil.currentList[position])

      holder.itemBinding.root.postDelayed(
        {
          doOnMainThreadIdle({
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

  override fun getItemCount(): Int = diffUtil.currentList.size


}
