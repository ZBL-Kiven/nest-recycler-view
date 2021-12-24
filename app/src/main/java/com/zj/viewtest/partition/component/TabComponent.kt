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
class TabComponent(context: Context, override val type: Int) : BaseComponent<Any?>(context), NestRecyclerView.NestScrollerIn {

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

    override fun getInnerView(): View? {
        return tabPagerManager?.getCurrentFragment()?.getInnerView()
    }

    override fun onDestroy(soft: Boolean) {
        super.onDestroy(soft)
        tabPagerManager?.clear(soft)
    }
}