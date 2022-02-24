@file:Suppress("unused")
package me.spica.weather.widget.weatherIcon

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import me.spica.weather.common.WeatherType

@Suppress("unused")
class WeatherIconView : View {

    private var drawDelegate: DrawDelegate? = null

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) :
        super(context, attrs)

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    fun setTypeAndDraw(weatherType: WeatherType) {
        drawDelegate?.stopDraw()
        drawDelegate?.init(context, this)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawDelegate?.onDraw(canvas)
    }

    interface DrawDelegate {

        // 绘制
        fun onDraw(canvas: Canvas) {
        }

        //  初始化
        fun init(context: Context, view: View)

        // 停止绘制
        fun stopDraw()
    }
}
