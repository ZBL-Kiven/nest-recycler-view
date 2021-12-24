package com.zj.viewtest.partition.services.beans

import androidx.annotation.IntRange
import com.zj.viewtest.partition.services.beans.v.BannerConfigBean
import kotlin.random.Random

/**
 * Component configuration information, which represents a corresponding component.
 * */
class ComponentInfo {

    companion object {
        const val CONTENT_TYPE_SHORT = 1
        const val CONTENT_TYPE_ORDINARY = 0
        const val CONTENT_TYPE_DRAMA = 2

        fun createMock(@IntRange(from = 0, to = 4) type: Int, @IntRange(from = 0, to = 2) cType: Int = 0, id: Int): ComponentInfo {
            val mockImg = arrayOf("https://img1.baidu.com/it/u=1035046579,3682814176&fm=15&fmt=auto&gp=0.jpg", "https://img2.baidu.com/it/u=3410596620,2490950622&fm=15&fmt=auto&gp=0.jpg", "https://img1.baidu.com/it/u=1103299328,3966778221&fm=15&fmt=auto&gp=0.jpg")
            return ComponentInfo().apply {
                this.id = id
                partitionId = 112
                channelId = 12
                name = "Mock Test"
                componentType = type
                contentType = cType
                bannerAspectRatio = 3.777f
                showChannelName = true
                bannerItems = arrayListOf()
                repeat(mockImg.size) {
                    bannerItems?.add(BannerConfigBean().apply {
                        this.apiHost = "www.baidu.com"
                        this.bannerImg = mockImg[it]
                        this.jumpUrl = "www.baidu.com"
                    })
                }
            }
        }
    }

    var id = -1

    var partitionId = -1

    //Channel id--the partition component has no channel id
    var channelId = -1

    var name: String? = ""

    /**
     * Component type
     * 0 banner
     * 1 discovery page video component
     * 2 video component
     * 3 infinite paging component
     * 4 infinite paging tab component
     * 5 ranking component
     * */
    var componentType = -1

    /**
     * Resource type
     * 0 ordinary video
     * 1 short drama
     * 2 drama series
     */
    var contentType = -1

    var bannerAspectRatio: Float = 0f

    //banner ,exists if componentType is '0' and  noneNull
    var bannerItems: MutableList<BannerConfigBean?>? = null

    var showChannelName: Boolean = false

}
