package com.example.noteapp.Utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager


class NonSwipeableViewPage : ViewPager {
    constructor(context: Context ):super(context){
        setMyScoller()
    }

    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet){
        setMyScoller()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }



    private fun setMyScoller() {
        try {
            val viewPager = ViewPager::class.java
            val scroller = viewPager.getDeclaredField("mScroll")
            scroller.isAccessible =true
            scroller.set(this,MyScroller(context))
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}

class MyScroller(context: Context?):Scroller(context,DecelerateInterpolator()) {
    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy, 400)
    }
}
