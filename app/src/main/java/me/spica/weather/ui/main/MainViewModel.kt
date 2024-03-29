package me.spica.weather.ui.main

import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.spica.weather.model.city.CityBean
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


    val selectCityFlow = cityRepository.selectedCityFlow()


    @WorkerThread
    fun  getAllCity() = cityRepository.allCityList()


 fun selectCity(cityBean: CityBean){
     viewModelScope.launch(Dispatchers.IO) {
         cityRepository.selected(cityBean)
     }
 }


    fun changeCity(cityBean: CityBean){
        viewModelScope.launch(Dispatchers.IO){
            cityRepository.selected(cityBean)
        }

    }

}