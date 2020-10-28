package com.example.noteapp.fragment

import android.annotation.SuppressLint
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_recyclehome.*
import kotlinx.android.synthetic.main.item_recyclehome.view.*


class HomeFragment() : Fragment(),AlbumAdapter.AlbumAdapterListener {
    lateinit var recycler_home :RecyclerView
    var albumSelect :Album?= null
    var albumList: ArrayList<Album>?= null
    var albumAdapter :AlbumAdapter?= null
    var tenalbum:String? = null

    internal var listener: AlbumListener? = null
    fun setLintener(listener: AlbumListener){
        this.listener= listener
    }
    private lateinit var fibaseFireStore :FirebaseFirestore
    lateinit var mDatabaseFB:DatabaseReference
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase:FirebaseDatabase? = null
    private var mAuth:FirebaseAuth? = null
    private var fbUser: FirebaseUser? = null
    lateinit var option : FirebaseRecyclerOptions<Album>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("The Album")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_home, container, false)
        val activity = activity
        mAuth = FirebaseAuth.getInstance()
        fbUser = mAuth!!.currentUser
        fibaseFireStore = FirebaseFirestore.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child(fbUser!!.uid).child("The Album")
        mDatabaseFB = FirebaseDatabase.getInstance().getReference().child("The Album")
        albumList = ArrayList()

        recycler_home = view.findViewById(R.id.recycler_home)
        recycler_home.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        recycler_home.setHasFixedSize(true)
//        albumAdapter = AlbumAdapter(context!!,albumList!!,this)
//        recycler_home.adapter = albumAdapter
//        albumAdapter!!.notifyDataSetChanged()
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    }


    override fun onAlbumItemSelected(album: Album) {
        albumSelect = album
        tenalbum = albumSelect!!.mName
        img_album.setOnClickListener {
            val intent = Intent(context, ListImageActivity::class.java)
            intent.putExtra("ten album", tenalbum)
            startActivity(intent)
        }
    }


    private fun getDataFromFirebase() {
        val query: Query? = mDatabaseReference

        query!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            override fun onDataChange(snapshot: DataSnapshot) {
//                ClearAll()
                for (ss in snapshot.children) {
                    val al = ss.getValue(Album::class.java)
//                    Log.e("nameab ", al.Name!!)
                    albumList!!.add(al!!)
                    counterImgInAlbum(al.mName!!)
                }
                Log.e("album",albumList.toString())
                val albumAdapter1 = AlbumAdapter(context!!, albumList!!,this@HomeFragment)
                recycler_home.adapter = albumAdapter1
                Log.e("test","ok")
                albumAdapter1.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun ClearAll(){
        if (albumList != null){
            albumList!!.clear()

            if (albumAdapter != null){
                albumAdapter!!.notifyDataSetChanged()
            }
        }


        albumList = ArrayList()
    }

    private fun counterImgInAlbum(album: String) {
//      Gán tên Album = album
        val userId = mAuth!!.currentUser!!.uid
        val mAlbumReference = mDatabaseReference!!.child(userId).child("The Album").child(album)
        mAlbumReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val countImgInAlbums: Int = snapshot.childrenCount.toInt()
//                gán text view hiển thị số ảnh trong album = countImgInAlbums
                txt_countalbum.setText(countImgInAlbums)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

}
