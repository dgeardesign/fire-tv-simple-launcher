package com.dgeardesign.firetvlauncher.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dgeardesign.firetvlauncher.databinding.ItemAppBinding
import com.dgeardesign.firetvlauncher.models.AppInfo

/**
 * Binds apps to grid tiles. Tiles are focusable and clickable, so the D-pad
 * moves focus between them and the center/select button triggers onClick.
 */
class AppAdapter(private val onClick: (AppInfo) -> Unit) :
    RecyclerView.Adapter<AppAdapter.Holder>() {

    private var items: List<AppInfo> = emptyList()

    fun submit(newItems: List<AppInfo>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder(ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val app = items[position]
        holder.binding.icon.setImageDrawable(app.icon)
        holder.binding.name.text = app.label
        holder.binding.root.setOnClickListener { onClick(app) }
    }

    override fun getItemCount() = items.size

    class Holder(val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root)
}
