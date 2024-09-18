package com.example.personalassetmanagement.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.personalassetmanagement.R
import com.example.personalassetmanagement.data.ItemWithType
import java.text.SimpleDateFormat
import java.util.*

class ItemAdapter : PagingDataAdapter<ItemWithType, ItemAdapter.ItemViewHolder>(ITEM_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemWithType = getItem(position)
        if (itemWithType != null) {
            holder.bind(itemWithType)
        }
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.itemName)
        private val typeTextView: TextView = itemView.findViewById(R.id.itemType)
        private val dateTextView: TextView = itemView.findViewById(R.id.itemDate)

        fun bind(itemWithType: ItemWithType) {
            val item = itemWithType.item
            nameTextView.text = item.name
            typeTextView.text = "Type: ${itemWithType.typeName}"
            dateTextView.text = "Added: ${formatDate(item.dateAdded)}"
        }

        private fun formatDate(date: Date): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return formatter.format(date)
        }
    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<ItemWithType>() {
            override fun areItemsTheSame(oldItem: ItemWithType, newItem: ItemWithType): Boolean {
                return oldItem.item.id == newItem.item.id
            }

            override fun areContentsTheSame(oldItem: ItemWithType, newItem: ItemWithType): Boolean {
                return oldItem == newItem
            }
        }
    }
}