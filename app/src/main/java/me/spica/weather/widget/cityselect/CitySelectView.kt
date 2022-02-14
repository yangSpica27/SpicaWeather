package me.spica.weather.widget.cityselect

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.stuxuhai.jpinyin.ChineseHelper
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import me.spica.weather.R
import me.spica.weather.databinding.LayoutCitySelectViewBinding
import me.spica.weather.model.city.Province
import me.spica.weather.tools.hide
import me.spica.weather.tools.show
import me.spica.weather.widget.cityselect.CustomItemDecoration.TitleDecorationCallback
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


class CitySelectView : ConstraintLayout {

    private var viewBinding: LayoutCitySelectViewBinding

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val mainHandler = Handler(Looper.getMainLooper())

    //定时器
    private var timer: Timer? = null

    //定时任务
    private var timerTask: TimerTask? = null

    // 全国省市列表
    private val provinces by lazy {
        val moshi = Moshi.Builder().build()
        val listOfCardsType = Types.newParameterizedType(
            List::class.java,
            Province::class.java
        )
        val jsonAdapter = moshi.adapter<List<Province>>(listOfCardsType)
        return@lazy jsonAdapter.fromJson(
            getJsonString(
                context,
                "city.json"
            )
        )
    }

    private val list: MutableList<CityInfoModel> = mutableListOf()

    private val cacheList: MutableList<CityInfoModel> = mutableListOf()

    private val searchList: MutableList<CityInfoModel> = mutableListOf()


    private var mainAdapter: MainAdapter

    init {
        View.inflate(context, R.layout.layout_city_select_view, this)
        viewBinding = LayoutCitySelectViewBinding.bind(this)
        viewBinding.recyclerView.addItemDecoration(
            CustomItemDecoration(
                context,
                object : TitleDecorationCallback {
                    override fun getGroupId(position: Int): String {
                        //这个是用来比较是否是同一组数据的
                        return list[position].sortId
                    }

                    override fun getGroupName(position: Int): String {
                        val (type, _, sortId, sortName) = list.get(position)
                        return if (type == CityInfoModel.TYPE_CURRENT || type == CityInfoModel.TYPE_HOT) {
                            sortName
                        } else sortId.uppercase(Locale.getDefault())
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


        viewBinding.fastIndexView.listener = {
            viewBinding.tvIndex.text = it
            viewBinding.tvIndex.show()
            moveToLetterPosition(it)
            if (timer != null) {
                timer?.cancel()
                timer = null
            }

            if (timerTask != null) {
                timerTask?.cancel()
                timerTask = null
            }
            timerTask = object : TimerTask() {
                override fun run() {
                    mainHandler.post {
                        viewBinding.tvIndex.hide()
                    }
                }
            }
            timer = Timer()
            timer?.schedule(timerTask, 500)

        }

        initData()
    }


    private fun initData() {

    }


    //滚动recyclerview
    private fun moveToLetterPosition(str: String) {
        //这里主要是为了跳转到最顶端
        var letter = str
        if ("#" == letter) {
            letter = "*"
        }
        for (i in list.indices) {
            val (_, _, sortId) = list[i]
            if (sortId.uppercase(Locale.getDefault()) == letter) {
                val lm = viewBinding.recyclerView.layoutManager as LinearLayoutManager
                lm.scrollToPositionWithOffset(i, 0)
                return
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun search(key: String) {
        searchList.clear()
        val isChinese = ChineseHelper.containsChinese(key)
        if (isChinese) {
            for (cityInfoModel in cacheList) {
                var has = true
                HH@ for (c in key.toCharArray()) {
                    if (!cityInfoModel.cityName.contains(c.toString() + "")) {
                        has = false
                        break@HH
                    }
                }
                if (has) {
                    searchList.add(cityInfoModel)
                }
            }
        } else {
            for (cityInfoModel in cacheList) {
                var has = true
                HH@ for (c in key.toCharArray()) {
                    if (!cityInfoModel.cityName.contains(c.toString() + "")) {
                        has = false
                        break@HH
                    }
                }
                if (has) {
                    searchList.add(cityInfoModel)
                }
            }
            list.clear()
            list.addAll(searchList)
            mainAdapter.notifyDataSetChanged()
        }

    }


    /**
     * 读取assets下配置文件
     *
     * @param context 上下文
     * @param fileName 文件名
     * @return 内容
     */
    @Throws(IOException::class)
    private fun getJsonString(context: Context, fileName: String): String {
        var br: BufferedReader? = null
        val sb = StringBuilder()
        try {
            val manager = context.assets
            br = BufferedReader(InputStreamReader(manager.open(fileName)))
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