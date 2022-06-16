package me.spica.weather.view.weather_bg

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.util.*


class RainDrop(val mRandom: Random,val mPaint: Paint) {

    private var mStartX = 30
    private var mStartY = 0

    private var mK = 3
    private var mRainSizeW = 8
    private var mSpeed = 3
    private var mDirectX = 1

    private var mViewW = 0
    private var mViewH = 0


    private var mRandRainSizeW = 5
    private var mRandRainSpeed = 5
    private var mRainColor = Color.BLACK
    private var mRainSize = 3


    fun init(w: Int, h: Int) {
        mViewW = w
        mViewH = h
    }

    fun initPos() {
        mStartX = mRandom.nextInt(mViewW)
        mStartY = mRandom.nextInt(mViewH)
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
        canvas.drawLine(
            mStartX * 1f,
            mStartY * 1f,
            mStartX * 1f + mRainSizeW,
            mStartY * 1f + mRainSizeW * mK,
            mPaint
        )
        mStartX += mSpeed * mDirectX
        mStartY += mSpeed * mK
        if (mStartX > mViewW || mStartY > mViewH) {
            initPos()
        }
    }
}