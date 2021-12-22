package com.zj.viewtest.partition.component

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zj.views.list.refresh.layout.RefreshLayout
import com.zj.viewtest.partition.services.beans.ComponentInfo
import com.zj.viewtest.R
import com.zj.viewtest.partition.services.api.PartitionApi
import com.zj.viewtest.partition.services.beans.ComponentContentInfo
import com.zj.nest.NestRecyclerView
import com.zj.viewtest.partition.widget.TabComponentFragment

/**
 * Author: ZJJ
 */
@SuppressLint("ViewConstructor")
class InfinityComponent(context: Context, override val type: Int) : BaseListVideoComponent(context), NestRecyclerView.NestScrollerIn {

    private lateinit var tvComponentTitle: TextView
    private lateinit var rvContent: RecyclerView
    private var rl: RefreshLayout? = null
    private var pageType: TabComponentFragment.TabTypeInfo? = null
    private var lastId: Int = -1
    private var page: Int = 0

    override fun getSkeletonCount(contentType: Int): Int {
        return if (contentType == ComponentInfo.CONTENT_TYPE_ORDINARY) 10 else 12
    }

    override val layoutId: Int; get() = R.layout.component_infinity

    override fun getComponentData() {
        getData(false)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Log.e("------ ", "dispatchTouchEvent  ===> ${ev?.action}")
        return super.dispatchTouchEvent(ev)
    }

    fun setPageTypeInfo(pageType: TabComponentFragment.TabTypeInfo?) {
        this.pageType = pageType
    }

    private fun getData(isLoadMore: Boolean) {
        if (!isLoadMore) rl?.setNoMoreData(false)
        val c = cInfo ?: return
        PartitionApi.getComponentListContentInfo(c.id, lastId, pageType?.type ?: 0, page, getSkeletonCount(c.contentType)) { isSuccess, data, _ ->
            if (isSuccess && data.isNullOrEmpty()) {
                rl?.setNoMoreData(true)
                return@getComponentListContentInfo
            } else if (!data.isNullOrEmpty()) {
                page++
                setComponentData(data, isLoadMore)
                rl?.finishLoadMore()
            } else {
                rl?.finishLoadMore(false)
            }
        }
    }

    override fun getBehaviorScrollingView(): RecyclerView {
        return rvContent
    }

    override fun initView() {
        super.initView()
        page = 0
        lastId = -1
        tvComponentTitle = findViewById(R.id.component_infinity_title)
        rvContent = findViewById(R.id.component_infinity_rv_container)
        rl = findViewById(R.id.component_infinity_rl)
        rl?.setEnableLoadMore(false)
        rl?.setEnableRefresh(false)
        rl?.setOnLoadMoreListener {
            getData(true)
        }
    }

    override fun initData() {
        super.initData()
        val s = cInfo?.name
        tvComponentTitle.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
        tvComponentTitle.text = s
    }

    override fun onSetData(data: List<ComponentContentInfo?>?, pl: Any?) {
        rl?.setEnableLoadMore(true)
        super.onSetData(data, pl)
    }

    override fun onDestroy(soft: Boolean) {
        super.onDestroy(soft)
        if (!soft) {
            page = 0
            lastId = -1
        } else {
            rvContent
        }
    }

    override fun getInnerView(): View {
        return rvContent
    }
}