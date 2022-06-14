package me.spica.weather.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import me.spica.weather.model.city.CityBean
import me.spica.weather.ui.weather.WeatherFragment

class MainPagerAdapter(
    fragmentActivity: FragmentActivity,
    val scrollListener: View.OnScrollChangeListener
) :
    FragmentStateAdapter(fragmentActivity) {

    var onColorChange: (Int) -> Unit = {}


    private var backgroundColor = Color.parseColor("#1F787474")


    val diffUtil = AsyncListDiffer(
        this,
        object : DiffUtil.ItemCallback<CityBean>() {
            override fun areItemsTheSame(oldItem: CityBean, newItem: CityBean): Boolean =
                oldItem.cityName == newItem.cityName

            override fun areContentsTheSame(oldItem: CityBean, newItem: CityBean): Boolean = false

        }
    )

    override fun getItemCount(): Int = diffUtil.currentList.size

    override fun createFragment(position: Int): Fragment {
        val fragment = WeatherFragment(scrollListener)
        fragment.arguments = Bundle().apply {
            putParcelable("city", diffUtil.currentList[position])
        }
        fragment.onColorChange = {
            if (it != backgroundColor) {
                backgroundColor = it
                onColorChange(it)
            }
        }
        return fragment
    }


}