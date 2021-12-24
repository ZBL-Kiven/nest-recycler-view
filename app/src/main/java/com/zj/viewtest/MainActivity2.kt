package com.zj.viewtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zj.viewtest.partition.base.ComponentDelegate
import com.zj.viewtest.partition.services.api.PartitionApi
import com.zj.viewtest.partition.services.beans.ChannelInfo
import com.zj.nest.NestRecyclerView
import com.zj.viewtest.partition.widget.NestRecyclerAdapter

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_channel_main)

        val ci = ChannelInfo()
        val nestRecyclerView = findViewById<NestRecyclerView>(R.id.channel_main_nrv)

        PartitionApi.getComponentByChannelId(ci.channelId) { isSuccess, data, _ ->
            if (isSuccess && !data.isNullOrEmpty()) {
                val nestAdapter = NestRecyclerAdapter.create(nestRecyclerView)
                nestRecyclerView.adapter = nestAdapter
                val grouped = data.filterNotNull().groupBy { ComponentDelegate.isInfinityComponent(it.componentType) }
                val nestedAble = grouped[true]?.firstOrNull()
                nestAdapter.add(grouped[false])
                nestAdapter.add(nestedAble)
            }
        }
    }
}