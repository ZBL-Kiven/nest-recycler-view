package com.zj.viewtest.partition.widget

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zj.cf.fragments.BaseTabFragment
import com.zj.viewtest.R
import com.zj.viewtest.partition.services.beans.ComponentInfo
import com.zj.viewtest.partition.component.InfinityComponent
import com.zj.nest.NestRecyclerView
import java.util.*

@SuppressLint("MissingSuperCall")
class TabComponentFragment : BaseTabFragment(), com.zj.nest.NestRecyclerView.NestScrollerIn {

    var pageType: TabTypeInfo? = null
    private var pageTitle: String? = ""
    private var cInfo: ComponentInfo? = null

    @Suppress("unused")
    enum class TabTypeInfo(val pageNameId: Int, val type: Int) {
        TRENDING(R.string.trending, 0), MOST_VIEW(R.string.most_viewed, 1), NEW(R.string.newest, 2);

        fun getLowerCaseName(): String {
            return name.lowercase(Locale.ENGLISH)
        }
    }

    override fun getView(inflater: LayoutInflater, container: ViewGroup?): View {
        val v = InfinityComponent(requireContext(), 3)
        v.pageTitle = pageTitle
        v.setPageTypeInfo(pageType)
        v.setData(cInfo)
        return v
    }

    fun setData(pageType: TabTypeInfo, cInfo: ComponentInfo?, pageTitle: String?) {
        this.cInfo = cInfo
        this.pageType = pageType
        this.pageTitle = pageTitle
    }

    override fun getInnerView(): View? {
        return (rootView as? InfinityComponent)?.getInnerView()
    }

    fun clear(softClear: Boolean) {
        (rootView as? InfinityComponent)?.onDestroy(softClear)
    }
}