package me.spica.weather.view.threeD;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @ClassName TranslationTextView
 * @Description 3d效果文本
 * @Author Spica2 7
 * @Date 2022/8/8 14:23
 */
public class TranslationTextView extends AppCompatTextView {

  private int mCenterX;
  private int mCenterY;
  private float mCanvasRotateX = 0;
  private float mCanvasRotateY = 0;
  private final float mCanvasMaxRotateDegree = 20;
  private final Matrix mMatrix = new Matrix();
  private final Camera mCamera = new Camera();

  public TranslationTextView(@NonNull Context context) {
    super(context);
  }

  public TranslationTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public TranslationTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mCenterX = w / 2;
    mCenterY = h / 2;
  }

  @Override protected void onDraw(Canvas canvas) {
    rotateCanvas(canvas);
    super.onDraw(canvas);
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

  @SuppressLint("ClickableViewAccessibility") @Override
  public boolean onTouchEvent(MotionEvent event) {
    float x = event.getX();
    float y = event.getY();

    int action = event.getActionMasked();
    switch (action) {
      case MotionEvent.ACTION_DOWN: {
        rotateCanvasWhenMove(x, y);
        setParentRequestDisallowInterceptTouchEvent(true, getParent());
        return true;
      }
      case MotionEvent.ACTION_MOVE: {
        rotateCanvasWhenMove(x, y);
        invalidate();
        return true;
      }
      case MotionEvent.ACTION_UP: {
        mCanvasRotateY = 0;
        mCanvasRotateX = 0;
        invalidate();
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

    mCanvasRotateY = mCanvasMaxRotateDegree * percentX;
    mCanvasRotateX = -(mCanvasMaxRotateDegree * percentY);
  }

}
