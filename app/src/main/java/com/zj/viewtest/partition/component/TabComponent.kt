package com.zj.viewtest.partition.component

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import com.zj.viewtest.partition.base.BaseComponent
import com.google.android.material.tabs.TabLayout
import com.zj.viewtest.R
import com.zj.nest.NestRecyclerView
import com.zj.viewtest.partition.widget.TabComponentManager
import kotlin.math.absoluteValue
import kotlin.math.sign

@SuppressLint("ViewConstructor")
class TabComponent(context: Context, override val type: Int) : BaseComponent<Any?>(context), com.zj.nest.NestRecyclerView.NestScrollerIn {

    private var tabPagerManager: TabComponentManager? = null
    private lateinit var vp2: ViewPager2
    private lateinit var tab: TabLayout

    override val layoutId: Int; get() = R.layout.component_tab

    override fun initView() {
        vp2 = findViewById(R.id.component_tab_vp2)
        tab = findViewById(R.id.component_tab)
    }

    override fun showSkeleton() {}

    override fun initData() {
        setComponentData(null)
    }

    override fun onSetData(data: Any?, pl: Any?) {
        val fa = (context as? FragmentActivity) ?: return
        if (fa.isFinishing) return
        tabPagerManager = TabComponentManager(fa, vp2, 0, tab, pageTitle, cInfo)
    }

    /**
     * -------- Solve the Vp2 nested sliding conflict in the same direction --------
     * */
    private var touchSlop = 5
    private var initialX = 0f
    private var initialY = 0f

    private fun canChildScroll(orientation: Int, delta: Float): Boolean {
        val direction = -delta.sign.toInt()
        return when (orientation) {
            LinearLayoutManager.HORIZONTAL -> vp2.canScrollHorizontally(direction)
            LinearLayoutManager.VERTICAL -> vp2.canScrollVertically(direction)
            else -> throw IllegalArgumentException()
        }
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        handleInterceptTouchEvent(e)
        return super.onInterceptTouchEvent(e)
    }

    private fun handleInterceptTouchEvent(e: MotionEvent) {
        val orientation = vp2.orientation // Early return if child can't scroll in same direction as parent
        if (!canChildScroll(orientation, -1f) && !canChildScroll(orientation, 1f)) {
            return
        }
        if (e.action == MotionEvent.ACTION_DOWN) {
            initialX = e.x
            initialY = e.y
            parent.requestDisallowInterceptTouchEvent(true)
        } else if (e.action == MotionEvent.ACTION_MOVE) {
            val dx = e.x - initialX
            val dy = e.y - initialY
            val isVpHorizontal = orientation == ORIENTATION_HORIZONTAL
            val scaledDx = dx.absoluteValue * if (isVpHorizontal) .5f else 1f
            val scaledDy = dy.absoluteValue * if (isVpHorizontal) 1f else .5f
            if (scaledDx > touchSlop || scaledDy > touchSlop) {
                if (isVpHorizontal == (scaledDy > scaledDx)) {
                    parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    if (canChildScroll(orientation, if (isVpHorizontal) dx else dy)) {
                        parent.requestDisallowInterceptTouchEvent(true)
                    } else {
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
            }
        }
    }

    override fun getInnerView(): View? {
        return tabPagerManager?.getCurrentFragment()?.getInnerView()
    }

    override fun onDestroy(soft: Boolean) {
        super.onDestroy(soft)
        tabPagerManager?.clear(soft)
    }
}