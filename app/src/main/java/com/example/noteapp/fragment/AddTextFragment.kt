package com.example.noteapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.Interface.AddTextFragmentListener
import com.example.noteapp.R
import com.example.noteapp.adapter.ColorAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddTextFragment:BottomSheetDialogFragment(),ColorAdapter.ColorAdapterClickListener {
    var colorSelected :Int = android.graphics.Color.parseColor("#000000")

    internal var listener:AddTextFragmentListener? = null
    fun setLintener(listener: AddTextFragmentListener){
        this.listener= listener
    }
    var edit_add_text :EditText? = null
    var recycler_color :RecyclerView?= null
    var btn_done:Button? =null
    var colorAdapter :ColorAdapter? = null

    override fun onColorItemSelected(color: Int) {
        colorSelected = color
    }

    companion object {
        private var instance :AddTextFragment? = null

        fun getInstance(): AddTextFragment {
            if (instance == null)
                instance = AddTextFragment()
            return instance!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val itemview = inflater.inflate(R.layout.fragment_addtext,container,false)

        edit_add_text = itemview.findViewById(R.id.edit_add_text) as EditText
        recycler_color = itemview.findViewById(R.id.recycler_color) as RecyclerView
        btn_done = itemview.findViewById(R.id.btn_done) as Button

        recycler_color!!.setHasFixedSize(true)
        recycler_color!!.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)

        colorAdapter = ColorAdapter(context!!,this)
        recycler_color!!.adapter = colorAdapter

        btn_done!!.setOnClickListener {
            listener!!.onAddTextListener(edit_add_text!!.text.toString(),colorSelected)
            Toast.makeText(context,"Thêm chữ thành công",Toast.LENGTH_SHORT).show()
        }
        return itemview
    }

}