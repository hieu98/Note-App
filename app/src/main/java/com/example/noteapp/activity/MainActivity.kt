package com.example.noteapp.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.noteapp.R
import com.example.noteapp.SuaAnhActivity
import com.example.noteapp.fragment.HomeFragment
import com.example.noteapp.fragment.UserFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceNavigationView
import com.luseen.spacenavigation.SpaceOnClickListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    val fragment1: Fragment = HomeFragment()
    val fragment2: Fragment = UserFragment()
    var active = fragment1

    private var firebaseStorage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var databaseReference: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var fbUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        mAuth = FirebaseAuth.getInstance()
        fbUser = mAuth!!.currentUser
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference(fbUser!!.uid)
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        supportActionBar?.hide()
        val nav: SpaceNavigationView = space
        var a = false
        hidebutton(a)
        btn1.setOnClickListener {
            val intent = Intent(this, SuaAnhActivity::class.java)
            hidebutton(false)
            intent.putExtra("a", true)
            startActivity(intent)
        }
        btn2.setOnClickListener {
            alertCreateAlbumName("Name")
        }
        nav.initWithSaveInstanceState(savedInstanceState)
        nav.addSpaceItem(SpaceItem("HOME", R.drawable.ic_baseline_home_24))
        nav.addSpaceItem(SpaceItem("USER", R.drawable.ic_baseline_person_24))
        supportFragmentManager.beginTransaction().add(R.id.fram, fragment2, "2").commit()
        supportFragmentManager.beginTransaction().add(R.id.fram, fragment1, "1").commit()
        supportFragmentManager.beginTransaction().hide(fragment2).commit()
        supportFragmentManager.beginTransaction().hide(active).show(fragment1).commit()
        val setava = intent.getBooleanExtra("setava",false)
        if (setava){
            supportFragmentManager.beginTransaction().hide(active).show(fragment2).detach(fragment2).attach(fragment2).commit()
            active = fragment2
            hidebutton(false)
            a = false
        }
        nav.setSpaceOnClickListener(object : SpaceOnClickListener {
            override fun onCentreButtonClick() {
                nav.setCentreButtonSelectable(true)
                if (!a) {
                    hidebutton(true)
                    a = true
                } else {
                    hidebutton(false)
                    a = false
                }
            }

            override fun onItemClick(itemIndex: Int, itemName: String) {
                if (itemName == "HOME") {
                    supportFragmentManager.beginTransaction().hide(active).show(fragment1).commit()
                    active = fragment1
                    hidebutton(false)
                    a = false
                } else {
                    supportFragmentManager.beginTransaction().hide(active).show(fragment2).detach(fragment2).attach(fragment2).commit()
                    active = fragment2
                    hidebutton(false)
                    a = false
                }
            }


            override fun onItemReselected(itemIndex: Int, itemName: String) {
//                Toast.makeText(this@MainActivity, "$itemIndex $itemName", Toast.LENGTH_SHORT).show()
//                if (itemName == "HOME") {
//                    supportFragmentManager.beginTransaction().hide(active).show(fragment1).commit()
//                    active = fragment1
//                } else {
//                    supportFragmentManager.beginTransaction().hide(active).show(fragment2).commit()
//                    active = fragment2
//                }
            }
        })
    }

    private fun hidebutton(a: Boolean) {
        if (a){
            btn1.visibility= View.VISIBLE
            btn2.visibility= View.VISIBLE
        }else {
            btn1.visibility = View.INVISIBLE
            btn2.visibility = View.INVISIBLE
        }
    }

    private fun alertCreateAlbumName(key: String) {

        val alertDialog2 = AlertDialog.Builder(this)
        alertDialog2.setTitle("Create New Album")

        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(50, 10, 10, 10)

        val editText1 = EditText(this)
        editText1.hint = "Write Album $key"

        linearLayout.addView(editText1)
        alertDialog2.setView(linearLayout)
        alertDialog2.setPositiveButton("Create") { dialog, which ->
            val value = editText1.text.toString().trim { it <= ' ' }
            val result = java.util.HashMap<String, Any>()
            result[key] = value
            val userId = mAuth!!.currentUser!!.uid
            val currentUserDb = databaseReference!!.child(userId).child("The Album").child(value)
            currentUserDb!!.updateChildren(result)
                .addOnSuccessListener {
                    Toast.makeText(this, "Created New Album $key", Toast.LENGTH_SHORT).show()
                    alertCreateAlbumNote("Note", value)


                }
                .addOnFailureListener {
                    Toast.makeText(this, "Created $key Failed ", Toast.LENGTH_SHORT).show()
                }
        }

        alertDialog2.setNegativeButton("Cancel") { dialog, which ->
            Toast.makeText(this, "You clicked on Cancel", Toast.LENGTH_SHORT)
                .show()
            dialog.cancel()
        }
        alertDialog2.create().show()
    }
    private fun alertCreateAlbumNote(key: String, name: String) {

        val alertDialog2 = AlertDialog.Builder(this)
        alertDialog2.setTitle("Create New Album")

        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(50, 10, 10, 10)

        val editText1 = EditText(this)
        editText1.hint = "Write something about your album"

        linearLayout.addView(editText1)
        alertDialog2.setView(linearLayout)
        alertDialog2.setPositiveButton("Create") { dialog, which ->
            val value = editText1.text.toString().trim { it <= ' ' }
            val result = java.util.HashMap<String, Any>()
            result[key] = value
            val userId = mAuth!!.currentUser!!.uid
            val currentUserDb = databaseReference!!.child(userId).child("The Album")
            currentUserDb!!.child(name).updateChildren(result)
                .addOnSuccessListener {
                    Toast.makeText(this, "Created New Album $key", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Created $key Failed ", Toast.LENGTH_SHORT).show()
                }
        }

        alertDialog2.setNegativeButton("Cancel") { dialog, which ->
            Toast.makeText(this, "You clicked on Cancel", Toast.LENGTH_SHORT)
                .show()
            dialog.cancel()
        }
        alertDialog2.create().show()
    }
}