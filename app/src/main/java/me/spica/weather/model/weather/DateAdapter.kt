package me.spica.weather.model.weather

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.*


object DateAdapter {

    @ToJson
    fun dateToLong(date: Date) = date.time


    @FromJson
    fun longToDate(timeL: Long) = Date().apply {
        time = timeL
    }
}