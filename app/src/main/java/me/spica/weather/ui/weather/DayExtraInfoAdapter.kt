package me.spica.weather.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import coil.load
import me.spica.weather.databinding.ItemTipsBinding

class DayExtraInfoAdapter : RecyclerView.Adapter<DayExtraInfoAdapter.ViewHolder>() {

    // 数据
    val items: MutableList<ShowData> = mutableListOf()

    class ViewHolder(val itemTipsBinding: ItemTipsBinding) :
        RecyclerView.ViewHolder(itemTipsBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
        ViewHolder {
        val itemTipsBinding = ItemTipsBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemTipsBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items[position].let { item ->
            holder.itemTipsBinding.tvName.text = item.title
            holder.itemTipsBinding.tvDesc.text = item.value
            holder.itemTipsBinding.ivIcon.load(item.iconRes)
        }
    }

    override fun getItemCount(): Int = items.size

    data class ShowData(
        val title: String,
        val value: String,
        @DrawableRes val iconRes: Int
    )
}
