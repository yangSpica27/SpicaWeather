package me.spica.weather.view.threeD;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class TranslationConstraintLayout extends ConstraintLayout {
  private int mCenterX;
  private int mCenterY;
  private float mCanvasRotateX = 0;
  private float mCanvasRotateY = 0;
  private final float MAX_ROTATE_DEGREE = .4f;
  private final Matrix mMatrix = new Matrix();
  private final Camera mCamera = new Camera();

  private ValueAnimator steadyAnim;

  public TranslationConstraintLayout(@NonNull Context context) {
    super(context);
  }

  public TranslationConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public TranslationConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
    mCenterX = getWidth() / 2;
    mCenterY = getHeight() / 2;
    rotateCanvas(canvas);
    return super.drawChild(canvas, child, drawingTime);
  }

  private void rotateCanvas(Canvas canvas) {
    mMatrix.reset();
    mCamera.save();
    mCamera.rotateX(mCanvasRotateX);
    mCamera.rotateY(mCanvasRotateY);
    mCamera.getMatrix(mMatrix);
    mCamera.restore();
    mMatrix.preTranslate(-mCenterX, -mCenterY);
    mMatrix.postTranslate(mCenterX, mCenterY);

    canvas.concat(mMatrix);
  }

  private long touchTime = 0L;

  @SuppressLint("ClickableViewAccessibility") @Override
  public boolean onTouchEvent(MotionEvent event) {
    float x = event.getX();
    float y = event.getY();

    int action = event.getActionMasked();
    switch (action) {
      case MotionEvent.ACTION_DOWN: {
        cancelSteadyAnimIfNeed();
        touchTime = System.currentTimeMillis();
        rotateCanvasWhenMove(x, y);
        setParentRequestDisallowInterceptTouchEvent(true, getParent());
        postInvalidateOnAnimation();
        return true;
      }
      case MotionEvent.ACTION_MOVE: {
        rotateCanvasWhenMove(x, y);
        postInvalidateOnAnimation();
        return true;
      }
      case MotionEvent.ACTION_UP: {
        cancelSteadyAnimIfNeed();
        startNewSteadyAnim();
        setParentRequestDisallowInterceptTouchEvent(false, getParent());
        return true;
      }
    }

    return super.onTouchEvent(event);
  }

  private void setParentRequestDisallowInterceptTouchEvent(Boolean b, ViewParent parent) {
    if (parent != null) {
      if (parent instanceof RecyclerView) {
        parent.requestDisallowInterceptTouchEvent(b);
        ((RecyclerView) parent).setNestedScrollingEnabled(!b);
        return;
      }
      setParentRequestDisallowInterceptTouchEvent(b, parent.getParent());
    }
  }

  private void rotateCanvasWhenMove(float x, float y) {
    float dx = x - mCenterX;
    float dy = y - mCenterY;

    float percentX = dx / mCenterX;
    float percentY = dy / mCenterY;

    if (percentX > 1f) {
      percentX = 1f;
    } else if (percentX < -1f) {
      percentX = -1f;
    }
    if (percentY > 1f) {
      percentY = 1f;
    } else if (percentY < -1f) {
      percentY = -1f;
    }

    mCanvasRotateY = MAX_ROTATE_DEGREE * percentX;
    mCanvasRotateX = -(MAX_ROTATE_DEGREE * percentY);
  }

  private void cancelSteadyAnimIfNeed() {
    if (steadyAnim != null && (steadyAnim.isStarted() || steadyAnim.isRunning())) {
      steadyAnim.cancel();
    }
  }

  private void startNewSteadyAnim() {
    final String propertyNameRotateX = "mCanvasRotateX";
    final String propertyNameRotateY = "mCanvasRotateY";
    PropertyValuesHolder holderRotateX = PropertyValuesHolder.ofFloat(propertyNameRotateX, mCanvasRotateX, 0);
    PropertyValuesHolder holderRotateY = PropertyValuesHolder.ofFloat(propertyNameRotateY, mCanvasRotateY, 0);
    steadyAnim = ValueAnimator.ofPropertyValuesHolder(holderRotateX, holderRotateY);
    steadyAnim.setDuration(500);
    steadyAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        mCanvasRotateX = (float) animation.getAnimatedValue(propertyNameRotateX);
        mCanvasRotateY = (float) animation.getAnimatedValue(propertyNameRotateY);
        postInvalidateOnAnimation();
      }
    });
    steadyAnim.start();
  }



}
