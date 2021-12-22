package com.zj.viewtest.partition.component

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zj.views.list.views.EmptyRecyclerView
import com.zj.viewtest.partition.services.beans.ComponentInfo
import com.zj.viewtest.R
import com.zj.viewtest.partition.services.beans.ComponentContentInfo

/**
 * Author: ZJJ
 * Description: 适用于 头部为 SeeMore 的短视频组件
 */
@SuppressLint("ViewConstructor")
class EpisodeSeeMoreComponent(context: Context, override val type: Int) : BaseListVideoComponent(context) {

    private lateinit var endingLine: View
    private lateinit var tvComponentTitle: TextView
    private lateinit var tvComponentSeeMore: TextView
    private lateinit var rv: EmptyRecyclerView<ComponentContentInfo?>

    override val layoutId: Int; get() = R.layout.component_episode

    override fun getSkeletonCount(contentType: Int): Int {
        return if (contentType == ComponentInfo.CONTENT_TYPE_ORDINARY) 4 else 6
    }

    override fun getBehaviorScrollingView(): RecyclerView {
        return rv
    }

    override fun initView() {
        super.initView()
        tvComponentTitle = findViewById(R.id.component_episode_title)
        tvComponentSeeMore = findViewById(R.id.component_episode_see_more)
        rv = findViewById(R.id.component_episode_rv_container)
        endingLine = findViewById(R.id.component_ending_line)
    }

    override fun initData() {
        super.initData()
        tvComponentTitle.text = cInfo?.name
        tvComponentSeeMore.setOnClickListener {

        }
    }

    override fun hideEndingLine(isHide: Boolean) {
        endingLine.visibility = if (isHide) View.INVISIBLE else View.VISIBLE
    }
}