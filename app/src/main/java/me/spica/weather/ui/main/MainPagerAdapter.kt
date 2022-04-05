package me.spica.weather.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import me.spica.weather.model.city.CityBean
import me.spica.weather.ui.weather.WeatherFragment

class MainPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {


    val diffUtil = AsyncListDiffer(
        this,
        object : DiffUtil.ItemCallback<CityBean>() {
            override fun areItemsTheSame(oldItem: CityBean, newItem: CityBean): Boolean = oldItem.cityName == newItem.cityName
            override fun areContentsTheSame(oldItem: CityBean, newItem: CityBean): Boolean = true
        }
    )

    override fun getItemCount(): Int = diffUtil.currentList.size

    override fun createFragment(position: Int): Fragment {
        val fragment = WeatherFragment()
        fragment.arguments = Bundle().apply {
            putParcelable("city", diffUtil.currentList[position])
        }
        return fragment
    }



}