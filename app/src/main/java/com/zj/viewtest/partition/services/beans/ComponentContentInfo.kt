package com.zj.viewtest.partition.services.beans

import com.zj.viewtest.partition.services.beans.skeleton.SkeletonAble
import com.zj.viewtest.partition.services.beans.v.VideoSource


class ComponentContentInfo : SkeletonAble {

    //Short video
    var videoInfo: VideoSource? = null

    //Episode Introduction
    var seriesRespEn: BaseSeriesInfo? = null

    //0 Ordinary video 1 Short drama collection data 2 Drama collection data
    var dataType: Int = 0

    var inSkeletonShow: Boolean = false

    fun createSkeleton(dataType: Int): ComponentContentInfo {
        inSkeletonShow = true
        this.dataType = dataType
        videoInfo = VideoSource()
        seriesRespEn = BaseSeriesInfo()
        return this
    }

    override fun isInSkeletonShow(): Boolean {
        return inSkeletonShow
    }
}
