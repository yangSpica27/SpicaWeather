package me.spica.weather.ui.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import me.spica.weather.model.city.CityBean
import me.spica.weather.persistence.repository.CityRepository
import javax.inject.Inject


@HiltViewModel
class CityViewModel @Inject
constructor(private val cityRepository: CityRepository) : ViewModel() {


     val allCityFlow = cityRepository.allCityFlow()

    private val _tips = MutableStateFlow("")

    val tipsFlow: Flow<String> = _tips.filterNotNull()

    fun selectCity(cityBean: CityBean) {
        viewModelScope.launch {
            cityRepository.selected(cityBean)
        }
    }


    fun addCity(cityBean: CityBean) {
        viewModelScope.launch(Dispatchers.IO) {
            _tips.value = cityRepository.addCity(cityBean)
        }
    }


    fun deleteItem(cityBean: CityBean) = cityRepository.deleteCity(cityBean)


}