package me.spica.weather.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import me.spica.weather.databinding.ItemTipsBinding
import me.spica.weather.model.weather.LifeIndexBean

class TipAdapter : RecyclerView.Adapter<TipAdapter.ViewHolder>() {

    // 数据
    val items: MutableList<LifeIndexBean> = mutableListOf()

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
            holder.itemTipsBinding.tvName.text = item.name
            holder.itemTipsBinding.tvDesc.text = item.category
            holder.itemTipsBinding.ivIcon.load(item.iconRes)
        }
    }

    override fun getItemCount(): Int = items.size
}
