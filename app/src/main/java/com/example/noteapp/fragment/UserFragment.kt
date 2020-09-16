package com.example.noteapp.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ContextThemeWrapper
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
import kotlinx.android.synthetic.main.fragment_user.tv_num_album
import kotlinx.android.synthetic.main.fragment_user.tv_user_mail
import kotlinx.android.synthetic.main.fragment_user.tv_user_name
import kotlinx.android.synthetic.main.fragment_user.tv_user_phone
import java.util.zip.Inflater


class UserFragment() : Fragment() {

    private var mDatabaseReference:DatabaseReference? = null
    private var mDatabase:FirebaseDatabase? = null
    private var mAuth:FirebaseAuth? = null
    private var fbUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        val contextThemeWrapper : Context = ContextThemeWrapper(activity, R.style.UserFragment)
//        val localInflater : LayoutInflater = inflater.cloneInContext(contextThemeWrapper)
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

        var imgAvatar = view.findViewById<ImageView>(R.id.imgbt_user_avatar)
        var tvName = view.findViewById<TextView>(R.id.tv_user_name)
        var tvEmail = view.findViewById<TextView>(R.id.tv_user_mail)
        var tvPhone = view.findViewById<TextView>(R.id.tv_user_phone)

        val mUserReference = mDatabaseReference!!.child(fbUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tvName!!.text = "" + snapshot!!.child("Name").value
                tvEmail!!.text = "" + snapshot!!.child("Email").value
                tvPhone.text = "" + snapshot!!.child("Phone Number").value

                val message:String = "" + snapshot!!.child("The Album").child("User Avatar").value
                Picasso.get().load(message).into(imgAvatar)
            }

            override fun onCancelled(error: DatabaseError) {}
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




