package me.spica.weather.view.weatherIcon

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import java.util.*


@Suppress("unused")
class RainIcon : View {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) :
            super(context, attrs)

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)


    private val DEFAULT_SIZE = 300 // the default size if set "wrap_content"


    private val DEFAULT_DROP_MAX_NUMBER = 30 // 最大同屏数量

    private val DEFAULT_DROP_CREATION_INTERVAL = 50 // 创建间隔

    private val DEFAULT_DROP_MAX_LENGTH = 50 // 最大的水滴长度

    private val DEFAULT_DROP_MIN_LENGTH = 10 // 最小的水滴长度

    private val DEFAULT_DROP_SIZE = 15 //

    private val DEFAULT_DROP_MAX_SPEECH = 5f // 水滴最大速度

    private val DEFAULT_DROP_MIN_SPEECH = 1f // 水滴最小速度

    private val DEFAULT_DROP_SLOPE = -3f //

    private val mRainDropMaxNumber = DEFAULT_DROP_MAX_NUMBER
    private val mRainDropCreationInterval = DEFAULT_DROP_CREATION_INTERVAL
    private val mRainDropMinLength = DEFAULT_DROP_MIN_LENGTH
    private val mRainDropMaxLength = DEFAULT_DROP_MAX_LENGTH
    private val mRainDropSize = DEFAULT_DROP_SIZE

    private val mRainDropMaxSpeed = DEFAULT_DROP_MAX_SPEECH
    private val mRainDropMinSpeed = DEFAULT_DROP_MIN_SPEECH
    private val mRainDropSlope = DEFAULT_DROP_SLOPE


    private val DEFAULT_LEFT_CLOUD_COLOR: Int = Color.parseColor("#B0B0B0")
    private val DEFAULT_RIGHT_CLOUD_COLOR: Int = Color.parseColor("#DFDFDF")
    private val DEFAULT_RAIN_COLOR: Int = Color.parseColor("#80B9C5")
    private val CLOUD_SCALE_RATIO = 0.85f

    // 绘制云朵的paint
    private val cloudPaint = Paint()

    // 屏幕中的所有水滴
    private val mRainDrops //all the rain drops
            : MutableList<RainDrop> = mutableListOf()

    // 水滴的复用池
    private val recycler = Stack<RainDrop>()

    // 瞬时缓存
    private val mRemovedRainDrops: MutableList<RainDrop> = mutableListOf()

    // 左边的云朵云朵-动画对象
    private val mLeftCloudAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        repeatCount = ValueAnimator.INFINITE
        duration = 1000
        interpolator = LinearInterpolator()
        repeatMode = ValueAnimator.REVERSE
    }

    // 右边的云朵参数-动画对象
    private val mRightCloudAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        repeatCount = ValueAnimator.INFINITE
        duration = 1000
        interpolator = LinearInterpolator()
        repeatMode = ValueAnimator.REVERSE
    }


    private var mLeftCloudAnimatorPlayTime: Long = 0

    private var mRightCloudAnimatorPlayTime: Long = 0


    private var mLeftCloudAnimatorValue = 0f
    private var mRightCloudAnimatorValue = 0f

    // 设置动画参数
    fun setupAnimator() {
        mLeftCloudAnimatorPlayTime = 0
        mRightCloudAnimatorPlayTime = 0
        mLeftCloudAnimator.removeAllUpdateListeners()
        mLeftCloudAnimator.addUpdateListener { animation ->
            mLeftCloudAnimatorValue = animation.animatedValue as Float
            postInvalidateOnAnimation()
        }
        mRightCloudAnimator.removeAllUpdateListeners()
        mRightCloudAnimator.addUpdateListener { animation ->
            mRightCloudAnimatorValue = animation.animatedValue as Float
            postInvalidateOnAnimation()
        }
    }

    fun cancel() {
        if (mLeftCloudAnimator.isRunning) {
            mLeftCloudAnimatorPlayTime = mLeftCloudAnimator.currentPlayTime;
            mLeftCloudAnimator.cancel()
        }

        if (mRightCloudAnimator.isRunning) {
            mRightCloudAnimatorPlayTime = mRightCloudAnimator.currentPlayTime;
            mRightCloudAnimator.cancel()
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)

        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)

        var w = widthSpecSize
        var h = heightSpecSize

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            w = DEFAULT_SIZE
            h = DEFAULT_SIZE
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            w = DEFAULT_SIZE
            h = heightSpecSize
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            w = widthSpecSize
            h = DEFAULT_SIZE
        }

        setMeasuredDimension(w, h)
    }


    val mLeftCloudPath = Path()
    val mRightCloudPath = Path()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 根据view的大小重新绘制
        mLeftCloudPath.reset()
        mRightCloudPath.reset()


        mLeftCloudPath.addArc(RectF(
            0f,0f,w/3f,h/2f
        ),0f,120f)

        mLeftCloudPath.close()

        mRightCloudPath.addArc(RectF(
            0f,0f,w/3f,h/2f
        ),0f,120f)

        mRightCloudPath.close()


        // 动画重置
        setupAnimator()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

    }

    private data class RainDrop(
        var speedX: Float = 0f,  //雨滴x轴移动速度
        var speedY: Float = 0f,   //雨滴y轴移动速度
        var xLength: Float = 0f, //雨滴的x轴长度
        var yLength: Float = 0f, //雨滴的y轴长度
        var x: Float = 0f,        //雨滴的x轴坐标
        var y: Float = 0f,        //雨滴的y轴坐标
        var slope: Float = 0f //雨滴的斜率
    )


    // 首先判断栈中是否存在回收的对象，若存在，则直接复用，若不存在，则创建一个新的对象
    private fun obtainRainDrop(): RainDrop {
        return if (recycler.isEmpty()) {
            RainDrop()
        } else recycler.pop()

    }


    private val mRainRect: Rect = Rect()


    private var mRainDropCreationTime = -1L

    private val mOnlyRandom = Random()

    /** 创建水滴单元 **/
    private fun createRainDrop() {

        if (mRainDrops.size >= mRainDropMaxNumber
            || mRainRect.isEmpty
        ) {
            // 数量已达限制
            return
        }


        val current = System.currentTimeMillis()
        if (current - mRainDropCreationTime < mRainDropCreationInterval) {
            return
        }

        require(
            !(mRainDropMinLength > mRainDropMaxLength
                    || mRainDropMinSpeed > mRainDropMaxSpeed)
        ) { "The minimum value cannot be greater than the maximum value." }

        mRainDropCreationTime = current

        val rainDrop = obtainRainDrop()
        rainDrop.slope = mRainDropSlope
        rainDrop.speedX = mRainDropMinSpeed + mOnlyRandom.nextFloat() * mRainDropMaxSpeed
        rainDrop.speedY = rainDrop.speedX * Math.abs(rainDrop.slope)

        val rainDropLength: Float =
            mRainDropMinLength * 1f + mOnlyRandom.nextInt(mRainDropMaxLength - mRainDropMinLength)
        val degree = Math.toDegrees(Math.atan(rainDrop.slope.toDouble()))

        rainDrop.xLength = Math.abs(Math.cos(degree * Math.PI / 180) * rainDropLength).toFloat()
        rainDrop.yLength = Math.abs(Math.sin(degree * Math.PI / 180) * rainDropLength).toFloat()

        rainDrop.x = mRainRect.left * 1f + mOnlyRandom.nextInt(mRainRect.width())

        rainDrop.y = mRainRect.top - rainDrop.yLength


        mRainDrops.add(rainDrop)


    }


}
