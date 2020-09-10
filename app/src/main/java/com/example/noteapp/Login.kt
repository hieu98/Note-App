package com.example.noteapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private var inputEmail : EditText?=null
    private var inputPassword :EditText ?=null
    private var btnLogIn:Button?=null
    private var btnLogUp:Button?=null
    private var tvForgotPwd:TextView?=null

    private var fbAuth: FirebaseAuth?=null

    protected override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        hideSystemUI()
        setContentView(R.layout.login_activity)

//        if (fbAuth!!.currentUser != null){
//            startActivity(Intent(this@Login, MainActivity::class.java))
//            finish()
//        }
        setContentView(R.layout.login_activity)
        inputEmail = findViewById(R.id.edt_mail_login) as EditText
        inputPassword = findViewById(R.id.edt_pwd_login) as EditText
        btnLogIn = findViewById(R.id.bt_logIn) as Button
        btnLogUp = findViewById(R.id.bt_logUp) as Button
        tvForgotPwd = findViewById(R.id.tv_forgot_pwd) as TextView
        val processBar = findViewById<ProgressBar>(R.id.progress_circle)

        fbAuth = FirebaseAuth.getInstance()

        tvForgotPwd!!.setOnClickListener(View.OnClickListener {
            val visibility = if (processBar!!.visibility == View.GONE) View.VISIBLE else View.GONE
            processBar.visibility = visibility
            sendPasswordResetEmail()
        })

        btnLogUp!!.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@Login, SignUp::class.java))
        })

        btnLogIn!!.setOnClickListener(View.OnClickListener {
            val email = inputEmail!!.text.toString().trim()
            val password = inputPassword!!.text.toString().trim()

            val visibility = if (processBar!!.visibility == View.GONE) View.VISIBLE else View.GONE

            if(TextUtils.isEmpty(email)){
                Toast.makeText(applicationContext,"Please Enter your email!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(password)){
                Toast.makeText(applicationContext,"Please Enter your password!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            processBar.visibility = visibility

            fbAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, OnCompleteListener {
                    task ->

                    if (!task.isSuccessful){
                        if (password.length < 6){
                            inputPassword!!.setError(getString(R.string.minium_password))
                        }else{
                            Toast.makeText(this@Login, getString(R.string.auth_failed), Toast.LENGTH_LONG).show()
                            processBar.visibility = visibility
                        }
                    }else{
                        val intent = Intent(this@Login, MainActivity::class.java)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                })
        })
    }

// Giữ nguyên trạng thái đăng nhập cho các lần sử dụng sau
//    override fun onStart() {
//        super.onStart()
//        if (fbAuth?.currentUser == null){
//            startActivity(Intent(this@Login, Login::class.java))
//        }else{
//            startActivity(Intent(this@Login, MainActivity::class.java))
//        }
//    }
    private fun sendPasswordResetEmail(){
        val email = inputEmail?.text.toString()
        val processBar = findViewById<ProgressBar>(R.id.progress_circle)
        val visibility = if (processBar!!.visibility == View.GONE) View.VISIBLE else View.GONE

        if (!TextUtils.isEmpty(email)){
            fbAuth!!.sendPasswordResetEmail(email)
                .addOnCompleteListener{task ->
                    if (task.isSuccessful){
                        val message = "Email sent."
                        Log.d("TAG", message)
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@Login, Login::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        processBar.visibility = visibility
                    }else{
                        task.exception!!.message?.let { Log.w("TAG", it) }
                        Toast.makeText(this, "User is not exist", Toast.LENGTH_SHORT).show()
                        processBar.visibility = visibility
                    }
                }
        }else{
            Toast.makeText(this, "Please Enter your email!", Toast.LENGTH_SHORT).show()
            processBar.visibility = visibility
        }
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

}

