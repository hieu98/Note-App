package com.example.noteapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.Interface.AlbumListener
import com.example.noteapp.R
import com.example.noteapp.activity.ListImageActivity
import com.example.noteapp.adapter.AlbumAdapter
import com.example.noteapp.model.Album
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_recyclehome.*


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

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase:FirebaseDatabase? = null
    private var mAuth:FirebaseAuth? = null
    private var fbUser: FirebaseUser? = null

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
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("The Album")
//        albumAdapter = AlbumAdapter(context!!,albumList!!,this)
//        recycler_home.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        recycler_home = view.findViewById(R.id.recycler_home)
        recycler_home.setHasFixedSize(true)

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    }

    fun getAlbumList(){
        
    }

    override fun onAlbumItemSelected(album: Album) {
        albumSelect = album
        tenalbum = albumSelect!!.nameab
        img_album.setOnClickListener {
            val intent = Intent(context, ListImageActivity::class.java)
            intent.putExtra("ten album",tenalbum)
            startActivity(intent)
        }
    }

}