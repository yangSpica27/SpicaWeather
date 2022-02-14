package me.spica.weather.ui.city

import android.view.LayoutInflater
import android.view.ViewGroup
import me.spica.weather.base.BindingFragment
import me.spica.weather.databinding.FragmentCityBinding

class CityFragment : BindingFragment<FragmentCityBinding>() {

    override fun setupViewBinding(inflater: LayoutInflater, container: ViewGroup?):
            FragmentCityBinding =
        FragmentCityBinding.inflate(inflater, container, false)

    override fun init() {

    }

}