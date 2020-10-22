package com.example.noteapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.Interface.IconFragmentListener
import com.example.noteapp.R
import com.example.noteapp.adapter.IconAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ja.burhanrashid52.photoeditor.PhotoEditor


class IconFragment : BottomSheetDialogFragment(),IconAdapter.IconAdapterListener {
    internal var iconRecycler :RecyclerView? = null
    internal var listener : IconFragmentListener? = null

    fun setListener(listener: IconFragmentListener){
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val itemView = inflater.inflate(R.layout.fragment_icon, container, false)
        iconRecycler = itemView.findViewById(R.id.recycler_icon) as RecyclerView

        iconRecycler!!.setHasFixedSize(true)
        iconRecycler!!.layoutManager= GridLayoutManager(activity,5)

        val adapter= IconAdapter(context!!,PhotoEditor.getEmojis(context),this)
        iconRecycler!!.adapter = adapter
        return itemView
    }

    override fun onIconItemSelected(icon: String) {
        listener!!.onIconItemSelected(icon)
    }
    companion object{
        private var instance :IconFragment? = null

        fun getInstance(): IconFragment {
            if (instance == null)
                instance = IconFragment()
            return instance!!
        }
    }

}