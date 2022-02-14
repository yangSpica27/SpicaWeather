package me.spica.weather.widget.cityselect

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.spica.weather.R


class MainAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var locationListener: () -> Unit = {}


    var itemClickListener: (CityInfoModel) -> Unit = {}


    // 所有城市
    private val data: List<CityInfoModel> = listOf()

    // 热门城市
    private val hotCities: List<CityInfoModel> = listOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder {

        //创建不同的 ViewHolder
        val view: View

        //根据viewtype来创建条目
        when (viewType) {
            CityInfoModel.TYPE_NORMAL -> {
                view = LayoutInflater.from(context).inflate(
                    R.layout.item_layout_normal,
                    parent, false
                )
                return NormalHolder(view)
            }
            CityInfoModel.TYPE_CURRENT -> {
                view = LayoutInflater.from(context).inflate(
                    R.layout.layout_current_city,
                    parent, false
                )
                return CurrentCityHolder(view)
            }
            CityInfoModel.TYPE_HOT -> {
                view = LayoutInflater.from(context).inflate(
                    R.layout.layout_hot_view,
                    parent, false
                )
                return HotCityHolder(view)
            }
            else -> {
                view = LayoutInflater.from(context).inflate(
                    R.layout.item_layout_normal,
                    parent, false
                )
                return NormalHolder(view)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val (type, cityName) = data[position]
        if (type == CityInfoModel.TYPE_NORMAL) {
            val realHolder = holder as NormalHolder
            realHolder.tvContent.text = cityName
            realHolder.tvContent.setOnClickListener {
                itemClickListener(data[position])
            }
        } else if (type == CityInfoModel.TYPE_CURRENT) {
            //当前城市
            val cityHolder = holder as CurrentCityHolder
            cityHolder.tvCurrentCity.text = cityName
            cityHolder.tvRetryLocation.text = context.resources.getString(R.string.str_retry_location)
            cityHolder.tvRetryLocation.setOnClickListener {
                cityHolder.tvRetryLocation.text = context.resources.getString(R.string.str_is_location)
                locationListener()
            }
        } else if (type == CityInfoModel.TYPE_HOT) {
            //热门城市
            if (hotCities.isNotEmpty()) {
                val adapter = HotRecyclerViewAdapter(context, hotCities)
                val hotCityHolder = holder as HotCityHolder
                hotCityHolder.rvHotCity.layoutManager = GridLayoutManager(context, 3)
                adapter.itemClickListener = itemClickListener
                hotCityHolder.rvHotCity.adapter = adapter
            }
        }
    }

    override fun getItemCount(): Int = data.size


    private class CurrentCityHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCurrentCity: AppCompatTextView = itemView.findViewById(R.id.tv_current_city)
        val tvRetryLocation: AppCompatTextView = itemView.findViewById(R.id.tv_current_city)
    }

    private class HotCityHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvHotCity: RecyclerView = itemView.findViewById(R.id.rv_hot)
    }

    private class NormalHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvContent: AppCompatTextView = itemView.findViewById(R.id.tv_city)
    }

}