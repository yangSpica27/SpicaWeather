package me.spica.weather.model.city

data class CityBean(
    val cityName: String, // 名称
    val sortName: String, // 拼音
    val lon: String, // 经度
    val lat: String // 纬度
) {
    val sortId: String = if (sortName.isNotEmpty()) {
        sortName[0].toString()
    } else {
        "#"
    }
}