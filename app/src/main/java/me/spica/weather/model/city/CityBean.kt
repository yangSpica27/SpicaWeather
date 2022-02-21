package me.spica.weather.model.city

import java.io.Serializable


data class CityBean(
    val cityName: String, // 名称
    val sortName: String, // 拼音
    val lon: String, // 经度
    val lat: String // 纬度
): Serializable {

    @Suppress("unused")
    private val serialVersionUID = -7060210544600464481L

    val sortId: String = if (sortName.isNotEmpty()) {
        sortName[0].toString()
    } else {
        "#"
    }
}