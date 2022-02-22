package me.spica.weather.ui.city

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.github.stuxuhai.jpinyin.PinyinFormat
import com.github.stuxuhai.jpinyin.PinyinHelper
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.spica.weather.R
import me.spica.weather.base.BindingActivity
import me.spica.weather.databinding.ActivityCitySelectBinding
import me.spica.weather.model.city.CityBean
import me.spica.weather.model.city.Province
import me.spica.weather.tools.doOnMainThreadIdle
import me.spica.weather.tools.dp
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * 城市选择
 */
class CitySelectActivity : BindingActivity<ActivityCitySelectBinding>() {

    // 城市列表
    private val cityList = arrayListOf<CityBean>()

    private val cityAdapter = CityAdapter()


    // 用于显示的列表
    private val rvItems = arrayListOf<CityBean>()

    private val provinces by lazy {
        val moshi = Moshi.Builder().build()
        val listOfCardsType = Types.newParameterizedType(
            List::class.java,
            Province::class.java
        )
        val jsonAdapter = moshi.adapter<List<Province>>(listOfCardsType)
        return@lazy jsonAdapter.fromJson(
            getJsonString(
                this
            )
        )
    }

    private val textWatch = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

        override fun afterTextChanged(edit: Editable) {
            rvItems.clear()
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


        viewBinding.etCityName.addTextChangedListener(textWatch)

        this
            .dividerBuilder()
            .size(1.dp.toInt())
            .colorRes(R.color.line_divider)
            .showFirstDivider()
            .build()
            .addTo(viewBinding.rvList)

        val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_slide_right)
        viewBinding.rvList.layoutAnimation = animation
        viewBinding.rvList.adapter = cityAdapter



        lifecycleScope.launch(Dispatchers.Default) {
            cityList.clear()
            rvItems.clear()
            cityList.addAll(
                provinces?.map {
                    CityBean(
                        cityName = it.name,
                        sortName = PinyinHelper.convertToPinyinString
                            (it.name, "", PinyinFormat.WITHOUT_TONE),
                        lon = it.log,
                        lat = it.lat
                    )
                }?.filter {
                    it.cityName.isNotEmpty()
                } ?: listOf<CityBean>()
            )

            provinces?.forEach {
                cityList.addAll(
                    it.children.map { city ->
                        CityBean(
                            cityName = city.name,
                            sortName = PinyinHelper.convertToPinyinString
                                (city.name, "", PinyinFormat.WITHOUT_TONE),
                            lon = city.log,
                            lat = city.lat
                        )
                    }
                )
            }

            cityList.sortBy {
                it.sortName
            }



            rvItems.addAll(cityList.filter {
                it.cityName.isNotEmpty()
            })


            doOnMainThreadIdle({
                cityAdapter.diffUtil.submitList(rvItems.toList())
                viewBinding.rvList.scheduleLayoutAnimation()
                viewBinding.etCityName.isEnabled = true
            })

        }

    }

    override fun setupViewBinding(inflater: LayoutInflater): ActivityCitySelectBinding = ActivityCitySelectBinding.inflate(inflater)


    /**
     * 读取assets下配置文件
     *
     * @param context 上下文
     * @return 内容
     */
    @Throws(IOException::class)
    private fun getJsonString(context: Context): String {
        var br: BufferedReader? = null
        val sb = StringBuilder()
        try {
            val manager = context.assets
            br = BufferedReader(InputStreamReader(manager.open("city.json")))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        } finally {
            try {
                br?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }
}