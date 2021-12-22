package com.zj.viewtest.partition.util

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import com.zj.views.list.refresh.layout.RefreshLayout
import com.zj.views.ut.DPUtils


class NestLoadMoreRecyclerView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : com.zj.nest.NestRecyclerView(context, attributeSet, def) {
    private val lastPoint = RectF()
    private val minTouchOffset = DPUtils.dp2px(4f)
    private var lastInterceptTouch = true
    private var mayInterrupt = false


    override fun unConsumedEvent(ne: NestUnConsumedEvent, firstChange: Boolean, event: MotionEvent?): Boolean {
        return when (ne) {
            NestUnConsumedEvent.FOCUS -> {
                getNestedChild()?.let {
                    val parent = it.parent
                    if (parent is RefreshLayout) {
                        if (firstChange) {
                            val v1 = MotionEvent.obtainNoHistory(event)
                            v1.action = MotionEvent.ACTION_CANCEL
                            val v2 = MotionEvent.obtainNoHistory(event)
                            v2.action = MotionEvent.ACTION_DOWN
                            parent.dispatchTouchEvent(v1)
                            parent.dispatchTouchEvent(v2)
                            v1.recycle()
                            v2.recycle()
                        }
                        parent.dispatchTouchEvent(event)
                        return true
                    }
                }
                false
            }
            NestUnConsumedEvent.CLICK -> {
                false
            }

            NestUnConsumedEvent.PASS -> {
                if (event?.action == MotionEvent.ACTION_UP || event?.action == MotionEvent.ACTION_CANCEL) {
                    getNestedChild()?.let {
                        val parent = it.parent
                        if (parent is RefreshLayout) {
                            event.action = MotionEvent.ACTION_CANCEL
                            post { parent.dispatchTouchEvent(event) }
                        }
                    }
                }

                //                if (event != null) when (event.action) {
                //                    MotionEvent.ACTION_DOWN -> {
                //                        lastInterceptTouch = true
                //                        lastPoint.set(event.rawX - minTouchOffset, event.rawY - minTouchOffset, event.rawX + minTouchOffset, event.rawY + minTouchOffset)
                //                        mayInterrupt = false;mayInterrupt = false
                //                    }
                //                    MotionEvent.ACTION_MOVE -> {
                //                        mayInterrupt = true
                //                    }
                //                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                //                        val contains = !lastPoint.contains(event.rawX, event.rawY)
                //                        lastInterceptTouch = if (mayInterrupt) contains else false
                //                    }
                //                }
                //                if (lastInterceptTouch && (event?.action == MotionEvent.ACTION_UP || event?.action == MotionEvent.ACTION_CANCEL)) getNestedChild()?.let {
                //                    val parent = it.parent
                //                    if (parent is RefreshLayout) {
                //                        event.action = MotionEvent.ACTION_CANCEL
                //                        post { parent.dispatchTouchEvent(event) }
                //                    }
                //                }

                //                if(event.action)getNestedChild()?.let {
                //                    val parent = it.parent
                //                    if (parent is RefreshLayout) {
                //                        event?.action = MotionEvent.ACTION_CANCEL
                //                        post { parent.dispatchTouchEvent(event) }
                //                    }
                //                }

                return false
            }
        }
    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        if ((e?.action == MotionEvent.ACTION_UP || e?.action == MotionEvent.ACTION_CANCEL) && !isNestHeaderFold() && lastInterceptTouch) return true
        return super.onInterceptTouchEvent(e)
    }
}