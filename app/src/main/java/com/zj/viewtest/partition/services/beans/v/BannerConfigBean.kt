package com.zj.viewtest.partition.services.beans.v

import com.zj.viewtest.partition.services.beans.skeleton.SkeletonAble

class BannerConfigBean : SkeletonAble {

    /**
     * Host annotation used for internal jump WebView injection
     * */
    var apiHost: String? = ""

    /**
     * banner picture
     */
    var bannerImg: String? = ""

    /**
     * Jump address Internal jump to app schema
     */
    var jumpUrl: String? = ""

    var defaultResId: Int = 0

    override fun isInSkeletonShow(): Boolean {
        return false
    }
}