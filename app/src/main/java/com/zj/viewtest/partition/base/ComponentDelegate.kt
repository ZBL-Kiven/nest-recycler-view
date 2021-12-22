package com.zj.viewtest.partition.base

import android.content.Context
import com.zj.viewtest.partition.component.*

object ComponentDelegate {

    /**
     * 0 banner
     * 1 discovery page video component
     * 2 video component
     * 3 infinite paging component
     * 4 infinite paging tab component
     **/
    fun getComponentWithType(context: Context, type: Int): BaseComponent<*>? {
        return when (type) {
            0 -> BannerComponent(context, type)
            1 -> DiscoverVideoComponent(context, type)
            2 -> EpisodeSeeMoreComponent(context, type)
            3 -> InfinityComponent(context, type)
            4 -> TabComponent(context, type)
            else -> null
        }
    }

    fun validType(type: Int): Boolean {
        return type in 0..4
    }

    fun isInfinityComponent(type: Int): Boolean {
        return type == 3 || type == 4
    }
}