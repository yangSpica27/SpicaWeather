package me.spica.weather.ui.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import me.spica.weather.persistence.repository.CityRepository
import javax.inject.Inject

/**
 * 主页的viewModel
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val cityRepository: CityRepository,
) : ViewModel() {



    val allCityFlow = cityRepository.allCityFlow()

}