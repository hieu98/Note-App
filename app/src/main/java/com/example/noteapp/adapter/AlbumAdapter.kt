package com.example.noteapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.noteapp.R
import com.example.noteapp.model.Album
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions


class AlbumAdapter : FirebaseRecyclerAdapter<Album, AlbumAdapter.AlbumViewHolder>
{

    constructor(options: FirebaseRecyclerOptions<Album>) : super(options) {
        }

    inner class AlbumViewHolder(itemview : View): RecyclerView.ViewHolder(itemview){

        internal val img_album = itemview.findViewById(R.id.img_album) as ImageView
        internal val txt_namealbum = itemview.findViewById<TextView>(R.id.txt_namealbum)
        internal val txt_notealbum = itemview.findViewById<TextView>(R.id.txt_notealbum)
        internal val txt_countalbum = itemview.findViewById<TextView>(R.id.txt_countalbum)

//        init {
//            itemview.setOnClickListener {
//                listener.onAlbumItemSelected(albumItemList[adapterPosition])
//            }
//        }
    }

    interface AlbumAdapterListener {
        fun onAlbumItemSelected(album :Album)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val itemview = LayoutInflater.from(parent.context).inflate(R.layout.item_recyclehome,parent,false)
        return AlbumViewHolder(itemview)
    }

    override fun onBindViewHolder(p0: AlbumViewHolder, p1: Int, p2: Album) {
        p0.txt_namealbum.setText(p2.mName)
        p0.txt_notealbum.setText(p2.mNote)
        p0.txt_countalbum.setText(p2.mCount)

        Glide.with(p0.img_album.context)
            .load(p2.mImage)
            .into(p0.img_album)

    }
}

//class AlbumAdapter(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView),
//    View.OnClickListener {
//    var tvNameAB: TextView
//    var tvCountAB: TextView
//    var tvNoteAB: TextView
//    override fun onClick(v: View) {}
//
//    init {
//        tvNameAB = itemView.findViewById<View>(R.id.txt_namealbum) as TextView
//        tvCountAB = itemView.findViewById<View>(R.id.txt_countalbum) as TextView
//        tvNoteAB = itemView.findViewById<View>(R.id.txt_notealbum) as TextView
//    }
//}