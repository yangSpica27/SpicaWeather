package me.spica.weather.widget.cityselect

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import me.spica.weather.R
import me.spica.weather.databinding.LayoutCitySelectViewBinding
import me.spica.weather.model.city.Provinces
import me.spica.weather.widget.cityselect.CustomItemDecoration.TitleDecorationCallback
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class CitySelectView : ConstraintLayout {

    private var viewBinding: LayoutCitySelectViewBinding

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val list: MutableList<CityInfoModel> = mutableListOf()

    private val cacheList: MutableList<CityInfoModel> = mutableListOf()

    private val searchList: MutableList<CityInfoModel> = mutableListOf()

    private lateinit var mainAdapter: MainAdapter

    init {
        View.inflate(context, R.layout.layout_city_select_view, this)
        viewBinding = LayoutCitySelectViewBinding.bind(this)
        viewBinding.recyclerView.addItemDecoration(
            CustomItemDecoration(context,
                object : TitleDecorationCallback {
                    override fun getGroupId(position: Int): String {
                        //这个是用来比较是否是同一组数据的
                        return list[position].sortId
                    }

                    override fun getGroupName(position: Int): String {
                        val (type, _, sortId, sortName) = list.get(position)
                        return if (type == CityInfoModel.TYPE_CURRENT || type == CityInfoModel.TYPE_HOT) {
                            sortName
                        } else sortId.toUpperCase()
                        //拼音都是小写的
                    }
                })
        )

        mainAdapter = MainAdapter(context)
        //设置item的点击事件
        mainAdapter.itemClickListener = {

        }

        //设置定位的点击时间
        mainAdapter.locationListener = {

        }


        viewBinding.recyclerView.adapter = mainAdapter

//        initData()
    }


    private fun initData() {


        val jsonStr = getJson()

        val moshi = Moshi.Builder().build()

        val jsonAdapter: JsonAdapter<Provinces> =
            moshi.adapter(Provinces::class.java)

        val provinces = jsonAdapter.fromJson(jsonStr)


    }


    private fun getJson(): String {
        //将json数据变成字符串
        val stringBuilder = StringBuilder()
        try {
            //获取assets资源管理器
            val assetManager = context.assets
            //通过管理器打开文件并读取
            val bf = BufferedReader(
                InputStreamReader(
                    assetManager.open("city.json")
                )
            )
            var line: String

            while (bf.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }


}