package me.spica.weather.ui.city

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.fondesa.recyclerviewdivider.dividerBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.spica.weather.R
import me.spica.weather.base.BindingActivity
import me.spica.weather.databinding.ActivityCitySelectBinding
import me.spica.weather.model.city.CityBean
import me.spica.weather.tools.doOnMainThreadIdle
import me.spica.weather.tools.dp
import me.spica.weather.tools.keyboard.FluidContentResizer
import me.spica.weather.tools.toast
import javax.inject.Inject

/**
 * 城市选择【全部城市】
 */
@AndroidEntryPoint
class CitySelectActivity : BindingActivity<ActivityCitySelectBinding>() {

  // 城市列表
  @Inject
  lateinit var cityList: List<CityBean>

  private val cityAdapter = CityAdapter()

  private val cityViewModel by viewModels<CityViewModel>()

  // 用于显示的列表
  private val rvItems = arrayListOf<CityBean>()

  private val textWatch = object : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

    override fun afterTextChanged(edit: Editable) {
      rvItems.clear()

      if (edit.toString().isEmpty()) {
        cityAdapter.diffUtil.submitList(rvItems.toList())
        return
      }

      cityList.forEach {
        if (it.cityName.contains(edit.trim()) ||
          it.sortName.contains(edit.trim())
        ) {
          rvItems.add(it)
        }
      }
      cityAdapter.diffUtil.submitList(rvItems.toList())
    }
  }

  override fun initializer() {
    init()
  }


  private fun init() {
    FluidContentResizer.listen(this)
    viewBinding.etCityName.addTextChangedListener(textWatch)


    val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_slide_right)
    viewBinding.rvList.layoutAnimation = animation

    dividerBuilder()
      .colorRes(android.R.color.transparent)
      .size(12.dp.toInt())
      .showFirstDivider()
      .showLastDivider()
      .build().addTo(viewBinding.rvList)


    viewBinding.rvList.adapter = cityAdapter

    // 设置点击item
    cityAdapter.itemClickListener = { cityBean ->
      cityViewModel.selectCity(cityBean)
      supportFinishAfterTransition()
    }

    viewBinding.btnBack.setOnClickListener {
      supportFinishAfterTransition()
    }

    viewBinding.btnCancel.setOnClickListener {
      viewBinding.etCityName.setText("")
    }

    // 提示结果
    lifecycleScope.launch {
      cityViewModel.tipsFlow.filter {
        it.isNotBlank()
      }.collectLatest {
        withContext(Dispatchers.Main) {
          toast(it)
        }
      }
    }

    // 添加数据
    lifecycleScope.launch {
      rvItems.clear()
      doOnMainThreadIdle({
        cityAdapter.diffUtil.submitList(rvItems.toList())
        viewBinding.rvList.scheduleLayoutAnimation()
        viewBinding.etCityName.isEnabled = true
      })
    }
  }


  override fun setupViewBinding(inflater: LayoutInflater): ActivityCitySelectBinding =
    ActivityCitySelectBinding.inflate(inflater)
}
