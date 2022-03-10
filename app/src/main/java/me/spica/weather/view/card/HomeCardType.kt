package me.spica.weather.view.card

enum class HomeCardType(val code: Int) {
    NOW_WEATHER(0),// 现在的天气
    HOUR_WEATHER(1), // 小时天气
    DAY_WEATHER(2), // 日级天气
    SUNRISE(3),// 日出日落
    AIR(4),
    LIFE_INDEX(5);// 生活指数
}