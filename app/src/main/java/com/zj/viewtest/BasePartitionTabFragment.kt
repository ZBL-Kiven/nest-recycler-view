package com.zj.viewtest

import android.annotation.SuppressLint
import com.zj.cf.fragments.BaseTabFragment
import com.zj.viewtest.partition.services.beans.ChannelInfo
import com.zj.viewtest.partition.util.NestLoadMoreRecyclerView

abstract class BasePartitionTabFragment : BaseTabFragment() {

    private var channelInfo: ChannelInfo? = null
    private var pendingScrollEvent: Int = 0

    abstract fun getNestRecyclerView(): NestLoadMoreRecyclerView?

    abstract fun initData(channelInfo: ChannelInfo?)

    fun getData(): ChannelInfo? {
        return channelInfo
    }

    fun nestIsExpand(): Boolean {
        return getNestRecyclerView()?.canScrollVertically(-1) == false
    }

    fun setData(channelInfo: ChannelInfo, totalScroll: Int) {
        this.channelInfo = channelInfo
        this.pendingScrollEvent = totalScroll
        if (isResumed) onSetData()
    }

    @SuppressLint("MissingSuperCall")
    override fun onStarted() {
        super.onStarted()
        onSetData()
    }

    private fun onSetData() {
        initData(channelInfo)
    }
}