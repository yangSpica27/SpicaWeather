package me.spica.weather.ui.city

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.github.stuxuhai.jpinyin.PinyinFormat
import com.github.stuxuhai.jpinyin.PinyinHelper
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.spica.weather.R
import me.spica.weather.base.BindingFragment
import me.spica.weather.databinding.FragmentCityBinding
import me.spica.weather.model.city.CityBean
import me.spica.weather.model.city.Province
import me.spica.weather.tools.dp
import me.spica.weather.ui.main.MainViewModel
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class CityFragment : BindingFragment<FragmentCityBinding>() {

    private val viewModel: MainViewModel by activityViewModels()


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
                requireContext()
            )
        )
    }



    override fun setupViewBinding(inflater: LayoutInflater, container: ViewGroup?):
            FragmentCityBinding =
        FragmentCityBinding.inflate(inflater, container, false)

    override fun init() {
        viewBinding.etCityName.addTextChangedListener { et ->
            rvItems.clear()
            rvItems.addAll(
                cityList.filter {
                    if (et.isNullOrEmpty()) return@filter true
                    return@filter it.cityName.contains(et.toString(), true) ||
                            it.sortName.contains(et.toString(), true)
                }
            )
            cityAdapter.diffUtil.submitList(rvItems.toList())
        }

        requireContext()
            .dividerBuilder()
            .size(1.dp.toInt())
            .colorRes(R.color.line_divider)
            .showFirstDivider()
            .build()
            .addTo(viewBinding.rvList)

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
                } ?: listOf()
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

            rvItems.addAll(cityList)


            withContext(Dispatchers.Main) {
                cityAdapter.diffUtil.submitList(rvItems.toList())
            }

        }

    }


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