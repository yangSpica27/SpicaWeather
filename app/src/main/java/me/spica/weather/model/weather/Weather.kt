package me.spica.weather.model.weather

data class Weather(
    val todayWeather: NowWeatherBean,
    val dailyWeather: List<DailyWeatherBean>,
    val hourlyWeather: List<HourlyWeatherBean>,
    val lifeIndexes: List<LifeIndexBean>
)