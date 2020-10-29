package com.example.noteapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.example.noteapp.activity.ImageActivity
import com.example.noteapp.model.Item
import com.squareup.picasso.Picasso

class SetAvaAdapter(private var items: List<Item>, private val context: Context) :
    RecyclerView.Adapter<SetAvaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item,
                parent,
                false
            )
        )
    }
    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)

        init {
            view.setOnClickListener {
                val intent : Intent = Intent(context, ImageActivity::class.java)
                intent.putExtra("image_url", items[position].imageUrl)
                intent.putExtra("setava",true)
                context.startActivity(intent)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
//        val image = image[position]
        Picasso.get().load(item.imageUrl).into(holder.imageView)
    }

}