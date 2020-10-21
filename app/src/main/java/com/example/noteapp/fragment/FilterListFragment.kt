package com.example.noteapp.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.Interface.FilterListFragmentListener
import com.example.noteapp.R
import com.example.noteapp.SuaAnhActivity
import com.example.noteapp.Utils.BitmapUtils
import com.example.noteapp.Utils.SpaceItemDecoration
import com.example.noteapp.adapater.ThumbnailAdapter
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager


class FilterListFragment : Fragment(), FilterListFragmentListener {
    private var listener : FilterListFragmentListener? = null
    private lateinit var adapter: ThumbnailAdapter
    private lateinit var thumbnailItemList: MutableList<ThumbnailItem>
    internal lateinit var recycler_view : RecyclerView

    companion object{
        internal var instance : FilterListFragment? = null
        internal var bitmap:Bitmap? = null
        fun getInstance(bitmapsave: Bitmap?): FilterListFragment{
            bitmap = bitmapsave
            if (instance == null)
                instance = FilterListFragment()
            return instance!!
        }
    }


    fun setListener(listener: FilterListFragmentListener){
        this.listener= listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_filter_list, container, false)

        thumbnailItemList = ArrayList()
        adapter = ThumbnailAdapter(activity!!,thumbnailItemList,this)
        recycler_view = view.findViewById<RecyclerView>(R.id.recycler_view)

        recycler_view.layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        recycler_view.itemAnimator = DefaultItemAnimator()
        val space = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8f,resources.displayMetrics).toInt()
        recycler_view.addItemDecoration(SpaceItemDecoration(space))
        recycler_view.adapter = adapter

        displayImage(bitmap)
        return view
    }

    fun displayImage(bitmap: Bitmap?) {
        val r = Runnable {
            val thumbImage : Bitmap?

            if (bitmap == null)
                thumbImage = BitmapUtils.getBitmapFromAssets(activity!!,
                    SuaAnhActivity.Main.IMAGE_NAME,100,100)
            else
                thumbImage = Bitmap.createScaledBitmap(bitmap,100,100,false)

            if (thumbImage == null)
                return@Runnable

            ThumbnailsManager.clearThumbs()
            thumbnailItemList.clear()

            val thumbnailItem = ThumbnailItem()
            thumbnailItem.image = thumbImage
            thumbnailItem.filterName ="Normal"
            ThumbnailsManager.addThumb(thumbnailItem)

            val filters = FilterPack.getFilterPack(activity!!)
            for (filter in filters){
                val item = ThumbnailItem()
                item.image = thumbImage
                item.filterName = filter.name
                item.filter= filter
                ThumbnailsManager.addThumb(item)
            }
            thumbnailItemList.addAll(ThumbnailsManager.processThumbs(activity))
            activity!!.runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        }
        Thread(r).start()
    }

    override fun onFilterSelected(filter: Filter) {
        if(listener != null){
            listener?.onFilterSelected(filter)
        }
    }
}