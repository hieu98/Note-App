package com.example.noteapp

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.santalu.maskedittext.MaskEditText
import kotlinx.android.synthetic.main.signup_activity.*

class SignUp : AppCompatActivity() {
    private var inputEmail : EditText?=null
    private var inputPassword : EditText?=null
    private var inputName : EditText?=null
    private var btnSignIn: Button?=null
    private var btnSignUp: Button?=null

    private var fbAuth: FirebaseAuth?=null
    private var mRef : DatabaseReference?=null
    private var mDatabase: FirebaseDatabase?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)

        fbAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mRef = mDatabase!!.reference!!.child("Users")

        btnSignIn = findViewById(R.id.bt_signIn) as Button
        btnSignUp = findViewById(R.id.bt_signUp) as Button
        inputEmail = findViewById(R.id.edt_mail_signUp) as EditText
        inputPassword = findViewById(R.id.edt_pwd_signUp) as EditText
        inputName = findViewById(R.id.edt_name_signUp) as EditText

        val mprogressBar = findViewById(R.id.progress_signUp) as ProgressBar



        btnSignIn!!.setOnClickListener(View.OnClickListener {
            val visibility = if (mprogressBar!!.visibility == View.GONE) View.VISIBLE else View.GONE
            mprogressBar.visibility = visibility
            finish()
        })

        btnSignUp!!.setOnClickListener(View.OnClickListener {
            val email = inputEmail!!.text.toString().trim()
            val password = inputPassword!!.text.toString().trim()
            val name = inputName!!.text.toString().trim()
            val phone = edt_phone_signUp.text.toString()

            val visibility = if (mprogressBar!!.visibility == View.GONE) View.VISIBLE else View.GONE
            mprogressBar.visibility = visibility

            if (TextUtils.isEmpty(email)){
                Toast.makeText(applicationContext, "Enter your email!", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(password)){
                Toast.makeText(applicationContext, "Enter your password!", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            if (password.length < 6){
                Toast.makeText(applicationContext, "Password too short!Please enter more than 6 characters!", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(name)){
                Toast.makeText(applicationContext, "Enter your name!", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
//            if (TextUtils.isEmpty(phone)){
//                Toast.makeText(applicationContext, "Enter your phone number!", Toast.LENGTH_LONG).show()
//                return@OnClickListener
//            }

            fbAuth!!.createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) {task ->
                    if (task.isSuccessful){
                        Toast.makeText(applicationContext, "Create New Account Successfully!Welcome!", Toast.LENGTH_LONG).show()
                        mprogressBar.visibility = visibility
                        val userId = fbAuth!!.currentUser!!.uid
                        val currentUserDb = mRef!!.child(userId)
                        currentUserDb.child("Name").setValue(name)
                        currentUserDb.child("Phone Number").setValue(phone)
                        updateUserInfoAndUI()
                    }else{
                        Log.w("TAG", "createNewAccount:failure", task.exception)
                        Toast.makeText(applicationContext, "Create New Account Failed! Please try again", Toast.LENGTH_LONG).show()
                        mprogressBar.visibility = visibility
                    }
                }

        })
    }
    private fun updateUserInfoAndUI(){
        val intent = Intent(this@SignUp, MainActivity::class.java)
        intent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TOP))
        startActivity(intent)
    }

    //Xác thực email có tồn tại hay không
    private fun verifyEmail(){
        val mUser = fbAuth!!.currentUser;
        mUser!!.sendEmailVerification()
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful){
                    Toast.makeText(this@SignUp, "Verification email sent to " + mUser.email, Toast.LENGTH_SHORT).show()

                }else{
                    Log.e("TAG", "sendEmailVerification", task.exception)
                    Toast.makeText(this@SignUp, "Failed to send verification email!", Toast.LENGTH_SHORT).show()
                }
            }
    }

}