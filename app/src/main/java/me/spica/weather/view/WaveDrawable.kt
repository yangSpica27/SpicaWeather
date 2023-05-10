package me.spica.weather.view

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.text.TextPaint
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.vectordrawable.graphics.drawable.AnimationUtilsCompat
import com.google.android.material.animation.AnimationUtils
import me.spica.weather.tools.dp
import timber.log.Timber
import kotlin.random.Random
import kotlin.random.asJavaRandom
import kotlin.random.asKotlinRandom

/**
 * @ClassName WaveDrawable
 * @Author Spica2 7
 * @Date 2023/5/8 16:58
 */
class WaveDrawable(parentView: View) {


    private val anchorPoints = arrayOfNulls<Point>(8)

    private val anchorRadius = arrayOfNulls<Int>(8)

    private val anchorPointAnimDirection = arrayOfNulls<Boolean>(8)


    // 半径
    private var radius = 80.dp.toInt()

    // 可变
    private var interval = 20.dp.toInt()


    private var waveColor = Color.GRAY

    private var frameDistance: Float = 6f


    fun setWaveColor(@ColorInt color: Int) {
        this.waveColor = color
    }


    fun setRadius(radius: Int) {
        this.radius = radius
        refreshPoint()
    }

    private fun refreshPoint() {
        for (index in anchorPoints.indices) {
            val angle = index * (360f / anchorPoints.size)
            val x = radius * Math.cos(angle * Math.PI / 180.0)
            val y = radius * Math.sin(angle * Math.PI / 180.0)
            anchorPoints[index] = Point(x.toInt(), y.toInt())
            anchorRadius[index] = ((radius - interval) + (2 * interval) * Random.nextFloat()).toInt()
            anchorPointAnimDirection[index] = index%2==0
        }
    }

    fun setInterval(interval: Int) {
        this.interval = interval
        refreshPoint()
    }

    private val animation = ValueAnimator.ofFloat(0f, 1f).apply {
        addUpdateListener {
            calculationAnim()
            parentView.postInvalidateOnAnimation()
        }
        repeatCount = Animation.INFINITE
        interpolator = LinearInterpolator()
    }


    fun start() {
        animation.start()
    }

    fun stop() {
        animation.cancel()
    }


    private fun calculationAnim() {
        for (index in anchorPoints.indices) {
            var currentRadius = anchorRadius[index] ?: radius
            if (anchorPointAnimDirection[index] != false) {
                currentRadius = Math.min(radius + interval, (currentRadius + frameDistance * Random.nextFloat()).toInt())
                if (currentRadius == radius + interval) {
                    anchorPointAnimDirection[index] = false
                }
            } else {
                currentRadius = Math.max(radius - interval, (currentRadius - frameDistance * Random.nextFloat()).toInt())
                if (currentRadius == radius - interval) {
                    anchorPointAnimDirection[index] = true
                }
            }



            val angle = index * (360f / anchorPoints.size)
            val x = currentRadius * Math.cos(angle * Math.PI / 180.0)
            val y = currentRadius * Math.sin(angle * Math.PI / 180.0)

            Timber.tag("x坐标").e("${x}px")
            Timber.tag("y坐标").e("${y}px")

            if (angle > 180) {
                anchorPoints[index] = Point(x.toInt(), y.toInt())
            } else {
                anchorPoints[index] = Point(x.toInt(), y.toInt())
            }
            anchorRadius[index] = currentRadius
        }

    }

    private val path = Path()


    fun draw(cX: Float, cY: Float, canvas: Canvas, paint: Paint) {

        path.reset()
        canvas.save()
        canvas.translate(cX, cY)

        for (index in anchorPoints.indices) {

            val currentPoint = anchorPoints[index] ?: Point()

            if (index == 0) {

                path.moveTo(currentPoint.x * 1f, currentPoint.y * 1f)
                continue
            }

            val lastPoint = anchorPoints[index - 1] ?: Point()

            path.quadTo(
                (lastPoint.x + currentPoint.x) / 2f, (lastPoint.y + currentPoint.y) / 2f, currentPoint.x * 1f, currentPoint.y * 1f
            )

        }

        path.close()

        canvas.drawPath(path, paint)

        canvas.restore()
    }

}