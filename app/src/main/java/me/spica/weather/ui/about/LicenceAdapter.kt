package me.spica.weather.ui.about

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.spica.weather.databinding.ItemLicensesBinding

class LicenceAdapter : RecyclerView.Adapter<LicenceAdapter.ViewHolder>() {

    val items = mutableListOf<Licence>()

    class ViewHolder(val itemBinding: ItemLicensesBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemLicensesBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemBinding.tvName.text = items[position].name
        holder.itemBinding.address.text = items[position].address
    }

    override fun getItemCount(): Int = items.size
}
