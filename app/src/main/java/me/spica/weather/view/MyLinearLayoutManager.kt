package me.spica.weather.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler


class MyLinearLayoutManager : LinearLayoutManager {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        // 先把所有的View先从RecyclerView中detach掉，然后标记为"Scrap"状态，表示这些View处于可被重用状态(非显示中)。
        // 实际就是把View放到了Recycler中的一个集合中。
        detachAndScrapAttachedViews(recycler)
        calculateChildrenSite(recycler)
    }

    private var totalHeight = 0
    private fun calculateChildrenSite(recycler: Recycler) {
        totalHeight = 0
        for (i in 0 until itemCount) {
            // 遍历Recycler中保存的View取出来
            val view: View = recycler.getViewForPosition(i)
            addView(view) // 因为刚刚进行了detach操作，所以现在可以重新添加
            measureChildWithMargins(view, 0, 0) // 通知测量view的margin值
            val width = getDecoratedMeasuredWidth(view) // 计算view实际大小，包括了ItemDecorator中设置的偏移量。
            val height = getDecoratedMeasuredHeight(view)
            val mTmpRect = Rect()
            //调用这个方法能够调整ItemView的大小，以除去ItemDecorator。
            calculateItemDecorationsForChild(view, mTmpRect)

            // 调用这句我们指定了该View的显示区域，并将View显示上去，此时所有区域都用于显示View，
            //包括ItemDecorator设置的距离。
            layoutDecorated(view, 0, totalHeight, width, totalHeight + height)
            totalHeight += height
        }
    }
}