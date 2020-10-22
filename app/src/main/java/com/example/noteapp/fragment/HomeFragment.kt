package com.example.noteapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class HomeFragment() : Fragment() {
    lateinit var recyclerView :RecyclerView
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
        mDatabaseReference = mDatabase!!.reference!!.child("The Album")
        recyclerView = view.findViewById(R.id.recycler_home)
        recyclerView.hasFixedSize()
//        recyclerView.layoutManager(LinearLayoutManager(this))

//        logRecyclerView()
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    }
    class AlbumViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

    }
//    private fun logRecyclerView(){
//        var FirebaseRecycleAdapater = object : FirebaseRecyclerAdapter<Album, AlbumViewHolder>(
//
//
//
//        ) {
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
//
//            }
//
//            override fun onBindViewHolder(p0: AlbumViewHolder, p1: Int, p2: Album) {
//
//            }
//
//        }
//        recyclerView.adapter = FirebaseRecycleAdapater
//    }
}