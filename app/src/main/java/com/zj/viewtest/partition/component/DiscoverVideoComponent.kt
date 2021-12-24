package com.zj.viewtest.partition.component

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.zj.views.list.views.EmptyRecyclerView
import com.zj.viewtest.partition.services.beans.ComponentInfo
import com.zj.viewtest.R
import com.zj.viewtest.partition.services.beans.ComponentContentInfo


/**
 * Author: ZJJ
 * Description: Suitable for short video components that include Enter in Discover
 */
@SuppressLint("ViewConstructor")
class DiscoverVideoComponent(context: Context, override val type: Int) : BaseListVideoComponent(context) {

    private lateinit var tvComponentTitle: TextView
    private lateinit var tvComponentEnter: TextView
    private lateinit var endingLine: View
    private lateinit var rv: EmptyRecyclerView<ComponentContentInfo?>

    override val layoutId: Int; get() = R.layout.component_descover_episode

    override fun getSkeletonCount(contentType: Int): Int {
        return if (contentType == ComponentInfo.CONTENT_TYPE_ORDINARY) 2 else 3
    }

    override fun getBehaviorScrollingView(): RecyclerView {
        return rv
    }

    override fun initView() {
        super.initView()
        tvComponentTitle = findViewById<AppCompatTextView>(R.id.component_discover_episode_title)
        tvComponentEnter = findViewById<AppCompatTextView>(R.id.component_discover_episode_enter)
        rv = findViewById(R.id.component_discover_rv_container)
        endingLine = findViewById(R.id.component_ending_line)
    }

    override fun initData() {
        super.initData()
        tvComponentTitle.text = cInfo?.name
        tvComponentEnter.setOnClickListener {

        }
    }

    override fun hideEndingLine(isHide: Boolean) {
        endingLine.visibility = if (isHide) View.INVISIBLE else View.VISIBLE
    }
}