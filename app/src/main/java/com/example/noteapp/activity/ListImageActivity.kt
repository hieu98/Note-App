package com.example.noteapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.noteapp.R
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.noteapp.adapter.ImageAdapter
import com.example.noteapp.model.Image
import com.example.noteapp.model.Item
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_list_image.*

class ListImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_image)

        val mAuth = FirebaseAuth.getInstance()
        val fbUser = mAuth!!.currentUser
        val storageRef = FirebaseStorage.getInstance().getReference(fbUser!!.uid).child("imagetotal/")
        val imageList: ArrayList<Item> = ArrayList()
        progressBar.visibility = View.VISIBLE

        val listAllTask: Task<ListResult> = storageRef.listAll()
        listAllTask.addOnCompleteListener { result ->
            val items: List<StorageReference> = result.result!!.items
            //add cycle for add image url to list
            items.forEachIndexed { index, item ->
                item.downloadUrl.addOnSuccessListener {
                    imageList.add(Item(it.toString()))
                }.addOnCompleteListener {
                    recyclerView.adapter = ImageAdapter(imageList, this,)
                    recyclerView.layoutManager = GridLayoutManager(this, 3)
                    progressBar.visibility = View.GONE
                }
            }
        }

    }
}