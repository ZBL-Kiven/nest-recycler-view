package com.zj.viewtest.partition.channel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zj.viewtest.BasePartitionTabFragment
import com.zj.viewtest.R
import com.zj.viewtest.partition.base.ComponentDelegate
import com.zj.viewtest.partition.services.api.PartitionApi
import com.zj.viewtest.partition.services.beans.ChannelInfo
import com.zj.viewtest.partition.widget.NestRecyclerAdapter

open class ChannelTabFragment : BasePartitionTabFragment() {

    private var nestRecyclerView: com.zj.nest.NestRecyclerView? = null

    override fun getNestRecyclerView(): com.zj.nest.NestRecyclerView? {
        return nestRecyclerView
    }

    override fun initData(channelInfo: ChannelInfo?) {
        val nrv: com.zj.nest.NestRecyclerView = find(R.id.component_channel_tab_nest_rv) ?: return
        nestRecyclerView = nrv
        channelInfo?.let { ci ->
            PartitionApi.getComponentByChannelId(ci.channelId) { isSuccess, data, _ ->
                if (isSuccess && !data.isNullOrEmpty()) {
                    val grouped = data.filterNotNull().groupBy { ComponentDelegate.isInfinityComponent(it.componentType) }
                    val normalComponents = grouped[false]
                    val nestedAble = grouped[true]?.firstOrNull()
                    val nestAdapter = NestRecyclerAdapter.create(nrv)
                    nestRecyclerView?.adapter = nestAdapter
                    nestAdapter.add(normalComponents)
                    nestAdapter.add(nestedAble)
                }
            }
        }
    }

    override fun getView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.component_channel_tab_item, container, true)
    }
}