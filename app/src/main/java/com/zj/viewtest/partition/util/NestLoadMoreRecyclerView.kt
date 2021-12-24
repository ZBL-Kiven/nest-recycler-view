package com.zj.viewtest.partition.util

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.zj.views.list.refresh.layout.RefreshLayout

class NestLoadMoreRecyclerView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : com.zj.nest.NestRecyclerView(context, attributeSet, def) {

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
                return false
            }
        }
    }
}