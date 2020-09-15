package com.example.noteapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.noteapp.fragment.Communicator
import com.example.noteapp.fragment.EditUserFragment
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceNavigationView
import com.luseen.spacenavigation.SpaceOnClickListener
import kotlinx.android.synthetic.main.activity_main.*
import com.example.noteapp.fragment.UserFragment
import com.example.noteapp.fragment.HomeFragment
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    val fragment1: Fragment = HomeFragment()
    val fragment2: Fragment = UserFragment()
    var active = fragment1

    private var fbAuth: FirebaseAuth?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val nav : SpaceNavigationView = space
        var a= false
        hidebutton(a)
        nav.initWithSaveInstanceState(savedInstanceState)
        nav.addSpaceItem(SpaceItem("HOME", R.drawable.ic_baseline_home_24))
        nav.addSpaceItem(SpaceItem("USER", R.drawable.ic_baseline_person_24))
        supportFragmentManager.beginTransaction().hide(UserFragment()).commit()
        supportFragmentManager.beginTransaction().add(R.id.fram, fragment2, "2").commit()
        supportFragmentManager.beginTransaction().add(R.id.fram, fragment1, "1").commit()

        nav.setSpaceOnClickListener(object : SpaceOnClickListener {
            override fun onCentreButtonClick() {
//                Toast.makeText(this@MainActivity, "onCentreButtonClick", Toast.LENGTH_SHORT).show()
                nav.setCentreButtonSelectable(true)
                if (a == false) {
                    hidebutton(true)
                    a = true
                } else {
                    hidebutton(false)
                    a = false
                }
            }

            override fun onItemClick(itemIndex: Int, itemName: String) {
//                Toast.makeText(this@MainActivity, "$itemIndex $itemName", Toast.LENGTH_SHORT).show()
                if (itemName == "HOME") {
                    supportFragmentManager.beginTransaction().hide(active).show(fragment1).commit()
                    active = fragment1
                } else {
                    supportFragmentManager.beginTransaction().hide(active).show(fragment2).commit()
                    active = fragment2
                }

            }

            override fun onItemReselected(itemIndex: Int, itemName: String) {
//                Toast.makeText(this@MainActivity, "$itemIndex $itemName", Toast.LENGTH_SHORT).show()

                if (itemName == "HOME") {
                    supportFragmentManager.beginTransaction().hide(active).show(fragment1).commit()
                    active = fragment1
                } else {
                    supportFragmentManager.beginTransaction().hide(active).show(fragment2).commit()
                    active = fragment2
                }

            }
        })

    }


    fun hidebutton(a: Boolean) {
        if (a==true){
            btn1.visibility= View.VISIBLE
            btn2.visibility= View.VISIBLE

        }else {
            btn1.visibility = View.INVISIBLE
            btn2.visibility = View.INVISIBLE

        }
    }

}