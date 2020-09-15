package com.example.noteapp.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.noteapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_edit_profile.*

class EditUserFragment : Fragment() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private var fbUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        val activity = activity
        val processBar = view.findViewById<ProgressBar>(R.id.progress_save)

        val btSave = view.findViewById<View>(R.id.bt_save) as Button
        btSave.setOnClickListener {
            val visibility = if (processBar!!.visibility == View.GONE) View.VISIBLE else View.GONE
            processBar.visibility = visibility
            val fragment2 = UserFragment()
            val fragmentM = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentM.beginTransaction()
            fragmentTransaction.replace(R.id.fram, fragment2)
            fragmentTransaction.disallowAddToBackStack()
            fragmentTransaction.commit()
            processBar.visibility = visibility
        }

        mAuth = FirebaseAuth.getInstance()
        fbUser = mAuth!!.currentUser
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")

//        var imgAvatar = view.findViewById<ImageView>(R.id.edt_user_avatar)
        var tvName = view.findViewById<TextView>(R.id.edt_user_name)
        var tvEmail = view.findViewById<TextView>(R.id.edt_user_mail)
        var tvPhone = view.findViewById<TextView>(R.id.edit_user_phone)

        val mUserReference = mDatabaseReference!!.child(fbUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                    tvName!!.text = "" + snapshot!!.child("Name").value
                    tvEmail!!.text = "" + snapshot!!.child("Email").value
                    tvPhone.text = "" + snapshot!!.child("Phone Number").value
            }

            override fun onCancelled(error: DatabaseError) {}
        })
            return view
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var edtName = view!!.findViewById<TextView>(R.id.edt_user_name)
        edtName!!.setOnClickListener(View.OnClickListener {
            alertEditName("Name")

        })

        edit_user_phone!!.setOnClickListener(View.OnClickListener {
            alertEditPhone("Phone Number")
        })


    }

    private fun alertEditName(key: String) {
        val alertDialog2 = AlertDialog.Builder(activity)
        alertDialog2.setTitle("Update $key")

        val linearLayout = LinearLayout(activity)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(10, 10, 10, 10)

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
                        Toast.makeText(activity, "Updated...", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, "Update Failed ", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(activity, "Please enter $key", Toast.LENGTH_SHORT).show()
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
        linearLayout.setPadding(10, 10, 10, 10)

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
                        Toast.makeText(activity, "Updated...", Toast.LENGTH_SHORT).show()
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



