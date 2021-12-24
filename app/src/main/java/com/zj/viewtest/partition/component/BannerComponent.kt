package com.zj.viewtest.partition.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.LifecycleOwner
import com.youth.banner.Banner
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.listener.OnBannerListener
import com.zj.viewtest.partition.base.BaseComponent
import com.zj.views.ut.DPUtils
import com.zj.viewtest.R
import com.zj.viewtest.partition.services.beans.v.BannerConfigBean
import com.zj.viewtest.partition.util.BaseBannerAdapter
import kotlin.math.roundToInt

/**
 * Author: ZJJ
 * Description: 仅限于在 Partition 使用 Banner 的组件
 */
@SuppressLint("ViewConstructor")
class BannerComponent constructor(context: Context, override val type: Int) : BaseComponent<List<BannerConfigBean>>(context), OnBannerListener<BannerConfigBean> {

    companion object {
        var DEf_PADDING = DPUtils.dp2px(18f)
    }

    private var startingWeb: Boolean = false
    private var banner: Banner<BannerConfigBean, BaseBannerAdapter>? = null
    private var bannerAdapter: BaseBannerAdapter? = null

    override fun onResumed() {
        super.onResumed()
        if ((banner?.childCount ?: 0) > 0) {
            banner?.setCurrentItem(0, false)
            banner?.start()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Log.e("------ ", "11111  ==>  ${ev?.action} ")
        return super.dispatchTouchEvent(ev)
    }

    override fun onPaused() {
        banner?.stop()
        super.onPaused()
    }

    override fun onDestroy(soft: Boolean) {
        super.onDestroy(soft)
        if (!soft) banner?.destroy()
    }

    override fun OnBannerClick(data: BannerConfigBean?, position: Int) {
        if (startingWeb) return
    }

    override val layoutId: Int; get() = R.layout.component_banner

    override fun initView() {
        banner = findViewById(R.id.component_banner)
        setPadding(DEf_PADDING, DEf_PADDING, DEf_PADDING, 0)
        banner?.indicator = CircleIndicator(context)
        banner?.setIndicatorSelectedColor(Color.BLACK)
        banner?.setIndicatorNormalColor(Color.GRAY)
        banner?.setIndicatorWidth(DPUtils.dp2px(2f), DPUtils.dp2px(2f))
        banner?.addBannerLifecycleObserver(context as? LifecycleOwner)
    }

    override fun initData() {
        val ratio = cInfo?.bannerAspectRatio ?: 0f
        if (ratio <= 0) return
        val width = context.resources.displayMetrics.widthPixels - DEf_PADDING * 2
        val height = (width / ratio).roundToInt()
        val lp = LayoutParams(width, height)
        banner?.layoutParams = lp
        val bannerData = cInfo?.bannerItems?.filterNotNull()
        if (bannerData.isNullOrEmpty()) return
        setComponentData(bannerData)
    }

    override fun showSkeleton() {}

    override fun onSetData(data: List<BannerConfigBean>?, pl: Any?) {
        if (data.isNullOrEmpty()) return
        banner?.let {
            if (bannerAdapter == null) {
                bannerAdapter = BaseBannerAdapter(data, it.width, it.height)
                it.adapter = bannerAdapter
                banner?.setOnBannerListener(this)
            } else bannerAdapter?.setDatas(data)
        }
        post {
            banner?.start()
        }
    }
}