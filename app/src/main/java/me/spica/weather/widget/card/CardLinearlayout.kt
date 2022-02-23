package me.spica.weather.widget.card

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.atan2

abstract class CardLinearlayout : LinearLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


//    private var ox = 0f
//    private var oy = 0f
//    private var nx = 0f
//    private var ny = 0f
//    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
//
//        when (ev.action) {
//            MotionEvent.ACTION_DOWN -> {
//                ox = ev.x
//                oy = ev.y
//            }
//            MotionEvent.ACTION_MOVE -> {
//                nx = ev.x
//                ny = ev.y
//
//                val angle = atan2(((ny - oy).toDouble()), ((nx - ox).toDouble()))
//                val theta = abs((angle * (180 / Math.PI)))
//                Timber.i("角度：${theta}")
//                return theta <= 46
//            }
//        }
//
//        return super.onInterceptTouchEvent(ev)
//    }


}