package com.lxt.easyrecorder.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.lxt.easyrecorder.R
import com.lxt.easyrecorder.demain.Video
import kotlinx.android.synthetic.main.item_local_video.view.*

/**
 * @author lxt <lxt352></lxt352>@gmail.com>
 * @since 2017/11/30.
 */
class RecyclerViewAdapter<Video>(private val context: Context, list: List<Video>, private val itemView: View) :
        RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder>() {

    private val data: List<Video>? = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder? =
            ItemViewHolder(context, itemView)

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val get = data?.get(position) as com.lxt.easyrecorder.demain.Video
        holder.bind(get)
    }

    override fun getItemCount(): Int = data?.size!!

    class ItemViewHolder(context: Context, itemView: View = View.inflate(context,
            R.layout.item_local_video, null)) : RecyclerView.ViewHolder(itemView) {

        fun bind(video: Video) {
            itemView.imageView
            itemView.title
        }
    }
}
