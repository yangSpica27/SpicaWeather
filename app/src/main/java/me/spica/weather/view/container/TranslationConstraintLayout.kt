package me.spica.weather.view.container

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout


/**
 * 带有触摸唯一效果的容器
 */
class TranslationConstraintLayout : ConstraintLayout {

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  private var mCenterX = 0f
  private var mCenterY = 0f

  private var mCanvasRotateX = 0f

  private var mCanvasRotateY = 0f


  private val mCamera = Camera()

  private val mMatrix = Matrix()


  private val mCanvasMaxRotateDegree = 20

  override fun onDraw(canvas: Canvas) {
    mCenterX = width / 2f
    mCenterY = height / 2f
    rotateCanvas(canvas)
    super.onDraw(canvas)
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    val x = event.x
    val y = event.y
    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        rotateCanvasWhenMove(x, y)
        return true
      }
      MotionEvent.ACTION_MOVE -> {
        rotateCanvasWhenMove(x, y)
       invalidate()
        return true
      }
      MotionEvent.ACTION_UP -> {
        mCanvasRotateY = 0f
        mCanvasRotateX = 0f
        invalidate()
        return true
      }
    }
    return super.onTouchEvent(event)
  }

  private fun rotateCanvas(canvas: Canvas) {
    mMatrix.reset()
    mCamera.save()
    mCamera.rotateX(mCanvasRotateX)
    mCamera.rotateY(mCanvasRotateY)
    mCamera.getMatrix(mMatrix)
    mCamera.restore()
    mMatrix.preTranslate(-mCenterX, -mCenterY)
    mMatrix.postTranslate(mCenterX, mCenterY)
    canvas.concat(mMatrix)
  }


  private fun rotateCanvasWhenMove(x: Float, y: Float) {
    val dx = x - mCenterX
    val dy = y - mCenterY
    var percentX = dx / mCenterX
    var percentY = dy / mCenterY
    if (percentX > 1f) {
      percentX = 1f
    } else if (percentX < -1f) {
      percentX = -1f
    }
    if (percentY > 1f) {
      percentY = 1f
    } else if (percentY < -1f) {
      percentY = -1f
    }
    mCanvasRotateY = mCanvasMaxRotateDegree * percentX
    mCanvasRotateX = -(mCanvasMaxRotateDegree * percentY)
  }

}