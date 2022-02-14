package me.spica.weather.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qweather.sdk.bean.IndicesBean
import me.spica.weather.databinding.ItemTipsBinding

class TipAdapter : RecyclerView.Adapter<TipAdapter.ViewHolder>() {


    val items: MutableList<IndicesBean.DailyBean?> = mutableListOf()


    class ViewHolder(val itemTipsBinding: ItemTipsBinding) :
        RecyclerView.ViewHolder(itemTipsBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val itemTipsBinding = ItemTipsBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemTipsBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemTipsBinding.tvName.text = item?.name
        holder.itemTipsBinding.tvValue.text = item?.category
    }

    override fun getItemCount(): Int = items.size
}