package com.example.noteapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R

class ColorAdapter(internal var context: Context, internal var listener:ColorAdapterClickListener):
    RecyclerView.Adapter<ColorAdapter.ColorViewHolder>(){

    internal var colorList:List<Int>
    init {
        this.colorList = genColorList()
    }

    interface ColorAdapterClickListener {
        fun onColorItemSelected(color :Int)
    }

    private fun genColorList() :List<Int>{
        val colorList = ArrayList<Int>()
        colorList.add(Color.parseColor("#696969"))
        colorList.add(Color.parseColor("#7fe5f0"))
        colorList.add(Color.parseColor("#ff0000"))
        colorList.add(Color.parseColor("#ff80ed"))
        colorList.add(Color.parseColor("#407294"))
        colorList.add(Color.parseColor("#cbcba9"))
        colorList.add(Color.parseColor("#065535"))
        colorList.add(Color.parseColor("#420420"))
        colorList.add(Color.parseColor("#f7347a"))
        colorList.add(Color.parseColor("#ffe4e1"))
        colorList.add(Color.parseColor("#00ffff"))
        colorList.add(Color.parseColor("#ffd700"))
        colorList.add(Color.parseColor("#0000ff"))
        colorList.add(Color.parseColor("#800000"))
        colorList.add(Color.parseColor("#00ff00"))
        colorList.add(Color.parseColor("#ffa500"))
        colorList.add(Color.parseColor("#ffffff"))
        colorList.add(Color.parseColor("#000000"))
        colorList.add(Color.parseColor("#8a2be2"))
        colorList.add(Color.parseColor("#ffb6c1"))
        colorList.add(Color.parseColor("#800080"))
        return colorList
    }

    inner class ColorViewHolder(itemView :View) :RecyclerView.ViewHolder(itemView) {
        internal var color_section :CardView
        init {
            color_section = itemView.findViewById(R.id.color_section) as CardView

            itemView.setOnClickListener{
                listener.onColorItemSelected(colorList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_color,parent,false)
        return ColorViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.color_section.setCardBackgroundColor(colorList[position])
    }

    override fun getItemCount(): Int {
        return colorList.size
    }
}