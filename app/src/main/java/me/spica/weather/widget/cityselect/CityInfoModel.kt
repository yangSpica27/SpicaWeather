package me.spica.weather.widget.cityselect




data class CityInfoModel(
    val type: Int,
    val cityName: String,
    val sortId: String,
    val sortName: String,
    val extra: Any?
){

    companion object{
        const val TYPE_NORMAL = 0
        const val TYPE_CURRENT = 1 //当前城市
        const val TYPE_HOT = 2 //热门城市
    }
}