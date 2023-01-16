package me.spica.weather.view.line;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;



public class IndexHorizontalScrollView2 extends HorizontalScrollView {

    private HourlyForecastView2 hourlyForecastView;

    public IndexHorizontalScrollView2(Context context) {
        this(context, null);
    }

    public IndexHorizontalScrollView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexHorizontalScrollView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int offset = computeHorizontalScrollOffset();

        int maxOffset = computeHorizontalScrollRange() - getContext().getResources().getDisplayMetrics().widthPixels;
        if(hourlyForecastView != null){
            hourlyForecastView.setScrollOffset(offset, maxOffset);
        }
    }

    public void setToday24HourView(HourlyForecastView2 today24HourView){
        this.hourlyForecastView = today24HourView;
    }
}
