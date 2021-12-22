package com.zj.viewtest.partition.util

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import com.zj.views.list.refresh.layout.RefreshLayout
import com.zj.views.ut.DPUtils
import com.zj.nest.NestRecyclerView


class NestLoadMoreRecyclerView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : com.zj.nest.NestRecyclerView(context, attributeSet, def) {
    private val lastPoint = RectF()
    private val minTouchOffset = DPUtils.dp2px(4f)
    private var lastInterceptTouch = true
    private var mayInterrupt = false

//    //Notify loading view when release gesture
//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        if (ev != null) when (ev.action) {
//            MotionEvent.ACTION_DOWN -> {
//                lastInterceptTouch = true
//                lastPoint.set(ev.rawX - minTouchOffset, ev.rawY - minTouchOffset, ev.rawX + minTouchOffset, ev.rawY + minTouchOffset)
//                mayInterrupt = false;mayInterrupt = false
//            }
//            MotionEvent.ACTION_MOVE -> {
//                mayInterrupt = true
//            }
//            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                val contains = !lastPoint.contains(ev.rawX, ev.rawY)
//                lastInterceptTouch = if (mayInterrupt) contains else false
//            }
//        }
//        val b = super.dispatchTouchEvent(ev)
//        if (lastInterceptTouch && (ev?.action == MotionEvent.ACTION_UP || ev?.action == MotionEvent.ACTION_CANCEL)) getNestedChild()?.let {
//            val parent = it.parent
//            if (parent is RefreshLayout) {
//                ev.action = MotionEvent.ACTION_CANCEL
//                post { parent.dispatchTouchEvent(ev) }
//            }
//        }
//        return b
//    }

//    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
//        if ((e?.action == MotionEvent.ACTION_UP || e?.action == MotionEvent.ACTION_CANCEL) && !isNestHeaderFold() && lastInterceptTouch) return true
//        return super.onInterceptTouchEvent(e)
//    }
}