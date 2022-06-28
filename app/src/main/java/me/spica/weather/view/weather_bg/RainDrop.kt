package me.spica.weather.view.weather_bg

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import me.spica.weather.tools.dp
import java.util.*


class RainDrop(private val mRandom: Random, private val mPaint: Paint) {

    private var mStartX = 30
    private var mStartY = 0

    private var mK = 3
    private var mRainSizeW = 8
    private var mSpeed = 10
    private var mDirectX = 1

    private var mViewW = 0
    private var mViewH = 0


    private var mRandRainSizeW = 10.dp.toInt()
    private var mRandRainSpeed = 15
    private var mRainColor = Color.WHITE



    fun init(w: Int, h: Int) {
        mViewW = w
        mViewH = h
      if (mRandom.nextInt(10) <1){
          mRainColor = Color.YELLOW
      }

    }

    fun initPos() {
        mStartX = mRandom.nextInt(mViewW)
        mStartY = 0
        mRainSizeW = 5 + mRandom.nextInt(mRandRainSizeW)
        mK = 3 + mRandom.nextInt(5)
        mSpeed = 1 + mRandom.nextInt(mRandRainSpeed)
        mDirectX = if ((mSpeed + mK) % 2 == 0) {
            1
        } else {
         1
        }
    }

    fun rain(canvas: Canvas) {
        mPaint.color = mRainColor
        canvas.drawLine(
            mStartX * 1f,
            mStartY * 1f,
            mStartX * 1f - mRainSizeW,
            mStartY * 1f + mRainSizeW * mK,
            mPaint
        )
        mStartX -= mSpeed * mDirectX
        mStartY += mSpeed * mK
        if (mStartX > mViewW || mStartY > mViewH) {
            initPos()
        }
    }
}