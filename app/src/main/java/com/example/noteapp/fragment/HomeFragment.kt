package com.example.noteapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.noteapp.R
import com.example.noteapp.adapter.ImageAdapter
import com.example.noteapp.model.Item
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment :Fragment(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view= inflater.inflate(R.layout.fragment_home, container, false)
        val mAuth = FirebaseAuth.getInstance()
        val fbUser = mAuth!!.currentUser
        val storageRef = FirebaseStorage.getInstance().getReference(fbUser!!.uid).child("imagetotal/")
        val imageList: ArrayList<Item> = ArrayList()
        view.progressBar.visibility = View.VISIBLE

        val listAllTask: Task<ListResult> = storageRef.listAll()
        listAllTask.addOnCompleteListener { result ->
            val items: List<StorageReference> = result.result!!.items
            //add cycle for add image url to list
            items.forEachIndexed { index, item ->
                item.downloadUrl.addOnSuccessListener {
                    imageList.add(Item(it.toString()))
                }.addOnCompleteListener {
                    view.recyclerView.adapter = ImageAdapter(imageList, context!!,)
                    view.recyclerView.layoutManager = GridLayoutManager(context, 3)
                    view.progressBar.visibility = View.GONE
                }
            }
        }
        return view
    }
}