package me.spica.weather.tools

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.qweather.sdk.bean.weather.WeatherDailyBean
import me.spica.weather.R
import kotlin.math.roundToInt


// 每日的气温图标样式
fun initDailyLineChart(lineChart: LineChart, context: Context, data: WeatherDailyBean) {

    val datas = data.daily.mapIndexed { index, dailyBean ->
        Entry(index.toFloat(), dailyBean.tempMax.toFloat())
    }

    val datas2 = data.daily.mapIndexed { index, dailyBean ->
        Entry(index.toFloat(), dailyBean.tempMin.toFloat())
    }

    val lineDataSet = LineDataSet(datas, "")
        .apply {
            setDrawValues(false)
            lineWidth = 1.dp
            setDrawCircleHole(true)
            setDrawFilled(false)
            circleRadius = 2.dp
            setDrawValues(true)
            valueTextSize = 4.dp
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.roundToInt()}℃"
                }
            }
            valueTextColor = ContextCompat.getColor(context, R.color.textColorPrimaryHint)
            circleHoleRadius = 1.dp
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setCircleColor(ContextCompat.getColor(context, R.color.textColorPrimaryHint))
            color = ContextCompat.getColor(context, R.color.textColorPrimaryHintLight)
        }

    val lineDataSet2 = LineDataSet(datas2, "")
        .apply {
            setDrawValues(false)
            lineWidth = 1.dp
            setDrawCircleHole(true)
            setDrawFilled(false)
            circleRadius = 2.dp
            setDrawValues(true)
            valueTextSize = 4.dp
            valueTextColor = ContextCompat.getColor(context, R.color.textColorPrimaryHint)
            circleHoleRadius = 1.dp
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.roundToInt()}℃"
                }
            }
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setCircleColor(ContextCompat.getColor(context, R.color.textColorPrimaryHint))
            color = ContextCompat.getColor(context, R.color.textColorPrimaryHintLight)
        }

    val lineData = LineData(lineDataSet, lineDataSet2)

    lineChart.data = lineData

    lineChart.xAxis.setDrawGridLines(true)

    lineChart.xAxis.setDrawAxisLine(false)

    lineChart.axisLeft.setDrawAxisLine(false)

    lineChart.axisLeft.setDrawGridLines(false)

    lineChart.axisLeft.setDrawLabels(false)

    lineChart.axisRight.setDrawLabels(false)

    lineChart.axisRight.setDrawAxisLine(false)

    lineChart.axisRight.setDrawGridLines(false)

    lineChart.xAxis.valueFormatter

    lineChart.xAxis.setDrawLabels(true)

    lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

    lineChart.xAxis.enableGridDashedLine(4.dp, 2.dp, 0f)

    lineChart.xAxis.gridLineWidth = 1.dp / 2F

    lineChart.xAxis.gridColor = ContextCompat.getColor(context, R.color.textColorPrimaryHintLight)

    lineChart.xAxis.textColor = ContextCompat.getColor(context, R.color.textColorPrimaryHint)

    lineChart.xAxis.typeface = Typeface.DEFAULT_BOLD

    lineChart.xAxis.textSize = 4.dp

    lineChart.legend.isEnabled = false

    lineChart.description.isEnabled = false

    lineChart.xAxis.mLabelHeight = 12.dp.toInt()

    lineChart.minOffset = 12.dp



    lineChart.xAxis.valueFormatter = object : ValueFormatter() {

        override fun getAxisLabel(value: Float, axis: AxisBase): String {
            return "2月${value.toInt()}日"
        }
    }



    lineChart.setTouchEnabled(false)


    lineChart.xAxis.yOffset = 4.dp

    lineChart.invalidate()

}