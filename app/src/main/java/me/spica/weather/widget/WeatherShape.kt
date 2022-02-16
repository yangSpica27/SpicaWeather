package me.spica.weather.widget

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import com.qweather.sdk.view.HeContext.context
import java.util.*

abstract class WeatherShape(val start: PointF, val end: PointF) {
    open var TAG = "WeatherShape"

    /**
     * 是否是正在被使用的状态
     */
    var isInUse = false

    /**
     * 是否是随机刷新的Shape
     */
    var isRandom = false

    /**
     * 下落的速度，特指垂直方向，子类可以实现自己水平方向的速度
     */
    var speed = 0.05f

    /**
     * shape的宽度
     */
    var width = 5f

    var shapeAlpha = 100

    var paint = Paint().apply {
        strokeWidth = width
        isAntiAlias = true
        alpha = alpha
    }

    // 总共下落的时间
    var lastTime = 0L

    // 原始x坐标位置
    var originX = 0f

    /**
     * 根据自己的规则计算加速度，如果是匀速直接 return 0
     */
    abstract fun getAcceleration(): Float

    /**
     * 绘制自身，这里在Shape是非使用的时候进行一些初始化操作
     */
    open fun draw(canvas: Canvas) {
        if (!isInUse) {
            lastTime += randomPre()
            initStyle()
            isInUse = true
        } else {
            drawWhenInUse(canvas)
        }
    }

    /**
     * Shape在使用的时候调用此方法
     */
    abstract fun drawWhenInUse(canvas: Canvas)

    /**
     * 初始化Shape风格
     */
    open fun initStyle() {
        val random = Random()
        // 获取随机透明度
        shapeAlpha = random.nextInt(155) + 50
        // 获得起点x偏移
        val translateX = random.nextInt(10).toFloat() + 5
        if (!isRandom) {
            start.x = translateX + originX
            end.x = translateX + originX
        } else {
            // 如果是随机Shape，将x坐标随机范围扩大到整个屏幕的宽度
            val randomWidth = random.nextInt(context.resources.displayMetrics.widthPixels)
            start.x = randomWidth.toFloat()
            end.x = randomWidth.toFloat()
        }
        speed = randomSpeed(random)
        // 初始化length的工作留给之后对应的子类去实现
        // 初始化color也留给子类去实现
        paint.apply {
            alpha = shapeAlpha
            strokeWidth = width
            isAntiAlias = true
        }
        // 如果有什么想要做的，刚好可以在追加上完成，就使用这个函数
        wtc(random)
    }

    /**
     * Empty body, this will be invoke in initStyle
     * method.If current initStyle method can satisfy your need
     * but you still add something, by override this method
     * will be a good idea to solve the problem.
     */
    open fun wtc(random: Random): Unit {

    }

    abstract fun randomSpeed(random: Random): Float

    /**
     * 获取一个随机的提前量，让shape在竖屏上有一个初始的偏移
     */
    open fun randomPre(): Long {
        val random = Random()
        val pre = random.nextInt(1000).toLong()
        return pre
    }
}