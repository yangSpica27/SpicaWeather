package me.spica.weather.widget.cityselect

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import me.spica.weather.R

@Suppress("unused")
class HotRecyclerViewAdapter(private val context: Context,
                             private val data: List<CityInfoModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var itemClickListener: (CityInfoModel) -> Unit = {}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder {
        return NormalHolder(
            LayoutInflater.from(context)
                .inflate(
                    R.layout.item_layout_hot_city,
                    parent, false
                )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val (_, cityName) = data[position]
        val realHolder = holder as NormalHolder
        realHolder.tvCity.text = cityName
        realHolder.tvCity.setOnClickListener {
            itemClickListener(data[position])
        }
    }


    override fun getItemCount(): Int = data.size


    private inner class NormalHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCity: AppCompatTextView = itemView.findViewById(R.id.tv_city)
    }

}