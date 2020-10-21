package com.example.noteapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.Interface.FilterListFragmentListener
import com.example.noteapp.R
import com.zomato.photofilters.utils.ThumbnailItem
import kotlinx.android.synthetic.main.item_list_filter.view.*

class ThumbnailAdapter(private val context: Context, private val thumbnailItemList:List<ThumbnailItem>,
                       private val listener:FilterListFragmentListener):
    RecyclerView.Adapter<ThumbnailAdapter.MyViewholder>() {
    private var selectedIndex = 0

    class MyViewholder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var thumbNail:ImageView = itemView.thumbnail
        var filterName:TextView = itemView.filter_name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_list_filter,parent,false)
        return MyViewholder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        val thumbNailItem = thumbnailItemList[position]
        holder.thumbNail.setImageBitmap(thumbNailItem.image)
        holder.thumbNail.setOnClickListener{
            listener.onFilterSelected(thumbNailItem.filter)
            selectedIndex = position
            notifyDataSetChanged()
        }

        holder.filterName.text = thumbNailItem.filterName
        if (selectedIndex == position)
            holder.filterName.setTextColor(ContextCompat.getColor(context,R.color.filter_label_selected))
        else
            holder.filterName.setTextColor(ContextCompat.getColor(context,R.color.filter_label_normal))

    }

    override fun getItemCount(): Int {
        return thumbnailItemList.size
    }
}