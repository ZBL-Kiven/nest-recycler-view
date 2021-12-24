package com.zj.viewtest.partition.services.beans


open class BaseSeriesInfo {

    companion object {

        fun createMock(type: Int): BaseSeriesInfo {
            return BaseSeriesInfo().apply {
                this.seriesId = 0
                this.seriesName = ""
                this.thumbnail = ""
                this.description = "Just test for NestRecyclerView"
                this.episodes = 0
                this.status = 0
                this.partitionName = "Some data's title"
                this.partitionId = 0
                this.isFavorite = false
                this.hotData = null
                this.channelId = 0
                this.channelName = ""
            }
        }
    }

    var seriesId: Int = 0
    var seriesName: String? = ""
    var thumbnail: String? = ""
    var description: String? = ""
    var episodes: Int = 0
    var status: Int = 0
    var partitionName: String? = ""
    var partitionId: Int = 0
    var isFavorite: Boolean? = false
    var hotData: VideoPartitionHotDataInfo? = null
    var channelId: Int = 0
    var channelName: String = ""

    fun isFinalized(): Boolean {
        return status == 0
    }
}