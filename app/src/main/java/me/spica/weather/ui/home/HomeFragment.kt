package me.spica.weather.ui.home

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import me.spica.weather.R
import me.spica.weather.base.BindingFragment
import me.spica.weather.databinding.FragmentHomeBinding
import me.spica.weather.tools.dp

/**
 * 主页
 */
class HomeFragment : BindingFragment<FragmentHomeBinding>() {

    override fun setupViewBinding(inflater: LayoutInflater, container: ViewGroup?):
            FragmentHomeBinding = FragmentHomeBinding.inflate(
        inflater, container,
        false
    )

    override fun init() {
        initLineChart(viewBinding.weatherChart)
    }


    private fun initLineChart(lineChart: LineChart) {
        val datas = arrayListOf(
            Entry(10F, 29F),
            Entry(20F, 24F),
            Entry(30F, 23F),
            Entry(40F, 22F),
            Entry(50F, 32F),
        )

        val lineDataSet = LineDataSet(datas, "")
            .apply {
                setDrawValues(false)
                lineWidth = 1.dp
                setDrawCircleHole(true)
                setDrawFilled(false)
                circleRadius = 2.dp
                circleHoleRadius = 1.dp
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setCircleColor(ContextCompat.getColor(requireContext(), R.color.textColorPrimaryHint))
                color = ContextCompat.getColor(requireContext(), R.color.textColorPrimaryHintLight)
            }

        val lineData = LineData(lineDataSet).apply {

        }

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

        lineChart.xAxis.gridColor = ContextCompat.getColor(requireContext(), R.color.textColorPrimaryHintLight)


        lineChart.xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textColorPrimaryHint)

        lineChart.xAxis.typeface = Typeface.DEFAULT_BOLD

        lineChart.xAxis.textSize = 4.dp

        lineChart.legend.isEnabled = false

        lineChart.description.isEnabled = false

        lineChart.xAxis.mLabelHeight = 12.dp.toInt()

        lineChart.minOffset =  12.dp

        lineChart.xAxis.valueFormatter = object : ValueFormatter() {

            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return "2月${value.toInt()}日"
            }
        }

        lineChart.setTouchEnabled(false)

        lineChart.setScaleEnabled(false)

        lineChart.xAxis.yOffset = 4.dp

        lineChart.invalidate()

    }
}