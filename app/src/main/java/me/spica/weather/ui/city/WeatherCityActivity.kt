package me.spica.weather.ui.city

import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.fondesa.recyclerviewdivider.dividerBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.spica.weather.R
import me.spica.weather.base.BindingActivity
import me.spica.weather.databinding.ActivityCityBinding
import me.spica.weather.model.city.CityBean
import me.spica.weather.tools.dp

/**
 * 城市选择
 */
@AndroidEntryPoint
class WeatherCityActivity : BindingActivity<ActivityCityBinding>() {

    private val cityWeatherAdapter = WeatherCityAdapter(this)

    private val cityViewModel: CityViewModel by viewModels()

    // 滑动删除工具类
    private val itemTouchHelper = ItemTouchHelper(
        object : ItemTouchHelper.Callback() {

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val item = cityWeatherAdapter
                    .diffUtil.currentList[viewHolder.adapterPosition]
                val dragFlags = 0
                // 只允许左右滑动
                val swipeFlags: Int = if (item.isSelected) {
                    // 当前选中的不允许被删除
                    0
                } else {
                    ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
                }
                // 被选中的不允许滑动

                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float,
                dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                //  滑动时候变透明
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val value = 1 - Math.abs(dX) / viewHolder.itemView.width
                    viewHolder.itemView.alpha = value
                }
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                // 滑动结束清除效果
                viewHolder.itemView.alpha = 1F
            }

            // 不给排序
            override fun onMove(
                recyclerView: RecyclerView, viewHolder:
                RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val item = cityWeatherAdapter
                        .diffUtil.currentList[viewHolder.adapterPosition]
                    cityViewModel.deleteItem(item)
                }
            }

        }
    )

    override fun initializer() {
        val animation = AnimationUtils.loadLayoutAnimation(
            this,
            R.anim.layout_animation_fall_down
        )
        itemTouchHelper.attachToRecyclerView(viewBinding.rvCity)

        dividerBuilder()
            .colorRes(R.color.line_divider)
            .size(1.dp.toInt())
            .build().addTo(viewBinding.rvCity)

        viewBinding.rvCity.layoutAnimation = animation
        viewBinding.rvCity.adapter = cityWeatherAdapter

        cityWeatherAdapter.itemClickListener = {
            cityViewModel.selectCity(it)
        }

        viewBinding.toolbar.setNavigationOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            cityViewModel.allCityFlow.collectLatest {
                if (it.isEmpty()) {
                    cityViewModel.addCity(
                        CityBean(
                            lon = "118.78",
                            lat = "32.04",
                            cityName = "南京",
                            sortName = "NanJing"
                        )
                    )
                }
                cityWeatherAdapter.diffUtil.submitList(it.reversed())
            }
        }
    }


    override fun setupViewBinding(inflater: LayoutInflater): ActivityCityBinding {
        return ActivityCityBinding.inflate(inflater)
    }
}
