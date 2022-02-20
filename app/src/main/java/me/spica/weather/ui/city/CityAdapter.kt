package me.spica.weather.ui.city

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.spica.weather.databinding.ItemCityBinding
import me.spica.weather.model.city.CityBean

class CityAdapter : RecyclerView.Adapter<CityAdapter.ViewHolder>() {


    val diffUtil = AsyncListDiffer(this,
        object : DiffUtil.ItemCallback<CityBean>() {

            override fun areItemsTheSame(oldItem: CityBean, newItem: CityBean): Boolean = oldItem.cityName == newItem.cityName
            override fun areContentsTheSame(oldItem: CityBean, newItem: CityBean): Boolean = true

        })


    var itemClickListener: (CityBean) -> Unit = {}

    class ViewHolder(val itemCityBinding: ItemCityBinding) :
        RecyclerView.ViewHolder(itemCityBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val itemCityBinding = ItemCityBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemCityBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemCityBinding.tvId.text = diffUtil.currentList[position].sortId
        holder.itemCityBinding.tvCityName.text = diffUtil.currentList[position].cityName
        holder.itemCityBinding.root.setOnClickListener {
            itemClickListener(diffUtil.currentList[position])
        }
    }

    override fun getItemCount(): Int = diffUtil.currentList.size


}