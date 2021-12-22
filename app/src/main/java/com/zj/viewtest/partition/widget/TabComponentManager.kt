package com.zj.viewtest.partition.widget

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.zj.cf.managers.TabFragmentManager
import com.zj.viewtest.partition.services.beans.ComponentInfo

class TabComponentManager(act: FragmentActivity, c: ViewPager2, i: Int, tab: TabLayout, private val pageTitle: String?, private val cInfo: ComponentInfo?) : TabFragmentManager<TabComponentFragment.TabTypeInfo, TabComponentFragment>(act, c, i, tab, *TabComponentFragment.TabTypeInfo.values()) {

    fun clear(softClear: Boolean) {
        getFragments()?.forEach { it.clear(softClear) }
        if (!softClear) super.clear()
    }

    override fun tabConfigurationStrategy(tab: TabLayout.Tab, position: Int) {
        tab.setText(getCurData()[position].d.pageNameId)
    }

    override fun onCreateFragment(d: TabComponentFragment.TabTypeInfo, p: Int): TabComponentFragment {
        val t = TabComponentFragment()
        t.setData(d, cInfo, pageTitle)
        return t
    }
}