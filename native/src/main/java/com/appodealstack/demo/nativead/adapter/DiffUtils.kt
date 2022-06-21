package com.appodealstack.demo.nativead.adapter

import androidx.recyclerview.widget.DiffUtil

internal class DiffUtils : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem) =
        oldItem.getItemId() == newItem.getItemId()

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem) =
        oldItem.hashCode() == newItem.hashCode()
}