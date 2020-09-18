package com.example.noteapp.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.noteapp.Login
import com.example.noteapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso


class UserFragment() : Fragment() {

    private var mDatabaseReference:DatabaseReference? = null
    private var mDatabase:FirebaseDatabase? = null
    private var mAuth:FirebaseAuth? = null
    private var fbUser: FirebaseUser? = null
    private var AlbumRef:DatabaseReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_user, container, false)
        val activity = activity

        //Change EditUserFragment
        val btEdit = view.findViewById<View>(R.id.bt_edit) as Button
        btEdit.setOnClickListener {
            val fragment3 = EditUserFragment()
            val fragmentM = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentM.beginTransaction()
            fragmentTransaction.replace(R.id.fram, fragment3)
            fragmentTransaction.disallowAddToBackStack()
            fragmentTransaction.commit()
        }

//        READ PROFILE
        mAuth = FirebaseAuth.getInstance()
        fbUser = mAuth!!.currentUser
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        AlbumRef = mDatabase!!.reference!!.child("Users")

        var imgAvatar = view.findViewById<ImageView>(R.id.imgbt_user_avatar)
        var tvName = view.findViewById<TextView>(R.id.tv_user_name)
        var tvEmail = view.findViewById<TextView>(R.id.tv_user_mail)
        var tvPhone = view.findViewById<TextView>(R.id.tv_user_phone)
        var tvAlbum = view.findViewById<TextView>(R.id.tv_num_album)
        var tvImage = view.findViewById<TextView>(R.id.tv_num_image)

        val mUserReference = mDatabaseReference!!.child(fbUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tvName!!.text = "" + snapshot!!.child("Name").value
                tvEmail!!.text = "" + snapshot!!.child("Email").value
                tvPhone.text = "" + snapshot!!.child("Phone Number").value

                val message: String = "" + snapshot!!.child("The Album").child("User Avatar").value
                Picasso.get().load(message).into(imgAvatar)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        val mAlbumReference = AlbumRef!!.child(fbUser!!.uid).child("The Album")
        mAlbumReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var countAlbums: Int = snapshot.childrenCount.toInt()
                tvAlbum.text = countAlbums.toString()


//                var countImage: Int = 0
//                for (ds in snapshot.children) {
//                    var map : Map<String, Object> = ds.value as (Map<String, Object>)
//                    var ctimage : Object? = map.get("Count Image")
//                    var pValue = ctimage.toString().toInt()
//
//                    countImage += pValue
//
//                    tvImage.text = countImage.toString()
//                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return view
    }

//Log Out
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var btLogOut = view!!.findViewById<Button>(R.id.bt_logOut) as Button
        btLogOut!!.setOnClickListener { alertsignout() }

    }

    companion object {
        @JvmStatic
        fun newInstance() : UserFragment {
            return UserFragment()
        }
    }

    //Set up Logout
    fun alertsignout() {
        val alertDialog1: AlertDialog.Builder = AlertDialog.Builder(activity)
        alertDialog1.setTitle("Confirm SignOut")
        alertDialog1.setMessage("Are you sure you want to SignOut?")

        // Setting Positive "Yes" Btn
        alertDialog1.setPositiveButton("YES", DialogInterface.OnClickListener { dialog, which ->
            FirebaseAuth.getInstance().signOut()
            val i = Intent(activity, Login::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        })

        alertDialog1.setNegativeButton("NO",
            DialogInterface.OnClickListener { dialog, which -> // Write your code here to execute after dialog
                Toast.makeText(activity, "You clicked on NO", Toast.LENGTH_SHORT)
                    .show()
                dialog.cancel()
            })

        // Showing Alert Dialog
        alertDialog1.show()
    }

}




