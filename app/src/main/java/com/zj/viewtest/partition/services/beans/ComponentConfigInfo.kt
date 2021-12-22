package com.zj.viewtest.partition.services.beans

class ComponentConfigDataInfo {

    var isShowChannelName: Boolean = false

    companion object {
        fun make(showChannelName: Boolean): ComponentConfigDataInfo {
            return ComponentConfigDataInfo().apply {
                isShowChannelName = showChannelName
            }
        }
    }

}