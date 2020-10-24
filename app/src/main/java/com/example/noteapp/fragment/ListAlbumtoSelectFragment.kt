package com.example.noteapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.noteapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ListAlbumtoSelectFragment : BottomSheetDialogFragment(){


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val itemView= inflater.inflate(R.layout.fragment_list_albumto_select, container, false)

        return itemView
    }


}