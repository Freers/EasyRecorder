package com.lxt.easyrecorder.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * @author lxt <lxt352></lxt352>@gmail.com>
 * @since 2017/11/30.
 */

class RecyclerViewAdapter<T> : RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder<*>>() {

    private val data: List<T>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<*>? {
        return null
    }

    override fun onBindViewHolder(holder: ItemViewHolder<*>, position: Int) {

    }

    override fun getItemCount(): Int {
        return 0
    }

    class ItemViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(t: T) {

        }
    }
}
