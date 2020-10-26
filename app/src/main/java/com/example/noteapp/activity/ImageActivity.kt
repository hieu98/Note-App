package com.example.noteapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.noteapp.R
import com.example.noteapp.SuaAnhActivity
import com.squareup.picasso.Picasso

class ImageActivity : AppCompatActivity() {

    private var img_select :ImageView? = null
    private var ln:LinearLayout?= null
    private var btn_fiximage : Button?= null
    private var btn_delimage : Button?= null
    private var btn_infoimage : Button?= null
    private var btn_setava : Button?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        var a =false
        img_select = findViewById(R.id.img_select)
        ln = findViewById(R.id.layout_bot)
        btn_fiximage= findViewById(R.id.btn_fiximage)
        btn_delimage= findViewById(R.id.btn_delimage)
        btn_infoimage= findViewById(R.id.btn_infoimage)
        btn_setava= findViewById(R.id.btn_setava)
        hideLayout(a)
        var intent1 : Intent
        // Nhận link ảnh trong storage
        val url : String? = intent.getStringExtra("url")
        Picasso.get().load(url).into(img_select)

        img_select!!.setOnClickListener {
            if (!a) {
                hideLayout(true)
                a = true
            } else {
                hideLayout(false)
                a = false
            }
        }

        btn_fiximage!!.setOnClickListener {
            // Truyền url sang SuaAnhActivity
            intent1 = Intent(this,SuaAnhActivity::class.java)
            intent1.putExtra("a",false)
            intent1.putExtra("url",url)
            startActivity(intent1)
        }

        btn_delimage!!.setOnClickListener {

        }

        btn_infoimage!!.setOnClickListener {

        }

        btn_setava!!.setOnClickListener {

        }

    }

    private fun hideLayout(a: Boolean){
        if (a)
            ln!!.visibility = View.VISIBLE
        else
            ln!!.visibility =View.INVISIBLE

    }
}