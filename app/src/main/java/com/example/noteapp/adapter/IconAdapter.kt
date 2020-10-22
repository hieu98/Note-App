package com.example.noteapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import io.github.rockerhieu.emojicon.EmojiconTextView

class IconAdapter (private val context: Context, private val iconItemList:List<String>,
                   private val listener: IconAdapterListener): RecyclerView.Adapter<IconAdapter.IconViewHolder>(){

    inner class IconViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        internal var icon_text_view :EmojiconTextView = itemView.findViewById(R.id.icon_text_view) as EmojiconTextView

        init {
            itemView.setOnClickListener {
                listener.onIconItemSelected(iconItemList[adapterPosition])
            }
        }
    }

    interface IconAdapterListener{
        fun onIconItemSelected(icon: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_list_icon,parent,false)
        return IconViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.icon_text_view.setText(iconItemList[position])
    }

    override fun getItemCount(): Int {
        return iconItemList.size
    }

}