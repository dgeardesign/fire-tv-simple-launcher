package com.dgeardesign.firetvlauncher

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TileAdapter(
    private val iconFor: (LauncherItem.App) -> Drawable?,
    private val onClick: (LauncherItem) -> Unit,
    private val onLongClick: (LauncherItem) -> Unit
) : RecyclerView.Adapter<TileAdapter.Holder>() {

    var items: List<LauncherItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.icon)
        val label: TextView = view.findViewById(R.id.label)
        val badge: TextView = view.findViewById(R.id.badge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_tile, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        holder.label.text = item.label
        when (item) {
            is LauncherItem.App -> {
                holder.itemView.setBackgroundResource(R.drawable.tile_app)
                holder.icon.setImageDrawable(iconFor(item))
                holder.badge.visibility = View.GONE
            }
            is LauncherItem.Website -> {
                holder.itemView.setBackgroundResource(R.drawable.tile_link)
                holder.icon.setImageResource(android.R.drawable.ic_menu_mapmode)
                holder.badge.visibility = View.VISIBLE
            }
        }
        holder.itemView.setOnClickListener { onClick(item) }
        holder.itemView.setOnLongClickListener { onLongClick(item); true }
    }
}
