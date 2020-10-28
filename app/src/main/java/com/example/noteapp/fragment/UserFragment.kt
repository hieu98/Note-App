package com.example.noteapp.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.noteapp.R
import com.example.noteapp.activity.ListImageActivity
import com.example.noteapp.activity.Login
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso


class UserFragment() : Fragment() {
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private var fbUser: FirebaseUser? = null
    private var AlbumRef: DatabaseReference? = null
    private var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_user, container, false)
        val activity = activity

//        READ PROFILE
        mAuth = FirebaseAuth.getInstance()
        fbUser = mAuth!!.currentUser
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        AlbumRef = mDatabase!!.reference.child("Users")
        storageReference = FirebaseStorage.getInstance().getReference(fbUser!!.uid)

        val imgAvatar = view.findViewById<ImageView>(R.id.imgbt_user_avatar)
        val tvName = view.findViewById<TextView>(R.id.tv_user_name)
        val tvEmail = view.findViewById<TextView>(R.id.tv_user_mail)
        val tvPhone = view.findViewById<TextView>(R.id.tv_user_phone)

        val mUserReference = mDatabaseReference!!.child(fbUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tvName!!.text = "" + snapshot!!.child("Name").value
                tvEmail!!.text = "" + snapshot!!.child("Email").value
                tvPhone.text = "" + snapshot!!.child("Phone Number").value

                val message: String = "" + snapshot.child("The Album").child("User Avatar").value
                Log.e("message",message)
                Picasso.get().load(message).into(imgAvatar)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        countTotalAlbum()

//          Đếm tổng số ảnh có trong storage
        countTotalImage()

        return view
    }

    //Log Out
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btLogOut = view.findViewById<Button>(R.id.bt_logOut) as Button
        btLogOut.setOnClickListener { alertsignout() }

        val edtName = view.findViewById<TextView>(R.id.tv_user_name)
        edtName!!.setOnClickListener(View.OnClickListener {
            alertEditName("Name")
        })
        val edtPhone = view.findViewById<TextView>(R.id.tv_user_phone)
        edtPhone!!.setOnClickListener(View.OnClickListener {
            alertEditPhone("Phone Number")
        })

        val viewImage = view.findViewById<TextView>(R.id.tv_num_image)
        viewImage.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, ListImageActivity::class.java))
        })

        val editAvatar:ImageView = view.findViewById(R.id.imgbt_user_avatar)
        editAvatar.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, ListImageActivity::class.java))
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(): UserFragment {
            return UserFragment()
        }
    }

    fun countTotalImage(){
        val fileRef = storageReference?.child("imagetotal/")
        if (fileRef != null) {
            fileRef.listAll().addOnSuccessListener(OnSuccessListener<ListResult> { listResult ->
                for (item in listResult.items) {
                    val countofimages = listResult.items.size
                    var tvImage = view!!.findViewById<TextView>(R.id.tv_num_image)
                    tvImage.text = countofimages.toString()
                }
            })
        }
    }

    fun countTotalAlbum(){
        val mAlbumReference = AlbumRef!!.child(fbUser!!.uid).child("The Album")
        mAlbumReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val countAlbums: Int = snapshot.childrenCount.toInt()
                val tvAlbum = view!!.findViewById<TextView>(R.id.tv_num_album)
                tvAlbum.text = countAlbums.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
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

    private fun alertEditName(key: String) {
        val alertDialog2 = AlertDialog.Builder(activity)
        alertDialog2.setTitle("Update $key")

        val linearLayout = LinearLayout(activity)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(50, 10, 10, 10)

        val editText = EditText(activity)
        editText.hint = "Enter New $key"
        editText.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)

        linearLayout.addView(editText)
        alertDialog2.setView(linearLayout)
        alertDialog2.setPositiveButton("Update") { dialog, which ->
            val value = editText.text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(value)) {
                val result = java.util.HashMap<String, Any>()
                result[key] = value
                mDatabaseReference!!.child(fbUser!!.uid).updateChildren(result)
                    .addOnSuccessListener {
                        Toast.makeText(activity, "Updated New Name", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, "Update Failed ", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(activity, "Please Enter $key", Toast.LENGTH_SHORT).show()
            }
        }

        alertDialog2.setNegativeButton("Cancel") { dialog, which ->
            Toast.makeText(activity, "You clicked on Cancel", Toast.LENGTH_SHORT)
                .show()
            dialog.cancel()
        }
        alertDialog2.create().show()
    }

    private fun alertEditPhone(key: String) {
        val alertDialog2 = AlertDialog.Builder(activity)
        alertDialog2.setTitle("Update $key")

        val linearLayout = LinearLayout(activity)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(50, 10, 10, 10)

        val editText = EditText(activity)
        editText.hint = "Enter New $key"
        editText.inputType = InputType.TYPE_CLASS_PHONE
        editText.filters = arrayOf(InputFilter.LengthFilter(10))

        linearLayout.addView(editText)
        alertDialog2.setView(linearLayout)
        alertDialog2.setPositiveButton("Update") { dialog, which ->
            val value = editText.text.toString().trim { it <= ' ' }
            val result = java.util.HashMap<String, Any>()
            result[key] = value
            mDatabaseReference!!.child(fbUser!!.uid).updateChildren(result)
                .addOnSuccessListener {
                    Toast.makeText(activity, "Updated New Phone Number", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Update Failed ", Toast.LENGTH_SHORT).show()
                }
        }

        alertDialog2.setNegativeButton("Cancel") { dialog, which ->
            Toast.makeText(activity, "You clicked on Cancel", Toast.LENGTH_SHORT)
                .show()
            dialog.cancel()
        }
        alertDialog2.create().show()
    }

}