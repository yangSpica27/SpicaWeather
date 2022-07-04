package me.spica.weather.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.appcompat.widget.AppCompatImageView;
import java.lang.ref.WeakReference;


public class ActivityScreenShotImageView extends AppCompatImageView {
    
    float width, height, mRadius;
    
    public ActivityScreenShotImageView(Context context) {
        super(context);
        init(null);
    }
    
    public ActivityScreenShotImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }
    
    public ActivityScreenShotImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    
    private void init(AttributeSet attrs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_HARDWARE, null);
        }
    }
    
    public void setRadius(float mRadius) {
        this.mRadius = mRadius;
        invalidate();
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        if (width >= mRadius && height > mRadius) {
            if (isScreenshotSuccess) {
                canvas.drawColor(Color.BLACK);
            }
            Path path = new Path();
            path.moveTo(mRadius, 0);
            path.lineTo(width - mRadius, 0);
            path.quadTo(width, 0, width, mRadius);
            path.lineTo(width, height - mRadius);
            path.quadTo(width, height, width - mRadius, height);
            path.lineTo(mRadius, height);
            path.quadTo(0, height, 0, height - mRadius);
            path.lineTo(0, mRadius);
            path.quadTo(0, 0, mRadius, 0);
            
            canvas.clipPath(path);
        }
        try {
            canvas.drawColor(Color.WHITE);
            super.onDraw(canvas);
        } catch (Exception e) {
        }
    }
    
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isAttachedToWindow() && !isScreenshotSuccess) refreshImage();
    }
    
    private int screenWidth, screenHeight;
    
    private void refreshImage() {
        if (screenWidth != getMeasuredWidth() || screenHeight != getMeasuredHeight()) {
            screenWidth = getMeasuredWidth();
            screenHeight = getMeasuredHeight();
            doScreenshotActivityAndZoom();
        }
    }
    
    private void doScreenshotActivityAndZoom() {
        View contentView = getContentView();
        if (contentView == null) {
            return;
        }
        //先执行一次绘制，防止出现闪屏问题
        if (!inited) drawViewImage(contentView);
        contentView.post(new Runnable() {
            @Override
            public void run() {
                //当view渲染完成后再次通知刷新一下界面（当旋转屏幕执行时，很可能出现渲染延迟的问题）
                drawViewImage(contentView);
                inited = true;
            }
        });
    }

    public void setScreenshotActivity(Activity screenshotActivity) {
        this.screenshotActivity = screenshotActivity;
    }

    private Activity screenshotActivity;
    
    private View getContentView() {
        return (FrameLayout) screenshotActivity.getWindow().getDecorView();
    }
    
    private boolean inited = false;
    private boolean isScreenshotSuccess;
    private WeakReference<View> contentView;
    public static boolean hideContentView = false;
    
    private void drawViewImage(View view) {
        if (view.getWidth() == 0 || view.getHeight() == 0) return;
        view.buildDrawingCache();
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        view.setDrawingCacheEnabled(true);
        setImageBitmap(Bitmap.createBitmap(view.getDrawingCache(), 0, 0, view.getWidth(), view.getHeight()));
        view.destroyDrawingCache();
        if (hideContentView) {
            if (contentView != null && contentView.get() != null) {
                contentView.get().setVisibility(VISIBLE);
            }
            View childView = ((ViewGroup) view).getChildAt(0);
            childView.setVisibility(GONE);
            contentView = new WeakReference<>(childView);
        }
        isScreenshotSuccess = true;
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (contentView != null && contentView.get() != null && hideContentView) {
            contentView.get().setVisibility(VISIBLE);
            contentView.clear();
        }
    }
}
