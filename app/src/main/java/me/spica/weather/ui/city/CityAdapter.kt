package me.spica.weather.ui.city

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.spica.weather.databinding.ItemCityBinding
import me.spica.weather.model.city.CityBean

class CityAdapter : RecyclerView.Adapter<CityAdapter.ViewHolder>() {


    val diffUtil = AsyncListDiffer<CityBean>(this,
        object : DiffUtil.ItemCallback<CityBean>() {

            override fun areItemsTheSame(oldItem: CityBean, newItem: CityBean): Boolean = oldItem.cityName == newItem.cityName
            override fun areContentsTheSame(oldItem: CityBean, newItem: CityBean): Boolean = true

        })

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
    }

    override fun getItemCount(): Int = diffUtil.currentList.size


    class CityDiffDUtils
        (
        private val newList: List<CityBean>,
        private val oldList: List<CityBean>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int):
                Boolean = newList[newItemPosition].cityName ==
                oldList[oldItemPosition].cityName

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int):
                Boolean = true
    }

}