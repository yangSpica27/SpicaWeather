package me.spica.weather.widget.card

enum class HomeCardType(val code: Int) {
    NOW_WEATHER(0),// 现在的天气
    HOUR_WEATHER(1), // 小时天气
    DAY_WEATHER(2), // 日级天气
    SUNRISE(2),// 日出日落
    LIFE_INDEX(4);// 生活指数
}