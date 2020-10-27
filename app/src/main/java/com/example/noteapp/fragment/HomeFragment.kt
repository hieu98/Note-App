package com.example.noteapp.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.with
import com.example.noteapp.Interface.AlbumListener
import com.example.noteapp.R
import com.example.noteapp.activity.ListImageActivity
import com.example.noteapp.adapter.AlbumAdapter
import com.example.noteapp.model.Album
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_recyclehome.*
import kotlinx.android.synthetic.main.item_recyclehome.view.*


class HomeFragment() : Fragment() {

    lateinit var recyclerView: RecyclerView
//    var recyclerAdapter: FirebaseRecyclerAdapter<Album, AlbumAdapter>? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var albumSelect: Album? = null
    var albumList: ArrayList<Album>? = null
    var albumAdapter: AlbumAdapter? = null
    var tenalbum: String? = null


    internal var listener: AlbumListener? = null
    fun setLintener(listener: AlbumListener) {
        this.listener = listener
    }

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private var fbUser: FirebaseUser? = null
    private var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("The Album")
    }

//    private fun ClearAll(){
//        if (albumList != null){
//            albumList!!.clear()
//
//            if (albumAdapter != null){
//                albumAdapter!!.notifyDataSetChanged()
//            }
//        }
//
//
//        albumList = ArrayList()
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_home, container, false)
        val activity = activity

        mAuth = FirebaseAuth.getInstance()
        fbUser = mAuth!!.currentUser
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child(fbUser!!.uid).child("The Album")

        recyclerView = view!!.findViewById(R.id.recycler_home)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.VERTICAL,
            false
        )
        val options: FirebaseRecyclerOptions<Album> = FirebaseRecyclerOptions.Builder<Album>()
            .setQuery(
                FirebaseDatabase.getInstance().reference.child(fbUser!!.uid).child("The Album"),
                Album::class.java
            )
            .build()

        albumAdapter = AlbumAdapter(options)
        recyclerView.adapter = albumAdapter

        return view
    }

    override fun onStart() {
        super.onStart()
        albumAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        albumAdapter!!.stopListening()
    }

    companion object {
        @JvmStatic
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    //    private fun getDataFromFirebase() {
//        var query:Query = mDatabaseReference!!
//
//        query.addValueEventListener(object : ValueEventListener {
//            @SuppressLint("RestrictedApi")
//            override fun onDataChange(snapshot: DataSnapshot) {
//                ClearAll()
//                for (ss in snapshot.children) {
//                    var album: Album = Album(
//                        ss.child("name").value.toString(),
//                        ss.child("note").value.toString(),
//                        ss.child(
//                            "image"
//                        ).value.toString()
//                    )
////                    if (album != null){
//
////                        album.Nameab(ss.child("name").value.toString())
////                        album.setNote(ss.child("note").value.toString())
////                        album.setImage(ss.child("image").value.toString())
////                    }
////                    album.setNameab(ss.child("name").value.toString())
////                    album.setNote(ss.child("note").value.toString())
////                    album.setImage(ss.child("note").value.toString())
//
//                    albumList!!.add(album)
//                }
//                albumAdapter = AlbumAdapter(getApplicationContext(), albumList!!)
//                recyclerView.adapter = albumAdapter
//                albumAdapter!!.notifyDataSetChanged()
//
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
//
//
//
//    }
//    open fun loadData() {
//        val options: FirebaseRecyclerOptions<*> = FirebaseRecyclerOptions.Builder<Album>()
//            .setQuery(mDatabaseReference!!, Album::class.java)
//            .build()
//        recyclerAdapter = object :
//            FirebaseRecyclerAdapter<Album, AlbumAdapter>(options as FirebaseRecyclerOptions<Album>) {
//
//            override fun onBindViewHolder(p0: AlbumAdapter, p1: Int, p2: Album) {
//                p0.tvNameAB.setText(p2.Name)
//                p0.tvNoteAB.setText(p2.Note)
//                p0.tvCountAB.setText(p2.Count)
//            }
//
//            override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, i: Int): AlbumAdapter {
//                val view: View = LayoutInflater.from(viewGroup.context)
//                    .inflate(R.layout.item_recyclehome, viewGroup, false)
//                return AlbumAdapter(view)
//            }
//        }
//        (recyclerAdapter as FirebaseRecyclerAdapter<Album, AlbumAdapter>).notifyDataSetChanged()
//        (recyclerAdapter as FirebaseRecyclerAdapter<Album, AlbumAdapter>).startListening()
//        recyclerView.adapter = recyclerAdapter
//    }

    @NonNull


    private fun counterAlbum() {

        val userId = mAuth!!.currentUser!!.uid
        val mAlbumReference = mDatabaseReference!!.child(userId).child("The Album")
        mAlbumReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val countAlbums: Int = snapshot.childrenCount.toInt()
//              gán text view hiển thị số album = countAlbums
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun counterImgInAlbum(album: String) {
//      Gán tên Album = album
        val userId = mAuth!!.currentUser!!.uid
        val mAlbumReference = mDatabaseReference!!.child(userId).child("The Album").child(album)
        mAlbumReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val countImgInAlbums: Int = snapshot.childrenCount.toInt()
//                gán text view hiển thị số ảnh trong album = countImgInAlbums
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun totalImage() {
        val userId = mAuth!!.currentUser!!.uid
        val fileRef = storageReference?.child(userId)
        fileRef!!.listAll().addOnSuccessListener(OnSuccessListener<ListResult> { listResult ->
            for (item in listResult.items) {
                val countofimages = listResult.items.size
//              Gán text view hiển thị số lượng = countofimages
            }
        })

    }

    fun onAlbumItemSelected(album: Album) {
        albumSelect = album
        tenalbum = albumSelect!!.mName
        img_album.setOnClickListener {
            val intent = Intent(context, ListImageActivity::class.java)
            intent.putExtra("ten album", tenalbum)
            startActivity(intent)
        }
    }

}

