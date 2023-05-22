package me.spica.weather.view.weather_drawable

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat
import me.spica.weather.R
import me.spica.weather.tools.dp

/**
 * 雾天的天气效果
 */
class FoggyDrawable(private val context: Context) : WeatherDrawable() {


    // 锚点
    private val points = arrayListOf<PointF>()


    private val interval = 12.dp

    private var isBack = false

    private val anim = ObjectAnimator.ofFloat(0f, 1f)
        .apply {
            interpolator = LinearInterpolator()
            addUpdateListener {
                if (it.animatedValue as Float == 1f) {
//                    isBack = !isBack
                }
            }
            duration = 800L
            repeatCount = Animation.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }

    fun startAnim() {
        anim.start()
    }

    fun cancelAnim() {
        anim.cancel()
    }

    @WorkerThread
    fun calculate(viewWidth: Int, viewHeight: Int) {
        val currentPadding = (anim.animatedValue as Float) * interval
        synchronized(points) {
            points.clear()
            // 左
            for (index in 0..5) {
                val heightStep = index * ((viewHeight - 2 * interval)/ 5f)
                val pointF = PointF(interval, heightStep)
                if (index % 2 == 0 == isBack) {
                    pointF.x += currentPadding
                } else {
                    pointF.x -= currentPadding
                }
                points.add(pointF)
            }
            // 下
            for (index in 0..5) {
                val widthStep = index * ((viewWidth - 2 * interval)/ 5f)

                val pf = PointF(widthStep, viewHeight * 1f - interval)

                if (index % 2 == 0 == isBack) {
                    pf.y += currentPadding
                } else {
                    pf.y -= currentPadding
                }

                points.add(
                    pf
                )
            }
            // 右
            for (index in 0..5) {
                val heightStep = index * ((viewHeight - 2 * interval)/ 5f)

                val pf = PointF(viewWidth - interval, viewHeight - interval - heightStep)

                if (index % 2 == 0 == isBack) {
                    pf.x += currentPadding
                } else {
                    pf.x -= currentPadding
                }
                points.add(pf)
            }
            // 上
            for (index in 0..5) {
                val widthStep = index * ((viewWidth - 2 * interval)/ 5f)

                val pf = PointF(viewWidth - interval - widthStep, interval)

                if (index % 2 == 0 == isBack) {
                    pf.y += currentPadding
                } else {
                    pf.y -= currentPadding
                }

                points.add(pf)
            }
        }

    }

    private val fogPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.water_color)
        pathEffect = CornerPathEffect(12.dp)
    }

    private val pointPint = Paint(Paint.ANTI_ALIAS_FLAG).apply {

    }

    private val path = Path()

    override fun doOnDraw(canvas: Canvas, width: Int, height: Int) {
        synchronized(points) {
            if (points.isEmpty()) return
            path.reset()
            path.moveTo(points[0].x, points[0].y)
            points.forEach { pointF ->
                path.lineTo(pointF.x, pointF.y)
                canvas.drawPoint(pointF.x, pointF.y, pointPint)
            }
            path.close()
            canvas.drawPath(path, fogPaint)
            canvas.rotate(180f)
            canvas.scale(1.1f, 1.1f)
            canvas.drawPath(path, fogPaint)
        }
    }
}