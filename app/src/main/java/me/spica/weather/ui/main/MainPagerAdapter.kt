package me.spica.weather.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import me.spica.weather.model.city.CityBean
import me.spica.weather.ui.weather.WeatherFragment

class MainPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {



    val cities: MutableList<CityBean> = mutableListOf()

    override fun getItemCount(): Int = cities.size

    override fun createFragment(position: Int): Fragment {
        val fragment = WeatherFragment()
        fragment.arguments = Bundle().apply {
            putParcelable("city", cities[position])
        }
        return fragment
    }

    override fun getItemId(position: Int): Long {
        return cities[position].cityName.hashCode().toLong()
    }


}