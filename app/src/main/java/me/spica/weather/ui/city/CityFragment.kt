package me.spica.weather.ui.city

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import me.spica.weather.base.BindingFragment
import me.spica.weather.databinding.FragmentCityBinding
import me.spica.weather.ui.main.MainViewModel

class CityFragment : BindingFragment<FragmentCityBinding>() {

    private val viewModel: MainViewModel by activityViewModels()


    override fun setupViewBinding(inflater: LayoutInflater, container: ViewGroup?):
            FragmentCityBinding =
        FragmentCityBinding.inflate(inflater, container, false)

    override fun init() {

    }



}