package com.zj.viewtest.partition.services.beans


open class BaseSeriesInfo {

    //剧集 ID
    var seriesId: Int = 0

    //剧集名称
    var seriesName: String? = ""

    // 封面
    var thumbnail: String? = ""

    //简介
    var description: String? = ""

    // 剧集数
    var episodes: Int = 0

    //状态 0连载 1完结
    var status: Int = 0

    //分区名
    var partitionName: String? = ""

    //分区id
    var partitionId: Int = 0

    //是否被收藏
    var isFavorite: Boolean? = false

    //热数据
    var hotData: VideoPartitionHotDataInfo? = null

    //频道ID
    var channelId: Int = 0

    //频道名
    var channelName: String = ""

    fun isFinalized(): Boolean {
        return status == 0
    }
}