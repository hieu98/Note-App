package com.example.noteapp.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.Interface.AlbumListener
import com.example.noteapp.R
import com.example.noteapp.activity.ListImageActivity
import com.example.noteapp.adapter.AlbumAdapter
import com.example.noteapp.model.Album
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.item_recyclehome.*


class HomeFragment() : Fragment(),AlbumAdapter.AlbumAdapterListener {
    lateinit var recyclerView :RecyclerView
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
    private var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("The Album")


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
        mDatabaseReference = mDatabase!!.reference!!.child(fbUser!!.uid).child("The Album")

        recyclerView = view!!.findViewById<RecyclerView>(R.id.recycler_home)
        recyclerView.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.setHasFixedSize(true)
        albumList = ArrayList()

        ClearAll()

        getDataFromFirebase()

//        albumAdapter = AlbumAdapter(context!!,albumList!!,this)
//        recycler_home.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
//        recycler_home = view.findViewById(R.id.recycler_home)
//        recycler_home.setHasFixedSize(true)

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    }

    private fun getDataFromFirebase() {
        var query:Query = mDatabaseReference!!

        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            override fun onDataChange(snapshot: DataSnapshot) {
                ClearAll()
                for (ss in snapshot.children) {
                    var album: Album = Album(ss.child("name").value.toString(), ss.child("note").value.toString(), ss.child("image").value.toString())
//                    if (album != null){

//                        album.Nameab(ss.child("name").value.toString())
//                        album.setNote(ss.child("note").value.toString())
//                        album.setImage(ss.child("image").value.toString())
//                    }
//                    album.setNameab(ss.child("name").value.toString())
//                    album.setNote(ss.child("note").value.toString())
//                    album.setImage(ss.child("note").value.toString())

                    albumList!!.add(album)
                }
                albumAdapter = AlbumAdapter(getApplicationContext(), albumList!!)
                recyclerView.adapter = albumAdapter
                albumAdapter!!.notifyDataSetChanged()


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })



    }

    private  fun counterAlbum() {

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

    override fun onAlbumItemSelected(album: Album) {
        albumSelect = album
        tenalbum = albumSelect!!.Nameab
        img_album.setOnClickListener {
            val intent = Intent(context, ListImageActivity::class.java)
            intent.putExtra("ten album", tenalbum)
            startActivity(intent)
        }
    }

}