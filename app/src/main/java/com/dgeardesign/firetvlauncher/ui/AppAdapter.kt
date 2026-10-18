package com.dgeardesign.firetvlauncher.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dgeardesign.firetvlauncher.R
import com.dgeardesign.firetvlauncher.databinding.ItemAppBinding
import com.dgeardesign.firetvlauncher.models.LauncherItem

/**
 * Binds apps and website shortcuts to grid tiles. Tiles are focusable and clickable, so the D-pad
 * moves focus between them and select triggers onClick; long-press triggers onLongClick.
 * Website tiles use a distinct background and a LINK badge.
 */
class AppAdapter(
    private val onClick: (LauncherItem) -> Unit,
    private val onLongClick: (LauncherItem) -> Unit
) : RecyclerView.Adapter<AppAdapter.Holder>() {

    private var items: List<LauncherItem> = emptyList()

    fun submit(newItems: List<LauncherItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder(ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        val binding = holder.binding
        binding.name.text = item.label
        when (item) {
            is LauncherItem.App -> {
                binding.icon.setImageDrawable(item.info.icon)
                binding.root.setBackgroundResource(R.drawable.tile_background)
                binding.badge.visibility = View.GONE
            }
            is LauncherItem.Link -> {
                binding.icon.setImageResource(android.R.drawable.ic_menu_mapmode)
                binding.root.setBackgroundResource(R.drawable.tile_link_background)
                binding.badge.visibility = View.VISIBLE
            }
        }
        binding.root.setOnClickListener { onClick(item) }
        binding.root.setOnLongClickListener { onLongClick(item); true }
    }

    override fun getItemCount() = items.size

    class Holder(val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root)
}
